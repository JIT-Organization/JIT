"use client";
import React, { useState, useMemo, useEffect } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { getOrderItemsForKitchen, patchUpdateOrderItem } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import FoodCard from "@/components/customUIComponents/FoodCard";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useIsMobile } from "@/hooks/use-mobile";
import { useSocket } from "@/components/providers/WebSocketContext";

const CreateOrder = () => {
  const router = useRouter();
  const isMobile = useIsMobile();
  const [showPopup, setShowPopup] = useState(false);
  const queryClient = useQueryClient();
  const queryKey = ["orderItems"];

  const { data: menuItems = [], isLoading, error } = useQuery(getOrderItemsForKitchen());
  const patchMutation = useMutation(patchUpdateOrderItem(queryClient));
  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

  const { subscribe, isConnected } = useSocket();

  useEffect(() => {
    console.log("Setting up WebSocket subscriptions...");

    const handleUserEvent = (newOrderItem) => {    
      queryClient.setQueryData(queryKey, (oldData) => {
        return [newOrderItem, ...oldData];
      });
    };

    const handleRoleEvent = (message) => {
      console.log("%cReceived message on /topic/COOK:", "color: #6f42c1;", message);
    };

    const unsubUser = subscribe("/user/queue/orderItemCreated", handleUserEvent);
    const unsubTopic = subscribe("/topic/COOK", handleRoleEvent);

    return () => {
      console.log("Cleaning up WebSocket subscriptions.");
      unsubUser();
      unsubTopic();
    };
  }, [subscribe]);

  useEffect(() => {
    if (isConnected) {
      console.log("Socket connected, invalidating kitchen orders.");
      queryClient.invalidateQueries({ queryKey });
    }
  }, [isConnected, queryClient]);

  const filteredMenuItems = useMemo(() => {
    return menuItems.filter((item) => {
      const categoryList = item?.categorySet ?? [];
      const matchesCategory = activeCategory === "All" || categoryList.includes(activeCategory);
      const matchesSearch = (item?.menuItemName ?? "")
        .toLowerCase()
        .includes(globalFilter.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [menuItems, globalFilter, activeCategory]);

  const categories = getDistinctCategories(menuItems);

  if (isLoading) return <p>Loading menu...</p>;
  if (error) return <p>Error loading menu: {error.message}</p>;

  const changeOrderItemStatus = (orderItem, action) => {
    switch (action) {
      case "accept":
        orderItem.orderItemStatus = "ASSIGNED"
        break;
      case "start":
        orderItem.orderItemStatus = "STARTED"
        break;
      case "ready":
        orderItem.orderItemStatus = "READY_TO_SERVE"
        break;
      case "served":
        orderItem.orderItemStatus = "SERVED"
        break;
      default:
        break;
    }
  }

  const handleUpdateStatusClick = (orderItem, action) => {
    changeOrderItemStatus(orderItem, action);
    patchMutation.mutate({ fields: { ...orderItem }, identifiers: {menuItemName: orderItem.menuItemName, orderNumber: orderItem.orderNumber} });
  }

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 shadow bg-white">
        <DataTableHeader
          tabName="Kitchen Orders"
          globalFilter={globalFilter}
          setGlobalFilter={setGlobalFilter}
          categories={categories}
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
          setColumnFilters={() => {}}
        />
      </CardTitle>

      <CardContent className="mt-0 px-2 py-0">
        <div className="flex flex-1 overflow-hidden" style={{ height: "calc(100vh - 191px)" }}>
          <div className="flex-1 overflow-y-auto p-0 pb-4">
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
              {filteredMenuItems.map((food, index) => (
                <div
                  key={food.menuItemName + (index + 1)}
                  className="w-full"
                >
                  <FoodCard
                    food={food}
                    quantity={food.quantity}
                    mode="kitchen"
                    status={food.orderItemStatus}
                    onActionClick={handleUpdateStatusClick}
                  />
                </div>
              ))}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default CreateOrder;
