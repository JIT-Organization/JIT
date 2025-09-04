"use client";
import { getPaymentsHistory } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { useState, useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useNotifications } from "@/contexts/NotificationContext";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";

const Payments = () => {
  const [tableData, setTableData] = useState([...data]);
  const queryClient = useQueryClient();
  const { addNotification } = useNotifications();
  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // WebSocket connection status
  useEffect(() => {
    if (isConnected) {
      console.log("Payments WebSocket connection established.");
    } else {
      console.log("Payments WebSocket connection lost.");
    }
  }, [isConnected]);

  // WebSocket subscriptions for payments page
  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up Payments WebSocket subscriptions...");

    // Handle payment confirmations
    const handlePaymentConfirmation = (message) => {
      console.log("%cReceived payment confirmation:", "color: #27ae60;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.PAYMENT_STATUS,
        message: `Payment confirmed for Order #${message.orderId || 'Unknown'}: ₹${message.amount || 'N/A'}`,
        priority: 'high',
        data: message
      });

      // Invalidate payment queries to refresh the list
      queryClient.invalidateQueries(['payments']);
      queryClient.invalidateQueries(['orders']);
    };

    // Handle payment failures
    const handlePaymentFailure = (message) => {
      console.log("%cReceived payment failure:", "color: #e74c3c;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.PAYMENT_STATUS,
        message: `Payment failed for Order #${message.orderId || 'Unknown'}: ${message.reason || 'Unknown reason'}`,
        priority: 'high',
        data: message
      });

      queryClient.invalidateQueries(['payments']);
      queryClient.invalidateQueries(['orders']);
    };

    // Handle refund notifications
    const handleRefundProcessed = (message) => {
      console.log("%cReceived refund notification:", "color: #f39c12;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.PAYMENT_STATUS,
        message: `Refund processed for Order #${message.orderId || 'Unknown'}: ₹${message.amount || 'N/A'}`,
        priority: 'high',
        data: message
      });

      queryClient.invalidateQueries(['payments']);
    };

    // Handle payment system status updates
    const handlePaymentSystemStatus = (message) => {
      console.log("%cReceived payment system status:", "color: #9b59b6;", message);
      
      const statusMessages = {
        'ONLINE': 'Payment system is online',
        'OFFLINE': 'Payment system is offline',
        'MAINTENANCE': 'Payment system under maintenance',
        'ERROR': 'Payment system error'
      };

      addNotification({
        type: NOTIFICATION_TYPES.PAYMENT_STATUS,
        message: statusMessages[message.status] || message.message || 'Payment system status updated',
        priority: ['OFFLINE', 'ERROR'].includes(message.status) ? 'high' : 'medium',
        data: message
      });
    };

    // Subscribe to payment-specific channels
    const unsubPaymentConfirmation = subscribe("/user/queue/paymentConfirmation", handlePaymentConfirmation);
    const unsubPaymentFailure = subscribe("/user/queue/paymentFailure", handlePaymentFailure);
    const unsubRefundProcessed = subscribe("/user/queue/refundProcessed", handleRefundProcessed);
    const unsubPaymentSystemStatus = subscribe("/topic/paymentSystem/status", handlePaymentSystemStatus);

    // Cleanup function
    return () => {
      console.log("Cleaning up Payments WebSocket subscriptions.");
      unsubPaymentConfirmation();
      unsubPaymentFailure();
      unsubRefundProcessed();
      unsubPaymentSystemStatus();
    };
  }, [subscribe, addNotification, queryClient]);

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getPaymentsHistory(handleEditClick, handleDeleteClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Payment History"
      />
    </div>
  );
};

export default Payments;
