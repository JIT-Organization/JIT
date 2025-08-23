"use client";
import { useState, useMemo } from "react";
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

export default function RootLayout({ children }) {
  const [activePage, setActivePage] = useState("/dashboard");

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
        <AppBar />
        <Sidebar className="pt-16 overflow-x-hidden sidebar-theme" collapsible="icon" aria-label="Sidebar">
          <SidebarNavigation 
            activePage={activePage}
            setActivePage={setActivePage}
            sidebarLinks={sidebarLinks}
          />
        </Sidebar>

        <main className="flex-1 flex flex-col pt-16">
          <div className="flex-1 p-2">{children}</div>
        </main>
      </SidebarProvider>
    </>
  );
}
