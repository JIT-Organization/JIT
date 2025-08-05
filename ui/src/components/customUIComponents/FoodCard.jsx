import React, { useState, useRef, useEffect } from "react";
import Image from "next/image";
import { Card, CardHeader, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

// Custom hook for text truncation detection
const useTextTruncation = (text) => {
  const [isTruncated, setIsTruncated] = useState(false);
  const textRef = useRef(null);

  useEffect(() => {
    if (textRef.current && text) {
      const element = textRef.current;
      setIsTruncated(element.scrollWidth > element.clientWidth);
    }
  }, [text]);

  return { textRef, isTruncated };
};

// Reusable truncated text component with tooltip
const TruncatedTextWithTooltip = ({ text, className, prefix = "" }) => {
  const { textRef, isTruncated } = useTextTruncation(text);
  
  return (
    <div 
      ref={textRef}
      className={className}
      title={isTruncated ? text : ''}
    >
      {prefix}{text || "--"}
    </div>
  );
};

const FoodCard = ({
  food,
  quantity = 0,
  handleUpdateQty,
  openCustomizeDialog,
  mode, 
  status = "", 
  onActionClick, 
  onAddAgain,
}) => {
  const [isEditable, setIsEditable] = useState(false);
  
  food.image = food.images?.length ? food.images[0].preview : food.images;
  const isItemEditable = status === "" || status === "UNASSIGNED" || status === "ASSIGNED";

  const renderStatusButton = () => {
    const buttonConfig = {
      kitchen: {
        UNASSIGNED: { text: "Accept", action: "accept", className: "bg-red-700" },
        ASSIGNED: { text: "Start", action: "start", className: "bg-green-500" },
        STARTED: { text: "Ready", action: "ready", className: "bg-green-600" }
      },
      order: {
        READY_TO_SERVE: { text: "Complete", action: "complete", className: "bg-blue-600" }
      }
    };

    const config = buttonConfig[mode]?.[status];
    if (!config) return null;

    return (
      <Button 
        disabled={config.disabled} 
        className={`${config.className} w-full`}
        onClick={config.disabled ? undefined : (e) => {
          e.stopPropagation();
          onActionClick?.(food.menuItemName, config.action);
        }}
      >
        {config.text}
      </Button>
    );
  };

  return (
    <Card className="w-full h-full border-2 border-yellow-400 rounded-xl overflow-hidden shadow hover:shadow-md transition-all relative">
      <CardHeader className="relative p-0" onClick={() => mode === "create" && setIsEditable(false)}>
        <div className="relative h-32 w-full">
          <Image
            src={food.image || "https://images.pexels.com/photos/1860208/pexels-photo-1860208.jpeg?cs=srgb&dl=cooked-food-1860208.jpg&fm=jpg"}
            alt={food.menuItemName ?? food.menuItemName}
            layout="fill"
            objectFit="cover"
            className="rounded-t-xl"
          />

          {mode === "create" && (
            <Badge className="absolute top-2 left-2 bg-white text-green-700 text-xs font-medium px-2 py-0.5 rounded-full shadow">
              ⭐ {food.rating || "4.5"}
            </Badge>
          )}

          {mode === "create" && (
            <Badge className="absolute bottom-2 right-2 bg-black text-white text-[10px] px-2 py-0.5 rounded shadow">
              {food.tag || "New Arrival"}
            </Badge>
          )}

          {quantity > 0 && mode === "create" && (
            <Badge className={`absolute top-2 right-2 text-white text-[10px] px-2 py-0.5 rounded-full shadow ${mode === "create" ? "bg-green-600" : "bg-blue-600"}`}>
              { `Selected (${quantity})`}
            </Badge>
          )}

          {mode === "create" && quantity > 0 && isEditable && (
            <div className="absolute inset-0 bg-black bg-opacity-40 flex flex-col items-center justify-center text-white font-bold text-lg">
              <div className="flex space-x-4 items-center">
                <Button
                  size="sm"
                  variant="secondary"
                  className="px-2 py-1"
                  disabled={!isItemEditable}
                  onClick={(e) => {
                    e.stopPropagation();
                    if (isItemEditable) {
                      handleUpdateQty(food.menuItemName, "decrement");
                    }
                  }}
                >
                  –
                </Button>
                <span>{quantity}</span>
                <Button
                  size="sm"
                  variant="secondary"
                  className="px-2 py-1"
                  disabled={!isItemEditable}
                  onClick={(e) => {
                    e.stopPropagation();
                    if (isItemEditable) {
                      handleUpdateQty(food.menuItemName, "increment");
                    }
                  }}
                >
                  +
                </Button>
              </div>

              <Button
                variant="outline"
                size="xs"
                disabled={!isItemEditable}
                onClick={(e) => {
                  e.stopPropagation();
                  if (isItemEditable) {
                    openCustomizeDialog(food.menuItemName, true);
                  }
                }}
                className="mt-2 text-blue-600 hover:bg-blue-50 text-xs px-2 py-0.5 rounded-full shadow"
              >
                Customize
              </Button>
              <Button
                variant="outline"
                size="xs"
                onClick={(e) => {
                  e.stopPropagation();
                  onAddAgain(food);
                }}
                className="mt-1 text-green-600 hover:bg-green-50 text-xs px-2 py-0.5 rounded-full shadow"
              >
                Add Again
              </Button>
            </div>
          )}
        </div>
      </CardHeader>

      <CardContent className="p-3 space-y-0" onClick={() => mode === "create" && setIsEditable(!isEditable)}>
        <div className="flex justify-between items-center">
          <TruncatedTextWithTooltip 
            text={food.menuItemName}
            className="font-bold text-sm overflow-hidden text-ellipsis whitespace-nowrap flex-1 mr-2"
          />

          {mode === "kitchen" || mode === "order" ? (
            (() => {
              const statusConfig = {
                order: {
                  UNASSIGNED: { text: "New", className: "bg-blue-600" },
                  ASSIGNED: { text: "Pending", className: "bg-orange-600" },
                  STARTED: { text: "Cooking", className: "bg-yellow-600" },
                  READY_TO_SERVE: { text: "Ready", className: "bg-purple-600" },
                  SERVED: { text: "Completed", className: "bg-green-600" },
                  CANCELLED: { text: "Cancelled", className: "bg-red-600" }
                },
                kitchen: {
                  UNASSIGNED: { text: "New", className: "bg-blue-600" },
                  ASSIGNED: { text: "Assigned", className: "bg-orange-600" },
                  STARTED: { text: "Started", className: "bg-yellow-600" },
                  READY_TO_SERVE: { text: "Ready", className: "bg-purple-600" },
                  SERVED: { text: "Completed", className: "bg-green-600" },
                  CANCELLED: { text: "Cancelled", className: "bg-red-600" }
                }
              };
              
              const config = statusConfig[mode]?.[status] || { text: status, className: "bg-gray-500" };
              
              return (
                <Badge className={`text-white text-[10px] px-2 py-0.5 rounded-full shadow ${config.className}`}>
                  {config.text}
                </Badge>
              );
            })()
          ) : (
            <div className={`w-4 h-4 border-[1.5px] ${food.onlyVeg ? "border-green-700" : "border-red-700"} flex items-center justify-center bg-white rounded-[2px]`}>
              <div className={`w-2 h-2 ${food.onlyVeg ? "bg-green-700" : "bg-red-700"} rounded-[1px]`} />
            </div>
          )}

        </div>

        {mode === "create" && (
          <TruncatedTextWithTooltip 
            text={food.description}
            className="text-xs text-gray-600 overflow-hidden text-ellipsis whitespace-nowrap min-h-[1.2rem] block space-y-0"
          />
        )}

        {(mode === "kitchen" || mode === "order") && (
          <TruncatedTextWithTooltip
            text={food.notes}
            prefix="Notes: "
            className="text-xs text-gray-600 overflow-hidden text-ellipsis whitespace-nowrap min-h-[1.2rem] block space-y-0"
          />
        )}

        <div className="flex justify-between items-center mt-0">
          {mode === "create" ? (
            <div className="text-sm font-semibold text-green-700">
              {food.offerPrice && (
                <span className="line-through text-gray-400 text-xs mr-1">
                  ₹{food.price}
                </span>
              )}
              ₹{food.offerPrice || food.price}
            </div>
          ) : (
            <div className="text-xs font-semibold text-blue-700">
              Quantity: {quantity}
            </div>
          )}

          <div className="text-xs text-gray-500">
            ⏱ {food.preparationTime || 0} mins
          </div>
        </div>

        {(mode === "order" || mode === "kitchen") && (
          <div className="mt-2">
            {renderStatusButton()}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default FoodCard;
