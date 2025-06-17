"use client";
import React, { useState, useMemo, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter, useParams } from "next/navigation";

import { getMenuItemsListForOrder, saveOrder, updateOrder } from "@/lib/api/api";
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

const CreateOrder = ({ isNew = true }) => {
  const router = useRouter();
  const { orderNumber } = useParams();
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const isMobile = useIsMobile();
  const [showPopup, setShowPopup] = useState(false);

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
          price: food.offerPrice || food.price,
          qty: 1
        }];
      }
      return [...prevCart];
    });
  };

  const [customizeItemData, setCustomizeItemData] = useState(null);
  const [showCustomizeDialog, setShowCustomizeDialog] = useState(false);

  const openCustomizeDialog = (itemName) => {
    setCustomizeItemData(cartItems.find((x) => x.itemName === itemName));
    setShowCustomizeDialog(true);
  };

  const closeCustomizeDialog = () => {
    setShowCustomizeDialog(false);
  };

  const handleSaveCustomizeDialog = (itemName, notes) => {
    setCartItems((prev) =>
      prev.map((item) =>
        item.itemName === itemName ? { ...item, customNotes: notes } : item
      )
    );
    setShowCustomizeDialog(false);
  };

  const getCartQuantityById = (itemName) => {
    return cartItems ? cartItems.find((item) => item.itemName === itemName)?.qty || 0 : 0;
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

  const handlePlaceOrder = () => {
    if (cartItems.length === 0) {
      toast({
        title: "Validation Error",
        description: "Please select at least one food item to place order",
        variant: "destructive",
      });
      return;
    }

    const orderItemsDTO = cartItems.map(item => ({
      ...item,
      itemName: item.itemName,
      quantity: item.qty
    }));

    const totalAmount = cartItems.reduce((sum, item) => sum + (item.price * item.qty), 0);

    const orderDTO = {
      amount: totalAmount,
      orderItemsDTO
    };

    if (isNew) {
      createOrderMutation.mutate(orderDTO);
    } else {
      updateOrderMutation.mutate({
        orderNumber,
        ...orderDTO
      });
    }
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
                    quantity={getCartQuantityById(food.menuItemName)}
                    mode='create'
                    openCustomizeDialog={openCustomizeDialog}
                  />
                </div>
              ))}
            </div>
          </div>

          <div className="hidden lg:block w-[300px] border-l px-2 py-0 sticky top-0 overflow-y-auto">
            <BillPreview
              cartItems={cartItems}
              handleUpdateQty={handleUpdateQty}
              openCustomizeDialog={openCustomizeDialog}
            />
          </div>
        </div>
      </CardContent>

      {isMobile && (
        <>
          <Button
            onClick={() => setShowPopup(true)}
            className="fixed bottom-4 right-4 bg-blue-600 text-white rounded-full p-4 shadow-lg z-50"
          >
            🧾
          </Button>

          {showPopup && (
            <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
              <div className="bg-white w-11/12 max-w-md max-h-[90vh] rounded-lg p-4 relative flex flex-col">
                <Button
                  variant="ghost"
                  className="absolute top-2 right-2 text-red-500 text-xl"
                  onClick={() => setShowPopup(false)}
                >
                  ❌
                </Button>
                <ScrollArea className="flex justify-center">
                  <BillPreview
                    cartItems={cartItems}
                    handleUpdateQty={handleUpdateQty}
                    openCustomizeDialog={openCustomizeDialog}
                  />
                </ScrollArea>
              </div>
            </div>
          )}
        </>
      )}

      <CustomizeDialog
        isOpen={showCustomizeDialog}
        onSave={handleSaveCustomizeDialog}
        item={customizeItemData}
        onClose={closeCustomizeDialog}
      />
    </Card>
  );
};

export default CreateOrder;
