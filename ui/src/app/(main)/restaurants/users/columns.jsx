"use client";
import * as React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import Image from "next/image";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getStaffMemberColumns = (
  handleSwitchToggle,
  handleEditClick,
  handleDeleteClick,
  onSubmit
) => [
  {
    accessorKey: "profilePictureUrl",
    header: "Image",
    cell: ({ row }) => (
      <div className="capitalize">
        <Image
          src={row.getValue("profilePictureUrl")}
          alt="Staff Member"
          height={80}
          width={80}
        />
      </div>
    ),
  },
  {
    accessorKey: "name",
    header: "Name",
    cell: ({ row }) => {
      return (
        <div className="capitalize">
          {row.original.firstName + " " + row.original.lastName}
        </div>
      );
    },
  },
  {
    accessorKey: "username",
    header: "Username",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("username")}</div>
    ),
  },
  {
    accessorKey: "phoneNumber",
    header: "Mobile",
    cell: ({ row }) => <div>{row.getValue("phoneNumber")}</div>,
  },
  {
    accessorKey: "role",
    header: "Role",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("role").toLowerCase()}</div>
    ),
  },
  {
    accessorKey: "shift",
    header: "Shift",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("shift")}</div>
    ),
  },
  {
    accessorKey: "active",
    header: "Active",
    cell: ({ row }) => (
      <Switch
        checked={row.original.isActive}
        onCheckedChange={(value) =>
          handleSwitchToggle(row.original.username, value)
        }
      />
    ),
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <CustomPopup
            type="user"
            trigger={
              <Button variant="ghost" colorVariant="none">
                <Pencil className="h-50 w-50" />
              </Button>
            }
            dialogDescription={"User Info"}
            data={handleEditClick(row.original)}
            onSubmit={onSubmit(row.original)}
            // selectOptions={options}
          />
          <Button
            variant="ghost"
            colorVariant="none"
            onClick={() => handleDeleteClick(row.original.id)}
          >
            <Trash2 className="text-red-600 h-50 w-50" />
          </Button>
        </div>
      );
    },
  },
  // {
  //   id: "dotActions",
  //   enableHiding: false,
  //   cell: ({ row }) => {
  //     const staff = row.original;

  //     return (
  //       <DropdownMenu>
  //         <DropdownMenuTrigger asChild>
  //           <Button variant="ghost" className="h-8 w-8 p-0">
  //             <span className="sr-only">Open menu</span>
  //             <MoreHorizontal />
  //           </Button>
  //         </DropdownMenuTrigger>
  //         <DropdownMenuContent align="end">
  //           <DropdownMenuLabel>Actions</DropdownMenuLabel>
  //           <DropdownMenuItem
  //             onClick={() => navigator.clipboard.writeText(staff.userName)}
  //           >
  //             Copy user name
  //           </DropdownMenuItem>
  //           <DropdownMenuSeparator />
  //           <DropdownMenuItem>View profile</DropdownMenuItem>
  //           <DropdownMenuItem>View shift details</DropdownMenuItem>
  //         </DropdownMenuContent>
  //       </DropdownMenu>
  //     );
  //   },
  // },
];
