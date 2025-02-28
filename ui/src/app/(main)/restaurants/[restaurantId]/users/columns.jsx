"use client";
import * as React from "react";
import {
  ArrowDown,
  ArrowUp,
  MoreHorizontal,
  Pencil,
  Trash2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Switch } from "@/components/ui/switch";
import Image from "next/image";
import { Badge } from "@/components/ui/badge";

export const getStaffMemberColumns = (handleSwitchToggle, handleEditClick, handleDeleteClick) => [
    {
      accessorKey: "image",
      header: "Image",
      cell: ({ row }) => (
        <div className="capitalize">
          <Image src={row.getValue("image")} alt="Staff Member" height={80} width={80} />
        </div>
      ),
    },
    {
      accessorKey: "userName",
      header: "Name",
      cell: ({ row }) => <div className="capitalize">{row.getValue("userName")}</div>,
    },
    {
      accessorKey: "mobile",
      header: "Mobile",
      cell: ({ row }) => <div>{row.getValue("mobile")}</div>,
    },
    {
      accessorKey: "role",
      header: "Role",
      cell: ({ row }) => <div className="capitalize">{row.getValue("role")}</div>,
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => (
        <div className="capitalize">{row.getValue("status")}</div>
      ),
    },
    {
      accessorKey: "shift",
      header: "Shift",
      cell: ({ row }) => <div className="capitalize">{row.getValue("shift")}</div>,
    },
    {
      accessorKey: "active",
      header: "Active",
      cell: ({ row }) => (
        <Switch
          checked={row.original.status === "active"}
          onCheckedChange={(value) =>
            handleSwitchToggle(row.original.userName, value)
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
            <Button variant="ghost" onClick={() => handleEditClick(row.original.userName)}>
              <Pencil className="text-black h-50 w-50" />
            </Button>
            <Button variant="ghost" onClick={() => handleDeleteClick(row.original.userName)}>
              <Trash2 className="text-black h-50 w-50" />
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
  
