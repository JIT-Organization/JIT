import { useState } from "react";

export const useCustomizeDialog = (cartItems, updateCartItemNotes) => {
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
    updateCartItemNotes(updatedItems);
    setShowCustomizeDialog(false);
  };

  return {
    customizeItems,
    showCustomizeDialog,
    openCustomizeDialog,
    closeCustomizeDialog,
    handleSaveCustomizeDialog,
  };
};
