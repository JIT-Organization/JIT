import React from "react";
import FoodCard from "@/components/customUIComponents/FoodCard";

const MenuGrid = ({ 
  filteredMenuItems, 
  handleAddToCart, 
  handleUpdateQty, 
  getCartQuantityByName, 
  openCustomizeDialog,
  handleAddAgain,
  cartItems = [],
  mode = "create" 
}) => {
  // Function to get item status from cart
  const getItemStatus = (menuItemName) => {
    const cartItem = cartItems.find(item => {
      const baseName = item.itemName.split('#')[0];
      return baseName === menuItemName;
    });
    return cartItem?.status || "";
  };

  return (
    <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-3">
      {filteredMenuItems.map((food) => (
        <div
          key={food.menuItemName}
          onClick={() => handleAddToCart(food)}
          className="cursor-pointer w-full"
        >
          <FoodCard
            food={food}
            mode={mode}
            handleUpdateQty={handleUpdateQty}
            quantity={getCartQuantityByName(food.menuItemName)}
            openCustomizeDialog={openCustomizeDialog}
            onAddAgain={handleAddAgain}
            status={getItemStatus(food.menuItemName)}
          />
        </div>
      ))}
    </div>
  );
};

export default MenuGrid;
