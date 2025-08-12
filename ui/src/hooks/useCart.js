import { useState } from "react";

const isItemEditable = (status) => {
  return !status || status === "UNASSIGNED" || status === "ASSIGNED";
};

export const useCart = () => {
  const [cartItems, setCartItems] = useState([]);

  const handleAddToCart = (food) => {
    setCartItems((prevCart) => {
      const index = prevCart.findIndex((item) => item.itemName === food.menuItemName);
      if (index === -1) {
        return [...prevCart, {
          ...food,
          itemName: food.menuItemName,
          itemPrice: food.offerPrice || food.price,
          qty: 1,
          status: food.status || "UNASSIGNED"
        }];
      }
      return [...prevCart];
    });
  };

  const getCartQuantityByName = (itemName) => {
    if (!cartItems) return 0;
    if (!itemName) return 0;
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
            // Check if item is editable based on status
            const itemEditable = isItemEditable(item.status);
            
            if (!itemEditable && type !== "remove") {
              // Don't allow quantity changes for non-editable items (except remove)
              return item;
            }
            
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
          status: "UNASSIGNED" // New items always start as UNASSIGNED
        }
      ];
    });
  };

  const updateCartItemNotes = (updatedItems) => {
    setCartItems(prev =>
      prev.map(item => {
        const updated = updatedItems.find(u => u.itemName === item.itemName);
        return updated ? { ...item, customNotes: updated.customNotes } : item;
      })
    );
  };

  return {
    cartItems,
    setCartItems,
    handleAddToCart,
    getCartQuantityByName,
    handleUpdateQty,
    handleAddAgain,
    updateCartItemNotes,
    isItemEditable, // Export the utility function
  };
};
