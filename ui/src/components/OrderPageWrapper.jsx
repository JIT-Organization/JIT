import React, { useState, useMemo, useEffect, useRef } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { useToast } from "@/hooks/use-toast";
import { Card, CardContent, CardTitle } from "@/components/ui/card";
import { useIsMobile } from "@/hooks/use-mobile";
import { getDistinctCategories } from "@/lib/utils/helper";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import BillPreview from "@/components/BillPreview";
import MenuGrid from "@/components/MenuGrid";
import MobileBillModal from "@/components/MobileBillModal";
import FoodCustomizeStepperDialog from "@/components/customUIComponents/FoodCustomizeStepperDialog";
import CommonForm from "@/components/CommonForm";
import { useCart } from "@/hooks/useCart";
import { useCustomizeDialog } from "@/hooks/useCustomizeDialog";
import { getTablesListOptions, createOrder, updateOrder, getOrderDetails } from "@/lib/api/api";

const customerFormSchema = z.object({
  tables: z.array(z.string()),
  customerName: z.string().optional(),
  customerNumber: z.string().optional(),
  takeaway: z.boolean().default(false),
}).superRefine((data, ctx) => {
  if (!data.takeaway && (!data.tables || data.tables.length === 0)) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: "Please select at least one table unless it's takeaway.",
      path: ["tables"],
    });
  }
});

const OrderPageWrapper = ({
  mode = "create", // "create" or "edit"
  queryOptions,
  orderNumber = null, // Only used in edit mode
  customOnSubmit = null, // Allow custom submit handler
}) => {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const isMobile = useIsMobile();

  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [isCustomerDialogOpen, setIsCustomerDialogOpen] = useState(false);

  const isNewOrder = mode === "create";
  const isEditOrder = mode === "edit";
  const currentOrderNumber = orderNumber;

  // Form setup
  const form = useForm({
    resolver: zodResolver(customerFormSchema),
    defaultValues: {
      tables: [],
      customerName: "",
      customerNumber: "",
      takeaway: false,
    },
  });

  // Open customer dialog for new orders
  useEffect(() => {
    if (isNewOrder) {
      setIsCustomerDialogOpen(true);
    }
  }, [isNewOrder]);

  // Queries
  const {
    data: menuItems = [],
    isLoading: queryLoading,
    error: queryError,
  } = useQuery(queryOptions);

  const { data: tablesList = [], isLoading: isTablesLoading } = useQuery(
    getTablesListOptions("TGSR")
  );

  // Fetch order details in edit mode
  const {
    data: orderDetails = null,
    isLoading: isOrderLoading,
    error: orderError
  } = useQuery(getOrderDetails(isEditOrder ? currentOrderNumber : null));


  // Mutations
  const createOrderMutation = useMutation({
    ...createOrder(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Order created successfully",
      });
      form.reset();
      // router.push("/restaurants/orders");
    },
  });

  const updateOrderMutation = useMutation({
    ...updateOrder(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Order updated successfully",
      });
      router.push("/restaurants/orders");
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update order",
        variant: "destructive",
      });
    },
  });

  // Cart hooks
  const {
    cartItems,
    handleAddToCart,
    getCartQuantityByName,
    handleUpdateQty,
    handleAddAgain,
    updateCartItemNotes,
    setCartItems,
  } = useCart();

  const {
    customizeItems,
    showCustomizeDialog,
    openCustomizeDialog,
    closeCustomizeDialog,
    handleSaveCustomizeDialog,
  } = useCustomizeDialog(cartItems, updateCartItemNotes);

  // Initialize cart and form with order details in edit mode
  useEffect(() => {
    if (isEditOrder && orderDetails) {
      // Transform order items to cart items format
      const cartItemsFromOrder = orderDetails.orderItems?.map(item => ({
        menuItemName: item.menuItemName,
        itemName: item.itemName,
        foodType: item.foodType,
        itemPrice: item.price,
        qty: item.quantity,
        selectedAddOns: item.selectedAddOns || [],
        customNotes: item.customNotes || "",
        totalPrice: item.totalPrice,
      })) || [];

      // Set cart items
      setCartItems(cartItemsFromOrder);

      // Update form with customer details
      form.reset({
        tables: orderDetails.diningTables || [],
        customerName: orderDetails.orderedBy || "",
        customerNumber: orderDetails.orderedNumber || "",
        takeaway: orderDetails.takeAway || false,
      });
    }
  }, [isEditOrder, orderDetails, form, setCartItems]);

  // Open customer dialog for new orders
  useEffect(() => {
    if (isNewOrder) {
      setIsCustomerDialogOpen(true);
    }
  }, [isNewOrder]);

  // Computed values
  const filteredMenuItems = useMemo(() => {
    return menuItems.filter((item) => {
      const categoryList = item?.categorySet ?? [];
      const matchesCategory =
        activeCategory === "All" || categoryList.includes(activeCategory);
      const matchesSearch = (item?.menuItemName ?? "")
        .toLowerCase()
        .includes(globalFilter.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [menuItems, globalFilter, activeCategory]);

  const categories = getDistinctCategories(menuItems);
  const tableOptions = (tablesList || []).map((t) => ({
    label: t.tableNumber,
    value: t.tableNumber
  }));
  // Form fields configuration
  const orderFormFields = [
    {
      name: "takeaway",
      label: "Takeaway",
      type: "toggleGroup",
      options: ["yes", "no"],
      fieldCol: "col-span-12",
      labelCol: "col-span-3",
      controlCol: "col-span-9",
    },
    {
      name: "tables",
      label: "Tables",
      type: "multiSelect",
      options: tableOptions,
      placeholder: isTablesLoading ? "Loading tables..." : "Select tables...",
      disabled: form.watch("takeaway"),
      fieldCol: "col-span-12",
      labelCol: "col-span-3",
      controlCol: "col-span-9",
      rules: {
        validate: (value) => {
          if (!form.watch("takeaway") && (!value || value.length === 0)) {
            return "Please select at least one table unless it's takeaway.";
          }
          return true;
        },
      },
    },
    {
      name: "customerName",
      label: "Customer Name",
      type: "input",
      placeholder: "Enter customer name...",
      fieldCol: "col-span-12",
      labelCol: "col-span-3",
      controlCol: "col-span-9",
    },
    {
      name: "customerNumber",
      label: "Customer Number",
      type: "input",
      inputType: "number",
      placeholder: "Enter customer number...",
      fieldCol: "col-span-12",
      labelCol: "col-span-3",
      controlCol: "col-span-9",
      rules: {
        validate: (value) => {
          if (value && isNaN(Number(value))) {
            return "Customer number must be a valid mobile number.";
          }
          return true;
        },
      },
    },
  ];

  // Order submission handler
  const handlePlaceOrder = async () => {
    if (customOnSubmit) {
      customOnSubmit(cartItems);
      return;
    }

    if (cartItems.length === 0) {
      toast({
        title: "Validation Error",
        description: "Please select at least one food item to place order",
        variant: "destructive",
      });
      return;
    }

    const isValid = await form.trigger();
    if (!isValid) {
      setIsCustomerDialogOpen(true);
      return;
    }

    const customerData = form.getValues();

    const orderItemsDTO = cartItems.map(item => ({
      foodType: item.foodType,
      menuItemName: item.menuItemName,
      itemName: item.itemName,
      quantity: item.qty,
      price: item.itemPrice,
      totalPrice: item.qty * item.itemPrice,
      selectedAddOns: item.selectedAddOns || [],
      customNotes: item.customNotes || "",
    }));

    const totalAmount = cartItems.reduce((sum, item) => sum + (item.itemPrice * item.qty), 0);

    const orderDTO = {
      amount: totalAmount,
      orderItems: orderItemsDTO,
      diningTables: customerData.tables,
      orderedBy: customerData.customerName,
      orderedNumber: customerData.customerNumber,
      takeAway: customerData.takeaway
    };

    if (isNewOrder) {
      createOrderMutation.mutate({ orderDTO });
    } else {
      updateOrderMutation.mutate({
        orderNumber: currentOrderNumber,
        ...orderDTO
      });
    }
  };

  const isLoading = queryLoading || (isEditOrder && isOrderLoading);
  const error = queryError || orderError;
  const isMutationPending = createOrderMutation.isPending || updateOrderMutation.isPending;
  const formRef = useRef(null);

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Error loading data: {error.message}</p>;

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 shadow bg-white">
        <DataTableHeader
          tabName={isNewOrder ? "Create Order" : "Edit Order"}
          globalFilter={globalFilter}
          setGlobalFilter={setGlobalFilter}
          categories={categories}
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
          setColumnFilters={() => { }}
          headerButtonName={isNewOrder ? "Place Order" : "Update Order"}
          headerButtonClick={handlePlaceOrder}
          disabled={isMutationPending}
        />
      </CardTitle>

      <CardContent className="mt-0 px-2 py-0">
        <div
          className="flex flex-1 overflow-hidden"
          style={{ height: "calc(100vh - 191px)" }}
        >
          <div className="flex-1 overflow-y-auto p-0 pb-4">
            <MenuGrid
              filteredMenuItems={filteredMenuItems}
              handleAddToCart={handleAddToCart}
              handleUpdateQty={handleUpdateQty}
              getCartQuantityByName={getCartQuantityByName}
              openCustomizeDialog={openCustomizeDialog}
              handleAddAgain={handleAddAgain}
              cartItems={cartItems}
            />
          </div>

          <div className="hidden lg:block w-[300px] border-l px-2 py-0">
            <BillPreview
              cartItems={cartItems}
              handleUpdateQty={handleUpdateQty}
              openCustomizeDialog={openCustomizeDialog}
              onOpenCustomerDialog={() => setIsCustomerDialogOpen(true)}
              handleCopyItem={handleAddAgain}
            />
          </div>
        </div>
      </CardContent>

      {isMobile && (
        <MobileBillModal
          cartItems={cartItems}
          handleUpdateQty={handleUpdateQty}
          openCustomizeDialog={openCustomizeDialog}
          onOpenCustomerDialog={() => setIsCustomerDialogOpen(true)}
          handleCopyItem={handleAddAgain}
        />
      )}

      <FoodCustomizeStepperDialog
        isOpen={showCustomizeDialog}
        onSave={handleSaveCustomizeDialog}
        items={customizeItems}
        onClose={closeCustomizeDialog}
      />

      <Dialog open={isCustomerDialogOpen} onOpenChange={setIsCustomerDialogOpen}>
        <DialogContent>
          <DialogTitle className="col-span-12 block w-full text-center">
            Customer Details
          </DialogTitle>
          <CommonForm form={form} formFields={orderFormFields} formRef={formRef} />
        </DialogContent>
      </Dialog>
    </Card>
  );
};

export default OrderPageWrapper;
