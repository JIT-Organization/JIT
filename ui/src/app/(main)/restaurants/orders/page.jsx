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
