"use client";
import { useState, useMemo, useEffect } from "react";
import { usePathname } from "next/navigation";
import {
  FaTachometerAlt,
  FaCreditCard,
  FaLayerGroup,
  FaUsers,
  FaTable,
  FaCog,
} from "react-icons/fa";
import { GiCook } from "react-icons/gi";
import { MdAddCircleOutline, MdRestaurantMenu, MdAddShoppingCart, MdListAlt } from "react-icons/md";
import {
  Sidebar,
  SidebarContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  useSidebar,
} from "@/components/ui/sidebar";
import Link from "next/link";
import AppBar from "@/components/ui/custom/AppBar";
import { NotificationProvider, useNotifications } from "@/contexts/NotificationContext";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";

// Separate component for WebSocket logic to access notification context
function WebSocketHandler() {
  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");
  const { addNotification } = useNotifications();

  useEffect(() => {
    if (isConnected) {
      console.log("Global WebSocket connected.");
    } else {
      console.log("Global WebSocket disconnected.");
    }
  }, [isConnected]);

  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up global WebSocket subscriptions...");

    // Handle role updates (topic messages - broadcast to all users)
    const handleRoleEvent = (message) => {
      console.log("%cReceived message on /topic/role:", "color: #6f42c1;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ROLE_UPDATE,
        message: message.content || `Role updated: ${message.role || 'Unknown'}`,
        priority: 'medium',
        data: message
      });
    };

    // Handle system alerts
    const handleSystemAlert = (message) => {
      console.log("%cReceived system alert:", "color: #dc3545;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.SYSTEM_ALERT,
        message: message.content || message.message || 'System alert received',
        priority: 'high',
        data: message
      });
    };

    // Handle menu updates
    const handleMenuUpdate = (message) => {
      console.log("%cReceived menu update:", "color: #28a745;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.MENU_UPDATE,
        message: message.content || 'Menu has been updated',
        priority: 'medium',
        data: message
      });
    };

    // Handle kitchen status updates
    const handleKitchenStatusUpdate = (message) => {
      console.log("%cReceived kitchen status update:", "color: #ff6b35;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.KITCHEN_STATUS,
        message: message.content || message.message || 'Kitchen status updated',
        priority: message.priority || 'medium',
        data: message
      });
    };

    // Handle payment system status updates
    const handlePaymentStatusUpdate = (message) => {
      console.log("%cReceived payment system status:", "color: #27ae60;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.PAYMENT_STATUS,
        message: message.content || message.message || 'Payment system status updated',
        priority: message.priority || 'high',
        data: message
      });
    };

    // Handle general announcements
    const handleAnnouncement = (message) => {
      console.log("%cReceived announcement:", "color: #3498db;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ANNOUNCEMENT,
        message: message.content || message.message || 'New announcement',
        priority: message.priority || 'medium',
        data: message
      });
    };

    // Handle user role changes
    const handleUserRoleChanged = (message) => {
      console.log("%cReceived user role change:", "color: #9b59b6;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.ROLE_UPDATE,
        message: message.content || `Your role has been changed to ${message.newRole || 'updated'}`,
        priority: 'high',
        data: message
      });
    };

    // Handle shift updates
    const handleShiftUpdate = (message) => {
      console.log("%cReceived shift update:", "color: #f39c12;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.SHIFT_UPDATE,
        message: message.content || message.message || 'Shift schedule updated',
        priority: 'medium',
        data: message
      });
    };

    // Handle table assignments
    const handleTableAssigned = (message) => {
      console.log("%cReceived table assignment:", "color: #8e44ad;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.TABLE_ASSIGNED,
        message: message.content || `Table ${message.tableName || message.tableNumber || 'Unknown'} has been assigned to you`,
        priority: 'high',
        data: message
      });
    };

    // Subscribe to general topics (broadcast messages)
    const unsubRole = subscribe("/topic/role", handleRoleEvent);
    const unsubSystem = subscribe("/topic/system", handleSystemAlert);
    const unsubMenu = subscribe("/topic/menu", handleMenuUpdate);
    const unsubKitchenStatus = subscribe("/topic/kitchen/status", handleKitchenStatusUpdate);
    const unsubPaymentStatus = subscribe("/topic/payment/status", handlePaymentStatusUpdate);
    const unsubAnnouncement = subscribe("/topic/announcement", handleAnnouncement);

    // Subscribe to user-specific queues (personal notifications)
    const unsubUserRoleChanged = subscribe("/user/queue/userRoleChanged", handleUserRoleChanged);
    const unsubShiftUpdate = subscribe("/user/queue/shiftUpdate", handleShiftUpdate);
    const unsubTableAssigned = subscribe("/user/queue/tableAssigned", handleTableAssigned);

    return () => {
      console.log("Cleaning up global WebSocket subscriptions.");
      unsubRole();
      unsubSystem();
      unsubMenu();
      unsubKitchenStatus();
      unsubPaymentStatus();
      unsubAnnouncement();
      unsubUserRoleChanged();
      unsubShiftUpdate();
      unsubTableAssigned();
    };
  }, [subscribe, addNotification]);

  return null; // This component doesn't render anything
}

function SidebarNavigation({ activePage, setActivePage, sidebarLinks }) {
  const { open: isOpen } = useSidebar();

  return (
    <SidebarContent className="overflow-x-hidden">
      <SidebarMenu>
        {sidebarLinks.map(({ href, label, icon }, index) => (
          <SidebarMenuItem key={index}>
            <SidebarMenuButton asChild>
              <Link
                href={href}
                className={`menu-item sidebar-item flex items-center text-xl transition-all duration-300 ease-in-out rounded-lg ${activePage === href ? "active" : ""}`}
                aria-current={activePage === href ? "page" : undefined}
                onClick={() => setActivePage(href)}
              >
                <div className={`flex justify-center items-center transition-all duration-300 ${
                  isOpen ? 'w-10 h-12' : ''
                }`}>
                  <span className={`transition-all duration-300 ${
                    isOpen ? 'text-xl' : 'text-2xl'
                  }`}>{icon}</span>
                </div>
                { isOpen && <span className="ml-2 text-lg transition-all duration-300 text-lg" >{label}</span>}
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        ))}
      </SidebarMenu>
      <style jsx global>{`
        .menu-item {
          transition: background-color 0.3s ease, transform 0.3s ease;
        }
        .menu-item:hover {
          transform: scale(1.05);
        }
      `}</style>
    </SidebarContent>
  );
}

function LayoutContent({ children }) {
  const pathname = usePathname();
  const [activePage, setActivePage] = useState(pathname);

  // Update activePage when URL changes
  useEffect(() => {
    setActivePage(pathname);
  }, [pathname]);

  const sidebarLinks = useMemo(
    () => [
      { href: "/dashboard", label: "Dashboard", icon: <FaTachometerAlt /> },
      { href: "/restaurants/kitchen", label: "Kitchen", icon: <GiCook /> },
      { href: "/restaurants/menu", label: "Our Menu", icon: <MdRestaurantMenu /> },
      { href: "/restaurants/createOrder", label: "New Order", icon: <MdAddShoppingCart /> },
      { href: "/restaurants/orders", label: "View Orders", icon: <MdListAlt /> },
      { href: "/restaurants/payments", label: "Payment History", icon: <FaCreditCard /> },
      { href: "/restaurants/categories", label: "Categories", icon: <FaLayerGroup /> },
      { href: "/restaurants/users", label: "Users", icon: <FaUsers /> },
      { href: "/restaurants/tables", label: "Tables", icon: <FaTable /> },
      { href: "/restaurants/add-on", label: "Add-Ons", icon: <MdAddCircleOutline /> },
      { href: "/restaurants/settings", label: "Settings", icon: <FaCog /> },
    ],
    []
  );

  return (
    <>
      <SidebarProvider>
        <WebSocketHandler />
        <AppBar />
        <Sidebar className="pt-20 overflow-x-hidden sidebar-theme" collapsible="icon" aria-label="Sidebar">
          <SidebarNavigation 
            activePage={activePage}
            setActivePage={setActivePage}
            sidebarLinks={sidebarLinks}
          />
        </Sidebar>

        <main className="flex-1 flex flex-col pt-20">
          <div className="flex-1 p-2">{children}</div>
        </main>
      </SidebarProvider>
    </>
  );
}

export default function RootLayout({ children }) {
  return (
    <NotificationProvider>
      <LayoutContent>{children}</LayoutContent>
    </NotificationProvider>
  );
}
