import React from "react";

const BillPreview = ({ cartItems, handleUpdateQty, customizeItem }) => {
  return (
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
                className="flex justify-between items-start border-b pb-2"
              >
                <div className="flex flex-col w-full">
                  <div className="flex justify-between items-center">
                    <span>{item.name}</span>
                    <span>₹ {(item.price * item.qty).toFixed(2)}</span>
                  </div>
                  <div className="flex items-center justify-between mt-1">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleUpdateQty(item.id, "decrement")}
                        className="px-2 py-1 border rounded text-sm"
                      >
                        -
                      </button>
                      <span>{item.qty}</span>
                      <button
                        onClick={() => handleUpdateQty(item.id, "increment")}
                        className="px-2 py-1 border rounded text-sm"
                      >
                        +
                      </button>
                    </div>
                    <div className="flex items-center gap-2">
                    <button
                      onClick={() => customizeItem(item.id)}
                      className="text-blue-500 text-sm underline ml-2"
                    >
                      Customize
                    </button>
                    <button
                      onClick={() => handleUpdateQty(item.id, "remove")}
                      className="text-red-500 text-sm underline ml-2"
                    >
                      Remove
                    </button>
                    </div>
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
  );
};

export default BillPreview;
