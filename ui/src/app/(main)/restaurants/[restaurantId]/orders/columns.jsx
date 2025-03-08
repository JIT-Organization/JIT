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

export const getOrderColumns = ( handleEditClick, handleDeleteClick) => [
    {
      accessorKey: "orderNumber",
      header: "Order Number",
      cell: ({ row }) => <div>{row.getValue("orderNumber")}</div>,
    },
    {
      accessorKey: "tableNumber",
      header: "Table Number",
      cell: ({ row }) => <div className="capitalize">{row.getValue("tableNumber")}</div>,
    },
    {
      accessorKey: "customer",
      header: "Customer",
      cell: ({ row }) => <div className="capitalize">{row.getValue("customer")}</div>,
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => <div>{row.getValue("status")}</div>,
    },
    {
      accessorKey: "serveTime",
      header: "Serve Time",
      cell: ({ row }) => <div>{row.getValue("serveTime")}</div>,
    },
    {
      accessorKey: "notes",
      header: "Notes",
      cell: ({ row }) => <div>{row.getValue("notes")}</div>,
    },
    {
      accessorKey: "payment",
      header: "Payment",
      cell: ({ row }) => <div>{row.getValue("payment")}</div>,
      onClick: (rowData) => {
        console.log("Payment clicked for:", rowData);
      }
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <div className="flex space-x-2">
          <Button variant="ghost" onClick={() => handleEditClick(row.original.id)}>
            <Pencil className="text-black h-50 w-50" />
          </Button>
          <Button variant="ghost" onClick={() => handleDeleteClick(row.original.id)}>
            <Trash2 className="text-black h-50 w-50" />
          </Button>
        </div>
      ),
    },
    // {
    //   id: "dotActions",
    //   enableHiding: false,
    //   cell: ({ row }) => {
    //     const order = row.original;
  
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
    //             onClick={() => navigator.clipboard.writeText(order.id)}
    //           >
    //             Copy order ID
    //           </DropdownMenuItem>
    //           <DropdownMenuSeparator />
    //           <DropdownMenuItem>View order details</DropdownMenuItem>
    //           <DropdownMenuItem>Change order status</DropdownMenuItem>
    //         </DropdownMenuContent>
    //       </DropdownMenu>
    //     );
    //   },
    // },
  ];
  