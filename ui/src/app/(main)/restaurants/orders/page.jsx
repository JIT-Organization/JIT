"use client";
import FoodCard from "@/components/customUIComponents/FoodCard";
import { getOrderColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteOrderItem, getOrdersListOptions } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter, usePathname } from "next/navigation";
import { useSocket } from "@/components/providers/WebSocketContext";
import { useEffect } from "react";

const Orders = () => {
  const router = useRouter();
   const pathName = usePathname();
  const queryClient = useQueryClient();
  const {
    data: orderListData,
    isLoading,
    error,
  } = useQuery(getOrdersListOptions());
  const deleteMutation = useMutation(deleteOrderItem(queryClient));

  const queryKey = ["ordersList"];

  const { subscribe, isConnected } = useSocket();

  useEffect(() => {
    console.log("Setting up WebSocket subscriptions...");

    // const handleUserEvent = (newOrderItem) => {
    //   console.log("Data before cache update:", queryClient.getQueryData(queryKey));
    //   queryClient.setQueryData(queryKey, (oldData) => {
    //     console.log("Received item from socket:", newOrderItem);
    
    //     const data = Array.isArray(oldData) ? oldData : [];
    
    //       let orderFound = false;
    //       let itemUpdated = false;
    //       const updatedOrders = data.map(order => {
    //         if (order.orderNumber !== newOrderItem.orderNumber) {
    //           return order;
    //         }
        
    //         orderFound = true;

    //         if(order.status != newOrderItem.status) {
    //           return {
    //             ...order, status: newOrderItem.status
    //           }
    //         }

    //         const updatedItems = order.orderItems.map(item => {
    //           if (item.menuItemName === newOrderItem.menuItemName) {
    //             itemUpdated = true;
    //             return newOrderItem;
    //           }
    //           return item;
    //         });
  
    //         if (itemUpdated) {
    //           console.log("Updated existing item in cache.");
    //           return {
    //             ...order,
    //             orderItems: updatedItems
    //           };
    //         } else {
    //           console.log("Adding new item to existing order.");
    //           return {
    //             ...order,
    //             orderItems: [newOrderItem, ...order.orderItems]
    //           };
    //         }
          
    //       });
        
    //       if (!orderFound) {
    //       console.log("Adding new order to cache.");
    //       const newOrder = {
    //         orderNumber: newOrderItem.orderNumber,
    //         orderItems: [newOrderItem],
    //       };
    //       return [newOrder, ...data];
    //     }
    
    //       return updatedOrders;
    //   });
    // };

    const handleUserEvent = (order) => {
      queryClient.setQueryData(queryKey, (oldData) => {
        const updatedOrders = oldData.map(data => {
          if(data.orderNumber != order.orderNumber) return data;
          return order;
        })
        return updatedOrders;
      })
    }

    const handleRoleEvent = (message) => {
      console.log("%cReceived message on /topic/COOK:", "color: #6f42c1;", message);
    };

    const unsubUser = subscribe("/user/queue/orderItemStatusUpdated", handleUserEvent);
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

  if (isLoading) return <p>Loading orders list...</p>;
  if (error) return <p>Error loading orders list: {error.message}</p>;

  const handleEditClick = (id) => {
    router.push(`${pathName}/${id}`);
  };

  const handleDeleteClick = (id) => {
    deleteMutation.mutate({ id });
  };
  const handleCreateOrderClick = () => {
    router.push("/restaurants/createOrder");
  };
  const toggleRowClick = (row) => {
    row.toggleExpanded();
  };

  const columns = getOrderColumns(handleEditClick, handleDeleteClick);

  const expandableRowContent = (row) => {
    console.log(orderListData);
    return (
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 p-4">
        {row.original.orderItems.map((item) => (
          <div key={item.itemName}>
            <FoodCard mode="order" food={item} quantity={item.quantity} status={item.orderItemStatus} />
          </div>
        ))}
      </div>
    );
  };

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={orderListData}
        tabName="Orders"
        handleHeaderButtonClick={handleCreateOrderClick}
        headerButtonName="New Order"
        handleRowClick={toggleRowClick}
        expandableRowContent={expandableRowContent}
      />
    </div>
  );
};

export default Orders;
