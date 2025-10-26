'use client'
import useWebSocket from "@/lib/utils/webSocketUtils";
import { createContext, useContext } from "react";

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ url, children }) => {
  const webSocket = useWebSocket(url);

  return (
    <WebSocketContext.Provider value={webSocket}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useSocket = () => {
  const context = useContext(WebSocketContext);
  if (context === null) {
    throw new Error("useSocket must be used within a WebSocketProvider");
  }
  return context;
};