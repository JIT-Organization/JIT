"use client";
import PieChartTemplate from "@/components/customUIComponents/PieChartTemplate";
import BarGraphTemplate from "@/components/customUIComponents/BarGraphTemplate";
import LineGraphTemplate from "@/components/customUIComponents/LineGraphTemplate";
import CustomCard from "@/components/customUIComponents/CustomCard";
import { FaShoppingCart, FaUserFriends, FaTable, FaUsers, FaRupeeSign } from "react-icons/fa";
import { useState } from "react";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useEffect } from "react";
import { useQueryClient, useQuery } from "@tanstack/react-query";
import { useNotifications } from "@/contexts/NotificationContext";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";
import { getDashboardDataOptions } from "@/lib/api/api";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";

const sampleData = [
  { name: "Pizza", quantity: 4 },
  { name: "Burger", quantity: 2 },
  { name: "Sushi", quantity: 5 },
  { name: "Pasta", quantity: 3 },
  { name: "Biriyani", quantity: 20 },
];

const weeklyData = [
  { day: "Monday", apples: 12, bananas: 18, cherries: 25 },
  { day: "Tuesday", apples: 10, bananas: 20, cherries: 15 },
  { day: "Wednesday", apples: 15, bananas: 22, cherries: 19 },
  { day: "Thurdsay", apples: 12, bananas: 18, cherries: 25 },
  { day: "Friday", apples: 10, bananas: 20, cherries: 15 },
  { day: "Saturday", apples: 15, bananas: 22, cherries: 19 },
];

const incomeData = [
  { day: "Monday", cash: 300, online: 500, wallet: 100 },
  { day: "Tuesday", cash: 200, online: 450, wallet: 150 },
  { day: "Wednesday", cash: 350, online: 400, wallet: 200 },
  { day: "Thursday", cash: 300, online: 600, wallet: 180 },
  { day: "Friday", cash: 400, online: 700, wallet: 120 },
];

const sample =[
  { label: "New", value: "26" },
  { label: "Ongoing", value: "36" },
  { label: "Completed", value: "48" },
]

const sample2 =[
  { label: "Manager", value: "1" },
  { label: "Server", value: "4" },
  { label: "Cook", value: "2" },
]
const sample3 =[
  { label: "Table seats", value: "48" },
  { label: "Open seats", value: "6" },
]

const sample4 =[
  { label: "Dine in", value: "78" },
  { label: "Take away", value: "15" },
  { label: "Delivery", value: "6" },
]
const sample5 =[
  { label: "Cash", value: "1,75,486" },
  { label: "Online", value: "10,68,320" },
  { label: "Wallet", value: "31,872" },
]



export default function DashboardPage() {
  const [expandedCards, setExpandedCards] = useState(new Set([])); // Default: no card expanded
  const queryClient = useQueryClient();
  const { addNotification } = useNotifications();
  
  // Fetch dashboard data from API
  const { data: dashboardData, isLoading, error } = useQuery(getDashboardDataOptions());
  console.log("Dashboard data:", dashboardData);
  // Use the hook and get connection status
  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // Effect to log connection status changes from the component's perspective
  useEffect(() => {
    if (isConnected) {
      console.log("Dashboard WebSocket connection established.");
    } else {
      console.log("Dashboard WebSocket connection lost.");
    }
  }, [isConnected]);

  // Effect for handling dashboard-specific subscriptions
  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up Dashboard WebSocket subscriptions...");

    const handleOrderItemCreated = (message) => {
      console.log("%cReceived message on /user/queue/orderItemCreated:", "color: #007bff;", message);
      
      // Add notification
      addNotification({
        type: NOTIFICATION_TYPES.ORDER_CREATED,
        message: `New order item created: ${message.itemName || 'Unknown item'}`,
        priority: 'high',
        data: message
      });

      // Invalidate queries to refresh dashboard data
      queryClient.invalidateQueries(['orders']);
      queryClient.invalidateQueries(['ordersList']);
      queryClient.invalidateQueries(['dashboardData']);
    };

    const handleRoleEvent = (message) => {
      console.log("%cReceived message on /topic/role:", "color: #6f42c1;", message);
      // This is already handled in the global WebSocket handler in layout
      // But you can add dashboard-specific logic here if needed
    };

    // Subscribe to user-specific queue
    const unSubUser = subscribe("/user/queue/orderItemCreated", handleOrderItemCreated);
    const unSubTopic = subscribe("/topic/role", handleRoleEvent);

    // The cleanup function will be called when the component unmounts
    return () => {
      console.log("Cleaning up Dashboard WebSocket subscriptions.");
      unSubUser();
      unSubTopic();
    };
  }, [subscribe, addNotification, queryClient]);

  const toggleCardExpansion = (idx) => {
    setExpandedCards(prev => {
      const newSet = new Set(prev);
      if (newSet.has(idx)) {
        newSet.delete(idx);
      } else {
        newSet.add(idx);
      }
      return newSet;
    });
  };

  // Show loading state
  if (isLoading) {
    return <LoadingState message="Loading dashboard data..." />;
  }

  // Show error state
  if (error) {
    return <ErrorState title="Error loading dashboard" message={error.message} />;
  }

  // Create dynamic card data from API response
  const cardData = dashboardData ? [
    {
      title: "Orders",
      value: dashboardData.totalOrders?.toString() || "0",
      data: sample, // Keep existing breakdown format for now
      icon: <FaShoppingCart className="text-3xl text-yellow-500" />,
      color: "text-yellow-500",
    },
    {
      title: "Active users",
      value: (dashboardData.totalCooks + dashboardData.totalServers)?.toString() || "0",
      data: [
        { label: "Cooks", value: dashboardData.totalCooks?.toString() || "0" },
        { label: "Servers", value: dashboardData.totalServers?.toString() || "0" },
      ],
      icon: <FaUserFriends className="text-3xl text-blue-500" />,
      color: "text-blue-500",
    },
    {
      title: "Available Tables",
      value: dashboardData.totalDiningTables?.toString() || "0",
      data: [
        { label: "Total Tables", value: dashboardData.totalDiningTables?.toString() || "0" },
      ],
      icon: <FaTable className="text-3xl text-green-500" />,
      color: "text-green-500",
    },
    {
      title: "Total Customers",
      value: dashboardData.totalCustomers?.toString() || "0",
      data: sample4, // Keep existing breakdown format for now
      icon: <FaUsers className="text-3xl text-purple-500" />,
      color: "text-purple-500",
    },
    {
      title: "Total Revenue",
      value: dashboardData.totalRevenue?.toLocaleString('en-IN') || "0",
      data: sample5, // Keep existing breakdown format for now
      icon: <FaRupeeSign className="text-3xl text-orange-500" />,
      color: "text-orange-500",
    },
  ] : [];

  return (
    <div className="p-4">
      <div className="flex flex-wrap gap-4">
        {cardData.map((card, idx) => (
          <CustomCard
            key={card.title}
            title={card.title}
            value={card.value}
            data={card.data}
            icon={card.icon}
            color={card.color}
            showBreakdown={expandedCards.has(idx)}
            className="p-2 cursor-pointer"
            onClick={() => toggleCardExpansion(idx)}
          />
        ))}
      </div>
      <h1 className="text-xl font-semibold mb-4">Food Quantity Distribution</h1>
      {/* 2x2 grid for charts */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-4">
        {/* Pie Chart */}
        <div className="w-full h-64">
          <PieChartTemplate data={sampleData} />
        </div>

        {/* Bar Graph */}
        <div className="flex w-full h-96">
          <BarGraphTemplate
            data={weeklyData}
            xKey="day"
            barKeys={["apples", "bananas", "cherries"]}
            config={{
              apples: { label: "Apples", color: "#FF6384" },
              bananas: { label: "Bananas", color: "#FFCE56" },
              cherries: { label: "Cherries", color: "#36A2EB" },
            }}
          />
        </div>

        {/* Line Graph 1 */}
        <div className="flex w-full h-96">
          <LineGraphTemplate
            data={incomeData}
            xKey="day"
            lineKeys={["cash", "online", "wallet"]}
          />
        </div>

        {/* Line Graph 2 */}
        <div className="flex w-full h-96">
          <LineGraphTemplate
            data={incomeData}
            xKey="day"
            lineKeys={["cash", "online", "wallet"]}
          />
        </div>
      </div>
    </div>
  );
}