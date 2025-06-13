import { FaMapMarkerAlt, FaBell, FaUserCircle } from "react-icons/fa";
import Image from "next/image";
import { CustomTrigger } from "./CustomTrigger"; 

export default function AppBar() {
  return (
    <div className="bg-gray-800 text-white p-4 flex justify-between items-center shadow-md w-full fixed top-0 left-0 z-20">
      <div className="flex items-center">
        <div className="flex items-center space-x-2">
          <CustomTrigger />
          <Image src="/favicon.ico" className="pl-2" alt="Logo" width={35} height={40} />
          <span className="font-bold text-lg pl-2">Business Name</span>
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
  );
}
