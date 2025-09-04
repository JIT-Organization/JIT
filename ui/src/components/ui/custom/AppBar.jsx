import { FaMapMarkerAlt, FaBell, FaUserCircle, FaCog, FaSignOutAlt, FaChevronDown } from "react-icons/fa";
import { HiOutlineLogout, HiOutlineCog } from "react-icons/hi";
import Image from "next/image";
import { CustomTrigger } from "./CustomTrigger";
import { useState, useRef, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useToast } from "@/hooks/use-toast";
import NotificationBell from "@/components/notifications/NotificationBell"; 

export default function AppBar() {
  const [isProfileDropdownOpen, setIsProfileDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const router = useRouter();
  const { toast } = useToast();

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsProfileDropdownOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleLogout = () => {
    try {
      // Clear authentication data
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      sessionStorage.clear();
      
      // Clear cookies if any
      document.cookie.split(";").forEach((c) => {
        const eqPos = c.indexOf("=");
        const name = eqPos > -1 ? c.substr(0, eqPos) : c;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
      });

      toast({
        variant: "success",
        title: "Success",
        description: "Logged out successfully"
      });

      // Redirect to login
      router.push("/login");
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Error during logout"
      });
    }
  };

  const handleSettingsClick = () => {
    // Navigate to settings page
    router.push("/restaurants/settings");
    setIsProfileDropdownOpen(false);
  };
  return (
    <div className="app-bar bg-gray-800 text-white p-4 flex justify-between items-center shadow-md w-full fixed top-0 left-0 z-20">
      <div className="flex items-center">
        <div className="flex items-center space-x-2">
          <CustomTrigger />
          <Image src="/favicon.ico" className="pl-2" alt="Logo" width={35} height={40} />
          <span className="font-bold text-lg pl-2">Business Name</span>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        <FaMapMarkerAlt className="text-white text-2xl" />

        <NotificationBell />

        {/* User Profile Dropdown */}
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setIsProfileDropdownOpen(!isProfileDropdownOpen)}
            className="flex items-center hover:bg-gray-700 rounded-lg p-1 transition-colors duration-200"
          >
            <div className="relative w-8 h-8 rounded-xl overflow-hidden border-2 border-white shadow-md cursor-pointer">
              {/* Show image if available */}
              <Image
                src="/resources/images/profile.jpeg"
                alt="User Avatar"
                width={32}
                height={32}
                className="w-full h-full object-cover rounded-xl"
                onError={(e) => {
                  // Hide image and show initials fallback
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'flex';
                }}
              />
              {/* Fallback to initials when image fails to load */}
              <div className="hidden w-full h-full bg-white items-center justify-center text-gray-800 font-bold text-sm rounded-xl">
                JD
              </div>
            </div>
          </button>

          {/* Dropdown Menu */}
          {isProfileDropdownOpen && (
            <div className="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-30 animate-in slide-in-from-top-2 duration-200">
              {/* User Info Header */}
              <div className="px-4 py-4 border-b border-gray-100">
                <div className="flex items-center space-x-4">
                  <div className="relative w-12 h-12 rounded-xl overflow-hidden border-2 border-gray-200 shadow-sm">
                    <Image
                      src="/resources/images/profile.jpeg"
                      alt="User Avatar"
                      width={48}
                      height={48}
                      className="w-full h-full object-cover rounded-xl"
                      onError={(e) => {
                        e.target.style.display = 'none';
                        e.target.nextSibling.style.display = 'flex';
                      }}
                    />
                    <div className="hidden items-center justify-center w-full h-full bg-white text-gray-800 font-bold text-lg rounded-xl">
                      JD
                    </div>
                    <div className="absolute bottom-0 right-0 w-4 h-4 bg-green-400 border-2 border-white rounded-full"></div>
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="text-base font-semibold text-gray-900 truncate">John Doe</h4>
                    <p className="text-sm text-gray-500 truncate">Administrator</p>
                    <p className="text-sm text-gray-400 truncate">john.doe@company.com</p>
                  </div>
                </div>
              </div>
              
              {/* Menu Items */}
              <div className="py-2">
                <button
                  onClick={handleSettingsClick}
                  className="flex items-center w-full px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors duration-150 group"
                >
                  <div className="flex items-center justify-center w-8 h-8 bg-gray-100 rounded-lg mr-3 group-hover:bg-blue-100 transition-colors duration-150">
                    <HiOutlineCog className="text-gray-600 text-lg group-hover:text-blue-600" />
                  </div>
                  <div className="flex-1 text-left">
                    <p className="font-medium">Settings</p>
                    <p className="text-xs text-gray-500">Manage your account</p>
                  </div>
                </button>
                
                <div className="border-t border-gray-100 my-2"></div>
                
                <button
                  onClick={handleLogout}
                  className="flex items-center w-full px-4 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors duration-150 group"
                >
                  <div className="flex items-center justify-center w-8 h-8 bg-red-100 rounded-lg mr-3 group-hover:bg-red-200 transition-colors duration-150">
                    <HiOutlineLogout className="text-red-600 text-lg" />
                  </div>
                  <div className="flex-1 text-left">
                    <p className="font-medium">Sign out</p>
                    <p className="text-xs text-red-500">Sign out of your account</p>
                  </div>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
