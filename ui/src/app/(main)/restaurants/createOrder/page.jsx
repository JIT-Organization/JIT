"use client";
import React, { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { getMenuItemListOptions } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import FoodCard from "@/components/customUIComponents/FoodCard";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import { Card, CardContent, CardTitle } from "@/components/ui/card";
import BillPreview from "./BillPreview";
import { useIsMobile } from "@/hooks/use-mobile";
import CustomizeDialog from "@/components/customUIComponents/CustomizeDialog";

const CreateOrder = () => {
  const router = useRouter();
  const isMobile = useIsMobile();
  const [showPopup, setShowPopup] = useState(false);

  const { data: menuItems = [], isLoading, error } = useQuery(getMenuItemListOptions());
  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");
  const [cartItems, setCartItems] = useState([]);

  const filteredMenuItems = useMemo(() => {
    return menuItems.filter((item) => {
      const categoryList = item?.categorySet ?? [];
      const matchesCategory = activeCategory === "All" || categoryList.includes(activeCategory);
      const matchesSearch = (item?.menuItemName ?? "")
        .toLowerCase()
        .includes(globalFilter.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [menuItems, globalFilter, activeCategory]);

  const handleAddToCart = (food) => {
    setCartItems((prevCart) => {
      const index = prevCart.findIndex((item) => item.id === food.id);
      if (index === -1) {
        return [...prevCart, { ...food, qty: 1 }];
      } return [...prevCart];
    });
  };

  const [customizeItemData, setCustomizeItemData] = useState(null);
  const [showCustomizeDialog, setShowCustomizeDialog] = useState(false);
  
  const openCustomizeDialog = (id) => {
    setCustomizeItemData(cartItems.find((x)=> x.id == id));
    setShowCustomizeDialog(true);
  };
  const closeCustomizeDialog = () => {
    setShowCustomizeDialog(false)
  }
  const handleSaveCustomizeDialog = (id, notes) => {
    setCartItems((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, customNotes: notes } : item
      )
    );
    setShowCustomizeDialog(false);
  };

  const getCartQuantityById = (id) => {
    return cartItems ? cartItems.find((item) => item.id === id)?.qty || 0 : 0;
  };

  const handleUpdateQty = (id, type) => {
    setCartItems((prevCart) => {
      return prevCart
        .map((item) => {
          if (item.id === id) {
            if (type === "increment") {
              return { ...item, qty: item.qty + 1 };
            } else if (type === "decrement") {
              return { ...item, qty: item.qty - 1 };
            } else if (type === "remove") {
              return { ...item, qty: 0}
            }
          }
          return item;
        })
        .filter((item) => item.qty > 0);
    });
  };
  const categories = getDistinctCategories(menuItems);

  if (isLoading) return <p>Loading menu...</p>;
  if (error) return <p>Error loading menu: {error.message}</p>;

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 shadow">
        <DataTableHeader
          tabName="Create Order"
          globalFilter={globalFilter}
          setGlobalFilter={setGlobalFilter}
          categories={categories}
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
          setColumnFilters={() => {}}
          headerButtonName="Place Order"
          headerButtonClick={() => console.log(cartItems)}
        />
      </CardTitle>

      <CardContent className="mt-0 px-2 py-0">
        <div className="flex flex-1 overflow-hidden" style={{ height: 'calc(100vh - 191px)' }}>
          <div className="flex-1 overflow-y-auto p-0 pb-4">
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3">
              {filteredMenuItems.map((food) => (
                <div
                  key={food.id}
                  onClick={() =>
                    handleAddToCart({
                      id: food.id,
                      name: food.menuItemName,
                      price: food.price,
                    })
                  }
                className="cursor-pointer w-full"
                >
                  <FoodCard food={food} key={food.id} 
                  handleUpdateQty={handleUpdateQty} quantity={getCartQuantityById(food.id)} openCustomizeDialog={openCustomizeDialog}/>
                </div>
              ))}
            </div>
          </div>

          <div className="hidden lg:block w-[300px] border-l px-2x py-0 sticky top-0 overflow-y-auto">
            <BillPreview cartItems={cartItems} handleUpdateQty={handleUpdateQty} openCustomizeDialog={openCustomizeDialog} />
          </div>
        </div>
      </CardContent>

      {isMobile && (
        <>
          <button
            onClick={() => setShowPopup(true)}
            className="fixed bottom-4 right-4 bg-blue-600 text-white p-3 rounded-full shadow-lg z-50"
          >
            🧾
          </button>

          {showPopup && (
            <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
              <div className="bg-white w-11/12 max-w-md max-h-[90vh] overflow-y-auto rounded-lg p-4 relative flex flex-col">
                <button
                  className="text-red-500 absolute top-2 right-2 text-xl"
                  onClick={() => setShowPopup(false)}
                >
                  ❌
                </button>
                <div className="flex justify-center">
                <BillPreview cartItems={cartItems} handleUpdateQty={handleUpdateQty}  openCustomizeDialog={openCustomizeDialog} />
                </div>
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
