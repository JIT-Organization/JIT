"use client";
import { useState } from 'react';
import { FaTachometerAlt, FaUtensils, FaCartPlus, FaClipboardList, FaCreditCard, FaLayerGroup, FaUsers, FaTable, FaCog, FaBell, FaMapMarkerAlt, FaUserCircle } from 'react-icons/fa';  // Import icons
import { Sidebar, SidebarContent, SidebarProvider } from '@/components/ui/sidebar';
import { Button } from "@/components/ui/button"; 
import Link from 'next/link';

export default function RootLayout({ children }) {
  const [isCollapsed, setIsCollapsed] = useState(false);  // State for collapsing
  const [activePage, setActivePage] = useState('/dashboard');  // Track the active page

  const toggleSidebar = () => {
    setIsCollapsed(!isCollapsed);  // Toggle between collapsed and expanded
  };

  // Function to handle page click, set the active page
  const handleLinkClick = (page) => {
    setActivePage(page);  // Set the clicked page as active
  };

  return (
    <>
      {/* App Bar - Full Width */}
      <div className="bg-gray-800 text-white p-4 flex justify-between items-center shadow-md w-full fixed top-0 left-0 z-20">
        <div className="flex items-center">
          {/* Logo and Business Name */}
          <div className="flex items-center space-x-2">
            <img src="/logo.png" alt="Business Logo" className="w-8 h-8" />
            <span className="font-bold text-lg">Business Name</span>
            <Button variant="outline" onClick={toggleSidebar} className="text-black border-white bg-white">
              {'☰'}
            </Button>
          </div>
        </div>

        {/* Right Side: User Profile, Location, Notifications */}
        <div className="flex items-center space-x-4">
          {/* Location Icon */}
          <FaMapMarkerAlt className="text-white text-2xl" />

          {/* Notifications Icon */}
          <div className="relative">
            <FaBell className="text-white text-2xl" />
            <span className="absolute top-0 right-0 bg-red-500 text-xs text-white rounded-full px-1">3</span> {/* Example for notifications */}
          </div>

          {/* User Profile */}
          <FaUserCircle className="text-white text-3xl" />
        </div>
      </div>

      <SidebarProvider>
        {/* Content Container */}
        <div className="flex relative min-h-screen pt-16"> {/* pt-16 to offset the fixed app bar */}
          
          {/* Sidebar Below the App Bar */}
          <Sidebar className="mt-16"> {/* mt-16 ensures sidebar starts below the app bar */}
            <SidebarContent>
              <ul>
                <li>
                  <Link 
                    href="/dashboard" 
                    className={`flex items-center text-xl ${activePage === '/dashboard' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/dashboard')}
                  >
                    <FaTachometerAlt className="mr-3 text-2xl" />
                    {!isCollapsed && ' Dashboard'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/kitchen" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/kitchen' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/kitchen')}
                  >
                    <FaUtensils className="mr-3 text-2xl" />
                    {!isCollapsed && ' Kitchen'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/menu" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/menu' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/menu')}
                  >
                    <FaCartPlus className="mr-3 text-2xl" />
                    {!isCollapsed && ' Our Menu'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/createOrder" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/createOrder' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/createOrder')}
                  >
                    <FaClipboardList className="mr-3 text-2xl" />
                    {!isCollapsed && ' New Order'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/orders" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/orders' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/orders')}
                  >
                    <FaClipboardList className="mr-3 text-2xl" />
                    {!isCollapsed && ' View Orders'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/payments" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/payments' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/payments')}
                  >
                    <FaCreditCard className="mr-3 text-2xl" />
                    {!isCollapsed && ' Payment History'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/categories" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/categories' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/categories')}
                  >
                    <FaLayerGroup className="mr-3 text-2xl" />
                    {!isCollapsed && ' Categories'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/users" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/users' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/users')}
                  >
                    <FaUsers className="mr-3 text-2xl" />
                    {!isCollapsed && ' Users'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/tables" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/tables' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/tables')}
                  >
                    <FaTable className="mr-3 text-2xl" />
                    {!isCollapsed && ' Tables'}
                  </Link>
                </li>
                <li>
                  <Link 
                    href="/restaurants/1/settings" 
                    className={`flex items-center text-xl ${activePage === '/restaurants/1/settings' ? 'bg-gray-400' : ''}`}
                    onClick={() => handleLinkClick('/restaurants/1/settings')}
                  >
                    <FaCog className="mr-3 text-2xl" />
                    {!isCollapsed && ' Settings'}
                  </Link>
                </li>
              </ul>
            </SidebarContent>
          </Sidebar>

          {/* Main Content Area */}
          <div className="flex-1 flex flex-col">
            {/* Main Content */}
            <div className="flex-1 p-6">
              {children}
            </div>
          </div>
        </div>
      </SidebarProvider>
    </>
  );
}
