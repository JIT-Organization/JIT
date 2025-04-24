import React from "react";

const BillPreview = ({ cartItems, handleUpdateQty, openCustomizeDialog }) => {

  return (
    <>
      <div className="w-full max-w-xs flex flex-col border rounded shadow-md h-full">
        <div className="p-4 border-b shrink-0">
          <h2 className="text-lg font-semibold">Order Summary</h2>
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          {cartItems.length === 0 ? (
            <p>No items added</p>
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
                      <button
                        onClick={() => handleUpdateQty(item.id, "decrement")}
                        className="px-1 py-0.5 border rounded"
                      >
                        -
                      </button>
                      <span>{item.qty}</span>
                      <button
                        onClick={() => handleUpdateQty(item.id, "increment")}
                        className="px-1 py-0.5 border rounded"
                      >
                        +
                      </button>
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
        </div>

        <div className="p-4 border-t font-bold flex justify-between shrink-0">
          <span>Total:</span>
          <span>
            ₹
            {cartItems
              .reduce((total, item) => total + item.price * item.qty, 0)
              .toFixed(2)}
          </span>
        </div>
      </div>

    </>
  );
};

export default BillPreview;
