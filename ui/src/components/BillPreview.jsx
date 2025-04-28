import React from "react";
import {
  Card,
  CardHeader,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";

const BillPreview = ({ cartItems, handleUpdateQty, openCustomizeDialog }) => {
  return (
    <Card className="w-full max-w-xs flex flex-col h-full">
      <CardHeader className="p-4 border-b">
        <CardTitle className="text-lg">Order Summary</CardTitle>
      </CardHeader>

      <ScrollArea className="flex-1 p-4">
        {cartItems.length === 0 ? (
          <p className="text-sm">No items added</p>
        ) : (
          <ul className="space-y-2">
            {cartItems.map((item) => (
              <li
                key={item.id}
                className="flex justify-between border-b pb-2 text-sm"
              >
                <div className="flex flex-col">
                  <span className="text-xs font-medium">{item.name}</span>
                  <div className="flex items-center gap-1 mt-1 text-xs">
                    <Button
                      variant="outline"
                      size="xs"
                      className="px-1 py-0.5 h-6"
                      onClick={() => handleUpdateQty(item.id, "decrement")}
                    >
                      -
                    </Button>
                    <span>{item.qty}</span>
                    <Button
                      variant="outline"
                      size="xs"
                      className="px-1 py-0.5 h-6"
                      onClick={() => handleUpdateQty(item.id, "increment")}
                    >
                      +
                    </Button>
                  </div>
                </div>

                <div className="flex flex-col items-end">
                  <span className="text-sm font-semibold">
                    ₹ {(item.price * item.qty).toFixed(2)}
                  </span>
                  <div className="flex gap-2 mt-1">
                    <button
                      onClick={() => openCustomizeDialog(item.id)}
                      className="text-blue-500 text-xs underline"
                    >
                      Customize
                    </button>
                    <button
                      onClick={() => handleUpdateQty(item.id, "remove")}
                      className="text-red-500 text-xs underline"
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        )}
      </ScrollArea>

      <CardFooter className="p-4 border-t font-bold flex justify-between">
        <span>Total:</span>
        <span>
          ₹
          {cartItems
            .reduce((total, item) => total + item.price * item.qty, 0)
            .toFixed(2)}
        </span>
      </CardFooter>
    </Card>
  );
};

export default BillPreview;
