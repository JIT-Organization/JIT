import { useEffect, useState } from "react";
import UpiQrGenerator from "./upiQRGenerator";

import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogDescription, 
  DialogFooter 
} from "./ui/dialog"; 
import { Button } from "./ui/button";

export  function PaymentPopup({popupData:popupData,setPopupData:setPopupData, isOpen : isOpen, setIsOpen : setIsOpen, isPopupDataUpdated: isPopupDataUpdated}) {
  const [isExpanded, setIsExpanded] = useState(false);

  const handleCashMethod = () =>{
    setPopupData({ ...popupData, payment: "paid" });
    isPopupDataUpdated.current = true;
  }
  return (
    <Dialog open={isOpen} onOpenChange={(open) => {
      setIsOpen(open);
      if (!open) setIsExpanded(false); // Reset expanded state on close
    }}>
      <DialogContent className="max-w-lg">
        <DialogHeader className="flex justify-between items-center">
          {isExpanded ? (
             <Button variant="ghost" onClick={() => setIsExpanded(false)} className="absolute top-4 left-4">
              ← 
            </Button>
          ) : (
            <div /> // Placeholder for spacing consistency
          )}
        </DialogHeader>

        {!isExpanded ? (
          <div className="flex flex-col items-center">
            <DialogTitle>Choose Payment</DialogTitle>
            <DialogDescription>Select a payment method below.</DialogDescription>
            <div className="flex gap-4 mt-4">
              <Button
                className="px-6 py-2 bg-gradient-to-r from-green-500 to-green-700 text-white font-semibold shadow-lg hover:from-green-600 hover:to-green-800 transition duration-300"
                onClick={() => {
                  setIsOpen(false);
                  handleCashMethod();
                }}
              >
                Cash
              </Button>
              <Button
                className="px-6 py-2 bg-gradient-to-r from-purple-500 to-purple-700 text-white font-semibold shadow-lg hover:from-purple-600 hover:to-purple-800 transition duration-300"
                onClick={() => setIsExpanded(true)}
              >
                UPI
              </Button>
            </div>
          </div>
        ) : (
          <div className="flex flex-col items-center">
            <UpiQrGenerator isExpanded={isExpanded} setIsExpanded={setIsExpanded} />
          </div>
        )}
      </DialogContent>
    </Dialog>
  );}

export function ChangePaymentStatus({isOpen:isOpen, setIsOpen:setIsOpen,popupData:popupData,setPopupData:setPopupData,isPopupDataUpdated: isPopupDataUpdated}) {

  // useEffect(() => {
  //   console.log("INsied")
  //   setPaymentStatusChange(true); // ✅ Open the dialog on mount
  // }, []);

  const handlePaymentStatusChange = () =>{
    isPopupDataUpdated.current = true;
    setPopupData({ ...popupData, payment: "pay" });

  }

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Confirm Payment Status Change</DialogTitle>
          <DialogDescription>
          You are about to change the payment status from <strong>Paid</strong> to <strong>Pay</strong>.  
            This means the transaction will be marked as unpaid, requiring further action.  
            Do you want to proceed?
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button 
           className="px-6 py-2 bg-gray-200 text-red-600 font-semibold shadow-lg 
           hover:bg-gradient-to-r hover:from-red-500 hover:to-red-700 
           hover:text-white transition duration-300"
          onClick={() => setIsOpen(false)}>
            Cancel
          </Button>
          <Button 
          className="px-6 py-2 bg-gray-200 text-black font-semibold shadow-lg 
          hover:bg-gradient-to-r hover:from-green-500 hover:to-green-700 
          hover:text-white transition duration-300"
          onClick={() => {
              setIsOpen(false)
              handlePaymentStatusChange()
            }
          }
          >
            Continue
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}


export default { PaymentPopup, ChangePaymentStatus };