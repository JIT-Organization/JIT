"use client";

import * as React from "react";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { Search, Plus, Shield, UserCog, Users, Briefcase, Calculator } from "lucide-react";
import { Checkbox } from "./ui/checkbox";

const roles = [
  { id: "owner", name: "Owner", icon: Shield },
  { id: "admin", name: "Admin", icon: UserCog },
  { id: "manager", name: "Manager", icon: Users },
  { id: "employee", name: "Employee", icon: Briefcase },
  { id: "accountant", name: "Accountant", icon: Calculator },
];

const permissions = {
  pointOfSale: {
    title: "Point of Sale",
    items: [
      { id: "take-orders", label: "Take orders" },
      { id: "process-payments", label: "Process payments" },
      { id: "apply-discounts", label: "Apply discounts" },
      { id: "issue-refunds", label: "Issue refunds" },
    ],
  },
  menuManagement: {
    title: "Menu Management",
    items: [
      { id: "add-categories", label: "Add/edit categories" },
      { id: "add-items", label: "Add/edit items" },
    ],
  },
  reporting: {
    title: "Reporting",
    items: [
      { id: "view-reports", label: "View sales reports" },
      { id: "export-reports", label: "Export reports" },
    ],
  },
  settings: {
    title: "Settings",
    items: [
      { id: "manage-users", label: "Manage users" },
      { id: "edit-roles", label: "Edit roles" },
    ],
  },
};

export default function RolesPermissions() {
  const [selectedRole, setSelectedRole] = React.useState("owner");
  const [searchQuery, setSearchQuery] = React.useState("");
  const [rolePermissions, setRolePermissions] = React.useState({
    owner: {
      "take-orders": true,
      "process-payments": true,
      "apply-discounts": true,
      "issue-refunds": true,
      "add-categories": true,
      "add-items": true,
      "view-reports": true,
      "export-reports": true,
      "manage-users": true,
      "edit-roles": true,
    },
    admin: {
      "take-orders": true,
      "process-payments": true,
      "apply-discounts": true,
      "issue-refunds": false,
      "add-categories": true,
      "add-items": true,
      "view-reports": true,
      "export-reports": false,
      "manage-users": false,
      "edit-roles": false,
    },
  });

  const togglePermission = (permissionId) => {
    setRolePermissions((prev) => ({
      ...prev,
      [selectedRole]: {
        ...prev[selectedRole],
        [permissionId]: !prev[selectedRole]?.[permissionId],
      },
    }));
  };

  const handleSaveChanges = () => {
    console.log("Saving permissions:", rolePermissions);
    // TODO: Implement API call to save permissions
  };

  const filteredRoles = roles.filter((role) =>
    role.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const selectedRoleData = roles.find((role) => role.id === selectedRole);
  const currentPermissions = rolePermissions[selectedRole] || {};

  return (
    <div className="flex gap-6 h-full">
      {/* Left Sidebar - Roles List */}
      <div className="w-80 bg-gray-50 rounded-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold">Roles</h2>
          <Button
            size="sm"
            className="bg-green-500 hover:bg-green-600 text-white"
          >
            <Plus className="w-4 h-4 mr-1" />
            Add Role
          </Button>
        </div>

        {/* Search */}
        <div className="relative mb-4">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <Input
            type="text"
            placeholder="Find a role"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10 bg-white"
          />
        </div>

        {/* Roles List */}
        <div className="space-y-2">
          {filteredRoles.map((role) => {
            const Icon = role.icon;
            return (
              <button
                key={role.id}
                onClick={() => setSelectedRole(role.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                  selectedRole === role.id
                    ? "bg-green-100 text-green-800 font-semibold"
                    : "bg-white hover:bg-gray-100 text-gray-700"
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{role.name}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Right Panel - Permissions */}
      <div className="flex-1 bg-white rounded-lg p-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold mb-2">
              Editing Permissions:{" "}
              <span className="text-green-600">{selectedRoleData?.name}</span>
            </h2>
            {selectedRole === "owner" && (
              <p className="text-gray-500 text-sm">
                All permissions are enabled and cannot be changed for the Owner
                role.
              </p>
            )}
          </div>
          <Button
            onClick={handleSaveChanges}
            className="bg-green-500 hover:bg-green-600 text-white font-semibold px-6"
          >
            Save Changes
          </Button>
        </div>

        {/* Permissions Grid */}
        <div className="space-y-8">
          {Object.entries(permissions).map(([key, section]) => (
            <div key={key}>
              <h3 className="text-xl font-bold mb-4">{section.title}</h3>
              <div className="grid grid-cols-2 gap-6">
                {section.items.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                  >
                    <label
                      htmlFor={item.id}
                      className="text-gray-700 cursor-pointer flex-1"
                    >
                      {item.label}
                    </label>
                    <Checkbox
                      id={item.id}
                      checked={currentPermissions[item.id] || false}
                      onCheckedChange={() => togglePermission(item.id)}
                      disabled={selectedRole === "owner"}
                      className="data-[state=checked]:bg-green-500 data-[state=checked]:border-green-500"
                    />
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
