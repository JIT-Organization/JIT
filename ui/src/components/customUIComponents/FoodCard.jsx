import React, { useState } from "react";
import Image from "next/image";
import { Card, CardContent } from "@/components/ui/card";

const FoodCard = ({ food,
  quantity,
  handleUpdateQty,
}) => {
  const cost = food.offerPrice || food.price;
  const originalCost = food.offerPrice ? food.price : null;
  const [isEditable, setIsEditable] = useState(false);

  return (
    <Card className="w-full  border-2 border-yellow-400 rounded-xl overflow-hidden shadow hover:shadow-md transition-all relative">
      
      <div className="relative h-32 w-full" onClick={() => {setIsEditable(true)}}>
        <Image
          src={
            food.image ||
            "https://images.pexels.com/photos/1860208/pexels-photo-1860208.jpeg?cs=srgb&dl=cooked-food-1860208.jpg&fm=jpg"
          }
          alt={food.menuItemName}
          layout="fill"
          objectFit="cover"
          className="rounded-t-md"
        />

        <div className="absolute top-2 left-2 bg-white text-green-700 text-xs font-medium px-2 py-0.5 rounded-full shadow">
          ⭐ {food.rating || "4.5"}
        </div>

        {/* {food.tag && ( */}
          <div className="absolute bottom-2 right-2 bg-black text-white text-[10px] px-2 py-0.5 rounded shadow">
            {food.tag || "New Arrival"}
          </div>
        {/* )} */}

        {quantity > 0 && !isEditable && (
          <div className="absolute top-2 right-2 bg-green-600 text-white text-[10px] px-2 py-0.5 rounded-full shadow">
            Selected ({quantity})
          </div>
        )}

        {quantity > 0 && isEditable && (
          <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center text-white font-bold text-lg space-x-4">
            <button
              className="px-2 py-1 bg-white text-black rounded"
              onClick={(e) => {
                e.stopPropagation();
                handleUpdateQty(food.id, "decrement");
              }}
            >
              –
            </button>
            <span>{quantity}</span>
            <button
              className="px-2 py-1 bg-white text-black rounded"
              onClick={(e) => {
                e.stopPropagation();
                handleUpdateQty(food.id, "increment");
              }}
            >
              +
            </button>
          </div>
        )}
      </div>

      <CardContent className="p-3 space-y-1" onClick={() => {setIsEditable(false)}}>
        <div className="flex justify-between items-center">
          <div className="font-bold text-sm">{food.menuItemName}</div>
          <div
            className={`w-4 h-4 border-[1.5px] ${
              food.onlyVeg ? "border-green-700" : "border-red-700"
            } flex items-center justify-center bg-white`}
          >
            <div
              className={`w-2 h-2 ${
                food.onlyVeg ? "bg-green-700" : "bg-red-700"
              } clip-triangle`}
            />
          </div>
        </div>

        {food.description && (
          <div className="text-xs text-gray-600 line-clamp-2">{food.description}</div>
        )}

        <div className="flex justify-between items-center">
          <div className="text-sm font-semibold text-green-700">
            {originalCost && (
              <span className="line-through text-gray-400 text-xs mr-1">
                ₹{originalCost}
              </span>
            )}
            ₹{cost}
          </div>
          <div className="text-xs text-gray-500">⏱ {food.preparationTime || 20} mins</div>
        </div>
      </CardContent>
    </Card>
  );
};

export default FoodCard;
