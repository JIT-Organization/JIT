"use client";
import { useState, useMemo } from "react";
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
  FaListAlt,
} from "react-icons/fa"; 
import {
  Sidebar,
  SidebarContent,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
} from "@/components/ui/sidebar";
import Link from "next/link";
import AppBar from "@/components/ui/custom/AppBar";

export default function RootLayout({ children }) {
  const [activePage, setActivePage] = useState("/dashboard");

  const sidebarLinks = useMemo(
    () => [
      { href: "/dashboard", label: "Dashboard", icon: <FaTachometerAlt /> },
      { href: "/restaurants/1/kitchen", label: "Kitchen", icon: <FaUtensils /> },
      { href: "/restaurants/1/menu", label: "Our Menu", icon: <FaCartPlus /> },
      { href: "/restaurants/1/createOrder", label: "New Order", icon: <FaClipboardList /> },
      { href: "/restaurants/1/orders", label: "View Orders", icon: <FaListAlt /> },
      { href: "/restaurants/1/payments", label: "Payment History", icon: <FaCreditCard /> },
      { href: "/restaurants/1/categories", label: "Categories", icon: <FaLayerGroup /> },
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
      <SidebarProvider>
        <AppBar />
        <Sidebar className="pt-16"  collapsible="icon"  aria-label="Sidebar">
          <SidebarContent>
          <SidebarMenu>
            {sidebarLinks.map(({ href, label, icon }, index) => (
              <SidebarMenuItem key={index}>
                <SidebarMenuButton asChild>
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
                    <span className="ml-3">{label}</span>
                  </Link>
                </SidebarMenuButton>

              </SidebarMenuItem>
            ))}
          </SidebarMenu>

          </SidebarContent>
        </Sidebar>

        <main className="flex-1 flex flex-col pt-16">
          <div className="flex-1 p-2">{children}</div>
        </main>
      </SidebarProvider>
    </>
  );
}
