"use client";
import { useState, useEffect, useMemo } from "react";
import {
  FaTachometerAlt,
  FaUtensils,
  FaCartPlus,
  FaClipboardList,
  FaCreditCard,
  FaLayerGroup,
  FaUsers,
  FaTable,
  FaCog,
  FaBell,
  FaMapMarkerAlt,
  FaUserCircle,
  FaListAlt,
} from "react-icons/fa"; // Import icons
import {
  Sidebar,
  SidebarContent,
  SidebarProvider,
} from "@/components/ui/sidebar";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import Image from "next/image";

export default function RootLayout({ children }) {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [activePage, setActivePage] = useState("/dashboard");

  const toggleSidebar = () => {
    setIsCollapsed(!isCollapsed);
  };

  const sidebarLinks = useMemo(
    () => [
      { href: "/dashboard", label: "Dashboard", icon: <FaTachometerAlt /> },
      {
        href: "/restaurants/1/kitchen",
        label: "Kitchen",
        icon: <FaUtensils />,
      },
      { href: "/restaurants/1/menu", label: "Our Menu", icon: <FaCartPlus /> },
      {
        href: "/restaurants/1/createOrder",
        label: "New Order",
        icon: <FaClipboardList />,
      },
      {
        href: "/restaurants/1/orders",
        label: "View Orders",
        icon: <FaListAlt />,
      },
      {
        href: "/restaurants/1/payments",
        label: "Payment History",
        icon: <FaCreditCard />,
      },
      {
        href: "/restaurants/1/categories",
        label: "Categories",
        icon: <FaLayerGroup />,
      },
      { href: "/restaurants/1/users", label: "Users", icon: <FaUsers /> },
      { href: "/restaurants/1/tables", label: "Tables", icon: <FaTable /> },
      { href: "/restaurants/1/settings", label: "Settings", icon: <FaCog /> },
    ],
    []
  );

  const handleLinkClick = (href) => {
    setActivePage(href);
  };

  return (
    <>
      <div className="bg-gray-800 text-white p-4 flex justify-between items-center shadow-md w-full fixed top-0 left-0 z-20">
        <div className="flex items-center">
          <div className="flex items-center space-x-2">
            <Image src="/favicon.ico" alt="Logo" width={20} height={20} />
            <span className="font-bold text-lg">Business Name</span>
            <Button
              variant="outline"
              onClick={toggleSidebar}
              className="text-black border-white bg-white"
              aria-label="Toggle sidebar"
            >
              {"☰"}
            </Button>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          <FaMapMarkerAlt className="text-white text-2xl" />

          <div className="relative">
            <FaBell className="text-white text-2xl" />
            <span className="absolute top-0 right-0 bg-red-500 text-xs text-white rounded-full px-1">
              3
            </span>
          </div>

          <FaUserCircle className="text-white text-3xl" />
        </div>
      </div>

      <SidebarProvider>
        <Sidebar
          className={`mt-16 transition-all duration-300 ${
            isCollapsed ? "w-16" : "w-64"
          }`}
          aria-label="Sidebar"
        >
          <SidebarContent>
            <ul>
              {sidebarLinks.map(({ href, label, icon }, index) => (
                <li key={index}>
                  <Link
                    href={href}
                    className={`sidebar-link flex items-center text-xl ${
                      activePage === href ? "bg-gray-400" : ""
                    }`}
                    aria-current={activePage === href ? "page" : undefined}
                    onClick={() => handleLinkClick(href)}
                  >
                    <div className="w-12 h-12 flex justify-center items-center">
                      {icon}
                    </div>
                    {!isCollapsed && <span className="ml-3">{label}</span>}
                  </Link>
                </li>
              ))}
            </ul>
          </SidebarContent>
        </Sidebar>

        <main className="flex-1 flex flex-col pt-16">
          <div className="flex-1 p-2">{children}</div>
        </main>
      </SidebarProvider>
    </>
  );
}
