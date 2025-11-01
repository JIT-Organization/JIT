"use client";
import BusinessProfilePage from "@/components/SettingsForm";
import RolesPermissions from "@/components/RolesPermissions";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Building2, ShieldCheck } from "lucide-react";

export default function SettingsPage() {
  return (
    <div className="p-6">
      <h1 className="text-4xl font-bold mb-6">Settings</h1>
      
      <Tabs defaultValue="business-profile" className="w-full">
        <TabsList className="mb-6 bg-transparent border-b border-gray-200 rounded-none h-auto p-0">
          <TabsTrigger 
            value="business-profile" 
            className="data-[state=active]:border-b-2 data-[state=active]:border-green-500 rounded-none px-6 py-3 text-base font-semibold data-[state=active]:bg-transparent data-[state=active]:shadow-none"
          >
            <Building2 className="w-5 h-5 mr-2" />
            Business Profile
          </TabsTrigger>
          <TabsTrigger 
            value="roles-permissions" 
            className="data-[state=active]:border-b-2 data-[state=active]:border-green-500 rounded-none px-6 py-3 text-base font-semibold data-[state=active]:bg-transparent data-[state=active]:shadow-none"
          >
            <ShieldCheck className="w-5 h-5 mr-2" />
            Roles & Permissions
          </TabsTrigger>
        </TabsList>
        
        <TabsContent value="business-profile" className="mt-0">
          <BusinessProfilePage />
        </TabsContent>
        
        <TabsContent value="roles-permissions" className="mt-0">
          <RolesPermissions />
        </TabsContent>
      </Tabs>
    </div>
  );
}