"use client";
import React, { useState, useMemo, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter, useParams } from "next/navigation";
import { Dialog, DialogTrigger, DialogContent, DialogTitle, DialogDescription } from "@/components/ui/dialog";

import { getMenuItemsListForOrder, saveOrder, updateOrder, getTablesListOptions } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import FoodCard from "@/components/customUIComponents/FoodCard";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import BillPreview from "../../../../components/BillPreview";
import { useIsMobile } from "@/hooks/use-mobile";
import CustomizeDialog from "@/components/customUIComponents/CustomizeDialog";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";
import { useToast } from "@/hooks/use-toast";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import CommonForm from "@/components/CommonForm";
import FoodCustomizeStepperDialog from "@/components/customUIComponents/FoodCustomizeStepperDialog";

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

const CreateOrder = ({ isNew = true }) => {
  const router = useRouter();
  const { orderNumber } = useParams();
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const isMobile = useIsMobile();
  const [showPopup, setShowPopup] = useState(false);
  const [isCustomerDialogOpen, setIsCustomerDialogOpen] = useState(false);

  const form = useForm({
    resolver: zodResolver(customerFormSchema),
    defaultValues: {
      tables: [],
      customerName: "",
      customerNumber: "",
      takeaway: false,
    },
  });

  useEffect(() => {
    if (isNew) {
      setIsCustomerDialogOpen(true);
    }
  }, [isNew]);

  const {
    data: menuItems = [],
    isLoading: isMenuLoading,
    error: menuError,
  } = useQuery(getMenuItemsListForOrder());

  // const {
  //   data: existingOrder,
  //   isLoading: isOrderLoading,
  //   error: orderError,
  // } = useQuery({
  //   ...getOrderbyNumber(orderNumber),
  //   enabled: !isNew && !!orderNumber,
  // });

  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [cartItems, setCartItems] = useState([]);

  // useEffect(() => {
  //   if (!isNew && existingOrder) {
  //     const transformedItems = existingOrder.items.map(item => ({
  //       ...item,
  //       itemName: item.menuItemName,
  //       price: item.offerPrice || item.price,
  //       qty: item.quantity
  //     }));
  //     setCartItems(transformedItems);
  //   }
  // }, [isNew, existingOrder]);

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

  const handleAddToCart = (food) => {
    setCartItems((prevCart) => {
      const index = prevCart.findIndex((item) => item.itemName === food.menuItemName);
      if (index === -1) {
        return [...prevCart, {
          ...food,
          itemName: food.menuItemName,
          itemPrice: food.offerPrice || food.price,
          qty: 1
        }];
      }
      return [...prevCart];
    });
  };

  const [customizeItems, setCustomizeItems] = useState([]);
  const [showCustomizeDialog, setShowCustomizeDialog] = useState(false);

  const openCustomizeDialog = (itemName, getAll = false) => {
    if (getAll) {
      const baseName = itemName.split('#')[0];
      const regex = new RegExp(`^${baseName}(#\\d+)?$`);
      setCustomizeItems(cartItems.filter(x => regex.test(x.itemName)));
    } else {
      setCustomizeItems(cartItems.filter(x => x.itemName === itemName));
    }
    setShowCustomizeDialog(true);
  };

  const closeCustomizeDialog = () => {
    setShowCustomizeDialog(false);
  };

  const handleSaveCustomizeDialog = (updatedItems) => {
    setCartItems(prev =>
      prev.map(item => {
        const updated = updatedItems.find(u => u.itemName === item.itemName);
        return updated ? { ...item, customNotes: updated.customNotes } : item;
      })
    );
    setShowCustomizeDialog(false);
  };

  const getCartQuantityByName = (itemName) => {
    if (!cartItems) return 0;
    const baseName = itemName.split('#')[0];
    const regex = new RegExp(`^${baseName}(#\\d+)?$`);
    return cartItems
      .filter(item => regex.test(item.itemName))
      .reduce((sum, item) => sum + (item.qty || 0), 0);
  };

  const handleUpdateQty = (itemName, type) => {
    setCartItems((prevCart) =>
      prevCart
        .map((item) => {
          if (item.itemName === itemName) {
            if (type === "increment") {
              return { ...item, qty: item.qty + 1 };
            } else if (type === "decrement") {
              return { ...item, qty: item.qty - 1 };
            } else if (type === "remove") {
              return { ...item, qty: 0 };
            }
          }
          return item;
        })
        .filter((item) => item.qty > 0)
    );
  };

  const categories = getDistinctCategories(menuItems);

  const createOrderMutation = useMutation({
    mutationFn: (data) => saveOrder(data),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Order created successfully",
      });
      router.back();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create order",
        variant: "destructive",
      });
    },
  });

  const updateOrderMutation = useMutation({
    mutationFn: (data) => updateOrder(data),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Order updated successfully",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update order",
        variant: "destructive",
      });
    },
  });

  const handlePlaceOrder = async () => {
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
      ...item,
      itemName: item.itemName,
      quantity: item.qty,
      totalPrice: item.qty * item.itemPrice
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
console.log(orderDTO)
    if (isNew) {
      createOrderMutation.mutate(orderDTO);
    } else {
      updateOrderMutation.mutate({
        orderNumber,
        ...orderDTO
      });
    }
  };

  const { data: tablesList = [], isLoading: isTablesLoading } = useQuery(getTablesListOptions("TGSR"));
  const tableOptions = (tablesList || []).map((t) => ({ label: t.tableNumber, value: t.tableNumber }));

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

  const handleAddAgain = (food) => {
    setCartItems((prevCart) => {
      const baseName = food.menuItemName;
      const regex = new RegExp(`^${baseName}(#\\d+)?$`);
      const suffixes = prevCart
        .filter(item => regex.test(item.itemName))
        .map(item => {
          const match = item.itemName.match(/^.+#(\d+)$/);
          return match ? parseInt(match[1], 10) : 0;
        });
      const maxSuffix = suffixes.length > 0 ? Math.max(...suffixes) : 0;
      const uniqueName = maxSuffix === 0
        ? `${baseName}#1`
        : `${baseName}#${maxSuffix + 1}`;
      return [
        ...prevCart,
        {
          ...food,
          itemName: uniqueName,
          itemPrice: food.offerPrice || food.price,
          qty: 1,
        }
      ];
    });
  };

  if (isMenuLoading) {
    return <LoadingState message="Loading menu items..." />;
  }

  if (menuError || (!isNew && orderError)) {
    return (
      <ErrorState
        title="Error loading data"
        message={menuError?.message || orderError?.message}
        action={
          <Button onClick={() => router.refresh()} className="mt-4">
            Try Again
          </Button>
        }
      />
    );
  }

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 shadow bg-white">
        <DataTableHeader
          tabName={isNew ? "Create Order" : "Edit Order"}
          globalFilter={globalFilter}
          setGlobalFilter={setGlobalFilter}
          categories={categories}
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
          setColumnFilters={() => {}}
          headerButtonName="Place Order"
          headerButtonClick={handlePlaceOrder}
          disabled={isNew ? createOrderMutation.isPending : updateOrderMutation.isPending}
        />
      </CardTitle>

      <CardContent className="mt-0 px-2 py-0">
        <div
          className="flex flex-1 overflow-hidden"
          style={{ height: "calc(100vh - 191px)" }}
        >
          <div className="flex-1 overflow-y-auto p-0 pb-4">
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3">
              {filteredMenuItems.map((food) => (
                <div
                  key={food.menuItemName}
                  onClick={() =>
                    handleAddToCart(food)
                  }
                  className="cursor-pointer w-full"
                >
                  <FoodCard
                    food={food}
                    handleUpdateQty={handleUpdateQty}
                    quantity={getCartQuantityByName(food.menuItemName)}
                    mode='create'
                    openCustomizeDialog={openCustomizeDialog}
                    onAddAgain={handleAddAgain}
                  />
                </div>
              ))}
            </div>
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
        <Dialog>
          <DialogTrigger asChild>
            <Button
              className="fixed bottom-4 right-4 text-white rounded-full p-4 shadow-lg z-50"
            >
              🧾
            </Button>
          </DialogTrigger>
          <DialogContent className="w-11/12 max-w-md h-[90vh] p-0 flex flex-col">
            <BillPreview
                cartItems={cartItems}
                handleUpdateQty={handleUpdateQty}
                openCustomizeDialog={openCustomizeDialog}
                onOpenCustomerDialog={() => setIsCustomerDialogOpen(true)}
                isDialog={true}
              />
          </DialogContent>
        </Dialog>
      )}

      <Dialog open={isCustomerDialogOpen} onOpenChange={setIsCustomerDialogOpen}>
        <DialogContent>
          <DialogTitle className="col-span-12 block w-full text-center">Customer Details</DialogTitle>
          <CommonForm form={form} formFields={orderFormFields} />
        </DialogContent>
      </Dialog>

      <FoodCustomizeStepperDialog
        isOpen={showCustomizeDialog}
        onSave={handleSaveCustomizeDialog}
        items={customizeItems}
        onClose={closeCustomizeDialog}
      />
    </Card>
  );
};

export default CreateOrder;
