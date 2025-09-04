"use client";
import FoodCard from "@/components/customUIComponents/FoodCard";
import { getOrderColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteOrderItem, getOrdersListOptions } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter, usePathname } from "next/navigation";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";
import { useToast } from "@/hooks/use-toast";
import { useState, useEffect } from "react";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useNotifications } from "@/contexts/NotificationContext";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";

const Orders = () => {
  const router = useRouter();
  const pathName = usePathname();
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const { addNotification } = useNotifications();
  const [loadingItems, setLoadingItems] = useState({});

  const clearLoadingItem = (id) =>
    setLoadingItems((prev) => {
      const newState = { ...prev };
      delete newState[id];
      return newState;
    });

  const {
    data: orderListData,
    isLoading,
    error,
  } = useQuery(getOrdersListOptions());

  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // Effect to log connection status changes
  useEffect(() => {
    if (isConnected) {
      console.log("Orders WebSocket connection established.");
    } else {
      console.log("Orders WebSocket connection lost.");
    }
  }, [isConnected]);

  // Effect for handling orders-specific subscriptions
  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up Orders WebSocket subscriptions...");

    // Handle new orders created
    const handleOrderCreated = (message) => {
      console.log("%cReceived new order:", "color: #28a745;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_CREATED,
        message: `New order received: Order #${message.orderId || message.id || 'Unknown'}`,
        priority: 'high',
        data: message
      });

      // Invalidate orders query to refresh the list
      queryClient.invalidateQueries(['orders']);
      queryClient.invalidateQueries(['dashboard-stats']);
    };

    // Handle order updates
    const handleOrderUpdated = (message) => {
      console.log("%cReceived order update:", "color: #ffc107;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order updated: Order #${message.orderId || message.id || 'Unknown'}`,
        priority: 'medium',
        data: message
      });

      // Invalidate orders query to refresh the list
      queryClient.invalidateQueries(['orders']);
    };

    // Handle order status changes
    const handleOrderStatusChanged = (message) => {
      console.log("%cReceived order status change:", "color: #17a2b8;", message);
      
      const statusMessages = {
        'PENDING': 'Order is pending',
        'CONFIRMED': 'Order has been confirmed',
        'PREPARING': 'Order is being prepared',
        'READY': 'Order is ready for pickup/delivery',
        'SERVED': 'Order has been served',
        'DELIVERED': 'Order has been delivered',
        'CANCELLED': 'Order has been cancelled'
      };

      const statusMessage = statusMessages[message.status] || `Status: ${message.status}`;
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order #${message.orderId || message.id || 'Unknown'}: ${statusMessage}`,
        priority: ['READY', 'SERVED', 'DELIVERED'].includes(message.status) ? 'high' : 'medium',
        data: message
      });

      // Invalidate orders query to refresh the list
      queryClient.invalidateQueries(['orders']);
      queryClient.invalidateQueries(['kitchen-orders']);
    };

    // Handle order cancellations
    const handleOrderCancelled = (message) => {
      console.log("%cReceived order cancellation:", "color: #dc3545;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order cancelled: Order #${message.orderId || message.id || 'Unknown'}`,
        priority: 'high',
        data: message
      });

      // Invalidate orders query to refresh the list
      queryClient.invalidateQueries(['orders']);
      queryClient.invalidateQueries(['kitchen-orders']);
    };

    // Handle payment status updates
    const handlePaymentProcessed = (message) => {
      console.log("%cReceived payment update:", "color: #6f42c1;", message);
      
      const paymentMessages = {
        'PENDING': 'Payment is pending',
        'PROCESSING': 'Payment is being processed',
        'COMPLETED': 'Payment completed successfully',
        'FAILED': 'Payment failed',
        'REFUNDED': 'Payment has been refunded'
      };

      const paymentMessage = paymentMessages[message.status] || `Payment: ${message.status}`;
      
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_UPDATED,
        message: `Order #${message.orderId || message.id || 'Unknown'}: ${paymentMessage}`,
        priority: ['COMPLETED', 'FAILED'].includes(message.status) ? 'high' : 'medium',
        data: message
      });

      // Invalidate orders query to refresh the list
      queryClient.invalidateQueries(['orders']);
      queryClient.invalidateQueries(['payments']);
    };

    // Subscribe to user-specific order queues
    const unsubOrderCreated = subscribe("/user/queue/orderCreated", handleOrderCreated);
    const unsubOrderUpdated = subscribe("/user/queue/orderUpdated", handleOrderUpdated);
    const unsubOrderStatusChanged = subscribe("/user/queue/orderStatusChanged", handleOrderStatusChanged);
    const unsubOrderCancelled = subscribe("/user/queue/orderCancelled", handleOrderCancelled);
    const unsubPaymentProcessed = subscribe("/user/queue/paymentProcessed", handlePaymentProcessed);

    // Cleanup function
    return () => {
      console.log("Cleaning up Orders WebSocket subscriptions.");
      unsubOrderCreated();
      unsubOrderUpdated();
      unsubOrderStatusChanged();
      unsubOrderCancelled();
      unsubPaymentProcessed();
    };
  }, [subscribe, addNotification, queryClient]);
  
  const deleteMutation = useMutation({
    ...deleteOrderItem(queryClient),
    onMutate: ({ id }) => {
      setLoadingItems((prev) => ({ ...prev, [id]: "deleting" }));
    },
    onSuccess: (_, { id }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Order deleted successfully",
      });
      clearLoadingItem(id);
    },
    onError: (error, { id }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete order",
        variant: "destructive",
      });
      clearLoadingItem(id);
    },
  });

  if (isLoading) {
    return <LoadingState message="Loading orders..." />;
  }

  if (error) {
    return <ErrorState title="Error loading orders" message={error.message} />;
  }

  if (!orderListData?.length) {
    return <ErrorState title="No Orders" message="No orders found." />;
  }

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

  const columns = getOrderColumns(
    handleEditClick, 
    handleDeleteClick,
    {
      isDeleting: (id) => loadingItems[id] === 'deleting'
    }
  );

  const expandableRowContent = (row) => {
    console.log(row.original);
    return (
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 p-4">
        {row.original.orderItems.map((item) => (
          <div key={item.itemName}>
            <FoodCard mode="order" food={item} quantity={item.quantity} status="READY_TO_SERVE"/>
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
