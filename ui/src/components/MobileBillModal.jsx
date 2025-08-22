import React from "react";
import { Dialog, DialogTrigger, DialogContent } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import BillPreview from "@/components/BillPreview";

const MobileBillModal = ({ 
  cartItems, 
  handleUpdateQty, 
  openCustomizeDialog,
  onOpenCustomerDialog,
  handleCopyItem,
  isDialog = true
}) => {
  if (isDialog) {
    return (
      <Dialog>
        <DialogTrigger asChild>
          <Button className="fixed bottom-4 right-4 text-white rounded-full p-4 shadow-lg z-50">
            🧾
          </Button>
        </DialogTrigger>
        <DialogContent className="w-11/12 max-w-md h-[90vh] p-0 flex flex-col">
          <BillPreview
            cartItems={cartItems}
            handleUpdateQty={handleUpdateQty}
            openCustomizeDialog={openCustomizeDialog}
            onOpenCustomerDialog={onOpenCustomerDialog}
            handleCopyItem={handleCopyItem}
            isDialog={true}
          />
        </DialogContent>
      </Dialog>
    );
  }

  return null;
};

export default MobileBillModal;
