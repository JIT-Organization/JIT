"use client"
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useEffect } from "react";

const page = () => {
  // Use the hook and get connection status
  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // Effect to log connection status changes from the component's perspective
  useEffect(() => {
    if (isConnected) {
      console.log("Component detected WebSocket connection.");
    } else {
      console.log("Component detected WebSocket disconnection.");
    }
  }, [isConnected]);


  // Effect for handling subscriptions
  useEffect(() => {
    // This will now only attempt to subscribe once the stable `subscribe` function is available.
    // The hook itself handles queuing if the connection is not ready.
    console.log("Setting up WebSocket subscriptions...");

    const handleUserEvent = (message) => {
      console.log("%cReceived message on /user/queue/orderItemCreated:", "color: #007bff;", message);
      // You might want to invalidate a query here to refresh data
      // queryClient.invalidateQueries(['orders']);
    };

    const handleRoleEvent = (message) => {
      console.log("%cReceived message on /topic/role:", "color: #6f42c1;", message);
    };

    const unsubUser = subscribe("/user/queue/orderItemCreated", handleUserEvent);
    const unsubTopic = subscribe("/topic/role", handleRoleEvent);

    // The cleanup function will be called when the component unmounts
    return () => {
      console.log("Cleaning up WebSocket subscriptions.");
      unsubUser();
      unsubTopic();
    };
  }, [subscribe]);
  return (
    <div>
      <div>Dashboard</div>
    </div>
  )
}

export default page;