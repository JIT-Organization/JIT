import React from "react";
import {
  Card,
  CardHeader,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Pencil, Trash2, User, Copy } from "lucide-react";
import { cn } from "@/lib/utils";

const BillPreview = ({ cartItems, handleUpdateQty, openCustomizeDialog, onOpenCustomerDialog, handleCopyItem, isDialog = false }) => {
  return (
    <Card className="w-full flex flex-col h-full overflow-hidden">
      <CardHeader className="p-4 border-b flex-shrink-0">
        <div className={cn("flex justify-between items-center", isDialog && "pr-8")}>
          <CardTitle className="text-lg">Order Summary</CardTitle>
          <Button variant="ghost" size="icon" onClick={onOpenCustomerDialog}>
            <User className="h-5 w-5" />
          </Button>
        </div>
      </CardHeader>

      <ScrollArea className="flex-1 p-4">
        {cartItems.length === 0 ? (
          <p className="text-sm">No items added</p>
        ) : (
          <ul className="space-y-2">
            {cartItems.map((item) => {
              const isItemEditable = !item.status || item.status === "UNASSIGNED" || item.status === "ASSIGNED";
              
              return (
              <li
                key={item.itemName}
                className="flex justify-between border-b pb-2 text-sm"
              >
                <div className="flex flex-col">
                  <span className="text-xs font-medium">{item.itemName} {`(₹${item.itemPrice})`}</span>
                  <div className="flex items-center gap-1 mt-1 text-xs">
                    <Button
                      variant="outline"
                      size="xs"
                      className="px-1 py-0.5 h-6"
                      disabled={!isItemEditable}
                      onClick={() => isItemEditable && handleUpdateQty(item.itemName, "decrement")}
                    >
                      -
                    </Button>
                    <span>{item.qty}</span>
                    <Button
                      variant="outline"
                      size="xs"
                      className="px-1 py-0.5 h-6"
                      disabled={!isItemEditable}
                      onClick={() => isItemEditable && handleUpdateQty(item.itemName, "increment")}
                    >
                      +
                    </Button>
                  </div>
                </div>

                <div className="flex flex-col items-end">
                  <span className="text-sm font-semibold">
                    ₹ {(item.itemPrice * item.qty).toFixed(2)}
                  </span>
                  <div className="flex gap-2 mt-1">
                    <button
                      onClick={() => isItemEditable && openCustomizeDialog(item.itemName, false)}
                      disabled={!isItemEditable}
                      className={`text-xs underline flex items-center ${
                        isItemEditable ? 'text-blue-500 cursor-pointer hover:text-blue-700' : 'text-gray-400 cursor-not-allowed'
                      }`}
                      title={isItemEditable ? "Edit" : "Cannot edit - item is being processed"}
                    >
                      <Pencil size={16} />
                    </button>
                    <button
                      onClick={() => handleCopyItem(item)}
                      className="text-green-500 text-xs underline flex items-center cursor-pointer hover:text-green-700"
                      title="Copy"
                    >
                      <Copy size={16} />
                    </button>
                    <button
                      onClick={() => isItemEditable && handleUpdateQty(item.itemName, "remove")}
                      disabled={!isItemEditable}
                      className={`text-xs underline flex items-center ${
                        isItemEditable ? 'text-red-500 cursor-pointer hover:text-red-700' : 'text-gray-400 cursor-not-allowed'
                      }`}
                      title={isItemEditable ? "Delete" : "Cannot delete - item is being processed"}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
              </li>
              );
            })}
          </ul>
        )}
      </ScrollArea>

      <CardFooter className="p-4 border-t font-bold flex justify-between flex-shrink-0">
        <span>Total:</span>
        <span>
          ₹
          {cartItems
            .reduce((total, item) => total + item.itemPrice * item.qty, 0)
            .toFixed(2)}
        </span>
      </CardFooter>
    </Card>
  );
};

export default BillPreview;
