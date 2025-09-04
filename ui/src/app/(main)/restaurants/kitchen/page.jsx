"use client";
import React, { useState, useMemo, useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { getMenuItemListOptions } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import FoodCard from "@/components/customUIComponents/FoodCard";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useIsMobile } from "@/hooks/use-mobile";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useNotifications } from "@/contexts/NotificationContext";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";

const Kitchen = () => {
  const router = useRouter();
  const isMobile = useIsMobile();
  const queryClient = useQueryClient();
  const { addNotification } = useNotifications();
  const [showPopup, setShowPopup] = useState(false);

  const { data: menuItems = [], isLoading, error } = useQuery(getMenuItemListOptions());
  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

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

  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // Effect to log connection status changes from the component's perspective
  useEffect(() => {
    if (isConnected) {
      console.log("Kitchen WebSocket connection established.");
    } else {
      console.log("Kitchen WebSocket connection lost.");
    }
  }, [isConnected]);

  // Effect for handling kitchen-specific subscriptions
  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up Kitchen WebSocket subscriptions...");

    // Handle new orders assigned to kitchen
    const handleKitchenOrderAssigned = (message) => {
      console.log("%cReceived kitchen order assignment:", "color: #ff6b35;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_CREATED,
        message: `New order assigned to kitchen: Order #${message.orderId || 'Unknown'}`,
        priority: 'high',
        data: message
      });

      // Invalidate kitchen orders query to refresh the list
      queryClient.invalidateQueries(['kitchen-orders']);
      queryClient.invalidateQueries(['orders']);
    };

    // Handle order status updates
    const handleOrderStatusUpdate = (message) => {
      console.log("%cReceived order status update:", "color: #4ecdc4;", message);
      
      const statusMessages = {
        'PREPARING': 'Order is being prepared',
        'READY': 'Order is ready for pickup',
        'SERVED': 'Order has been served',
        'CANCELLED': 'Order has been cancelled'
      };

      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order #${message.orderId || 'Unknown'}: ${statusMessages[message.status] || message.status}`,
        priority: message.status === 'READY' ? 'high' : 'medium',
        data: message
      });

      // Invalidate relevant queries
      queryClient.invalidateQueries(['kitchen-orders']);
      queryClient.invalidateQueries(['orders']);
    };

    // Handle order priority changes
    const handleOrderPriorityChanged = (message) => {
      console.log("%cReceived order priority change:", "color: #ffa726;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order #${message.orderId || 'Unknown'} priority changed to ${message.priority || 'unknown'}`,
        priority: 'medium',
        data: message
      });

      queryClient.invalidateQueries(['kitchen-orders']);
    };

    // Handle kitchen status updates
    const handleKitchenStatusUpdate = (message) => {
      console.log("%cReceived kitchen status update:", "color: #9c27b0;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.KITCHEN_STATUS,
        message: message.message || `Kitchen status: ${message.status || 'Updated'}`,
        priority: 'medium',
        data: message
      });
    };

    // Subscribe to user-specific kitchen queues
    const unsubKitchenOrder = subscribe("/user/queue/kitchenOrderAssigned", handleKitchenOrderAssigned);
    const unsubOrderStatus = subscribe("/user/queue/orderStatusUpdate", handleOrderStatusUpdate);
    const unsubOrderPriority = subscribe("/user/queue/orderPriorityChanged", handleOrderPriorityChanged);
    
    // Subscribe to kitchen-specific topic
    const unsubKitchenStatus = subscribe("/topic/kitchen/status", handleKitchenStatusUpdate);

    // Cleanup function
    return () => {
      console.log("Cleaning up Kitchen WebSocket subscriptions.");
      unsubKitchenOrder();
      unsubOrderStatus();
      unsubOrderPriority();
      unsubKitchenStatus();
    };
  }, [subscribe, addNotification, queryClient]);

  const categories = getDistinctCategories(menuItems);

  if (isLoading) {
    return <LoadingState message="Loading menu..." />;
  }

  if (error) {
    return <ErrorState title="Error loading menu" message={error.message} />;
  }

  if (!menuItems?.length) {
    return <ErrorState title="No Menu Items" message="No menu items found." />;
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
                  key={food.id + (index + 1)}
                  className="w-full"
                >
                  <FoodCard
                    food={food}
                    quantity={food.id} //qty update pannanum
                    mode="kitchen"
                    status="STARTED"
                    onActionClick={(id, x) => console.log(id, x)}
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

export default Kitchen;
