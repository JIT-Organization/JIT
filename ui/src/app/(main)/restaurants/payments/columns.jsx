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

export const getPaymentsHistory = ( handleEditClick, handleDeleteClick) => [
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
      accessorKey: "customerName",
      header: "Customer Name",
      cell: ({ row }) => <div className="capitalize">{row.getValue("customerName")}</div>,
    },
    {
      accessorKey: "mobile",
      header: "Mobile",
      cell: ({ row }) => <div>{row.getValue("mobile")}</div>,
    },
    {
      accessorKey: "amount",
      header: "Amount",
      cell: ({ row }) => {
        const amount = parseFloat(row.getValue("amount"));
        const formattedAmount = new Intl.NumberFormat("en-IN", {
          style: "currency",
          currency: "INR",
        }).format(amount);
        return <div>{formattedAmount}</div>;
      },
    },
    {
      accessorKey: "time",
      header: "Time",
      cell: ({ row }) => <div>{row.getValue("time")}</div>,
    },
    {
      accessorKey: "id",
      header: "ID",
      cell: ({ row }) => <div>{row.getValue("id")}</div>,
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => (
        <div className="flex space-x-2">
          <Button 
            variant="ghost" 
            colorVariant="none"
            onClick={() => handleEditClick(row.original.id)}
          >
            <Pencil className="h-50 w-50" />
          </Button>
          <Button 
            variant="ghost" 
            colorVariant="none"
            onClick={() => handleDeleteClick(row.original.id)}
          >
            <Trash2 className="text-red-600 h-50 w-50" />
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
  