"use client";
import * as React from "react";
import { ChevronDown, ChevronUp, Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export const getOrderColumns = (handleEditClick, handleDeleteClick) => [
  {
    accessorKey: "orderNumber",
    header: "Order Number",
    cell: ({ row }) => <div>{row.getValue("orderNumber")}</div>,
  },
  {
    accessorKey: "tableNumber",
    header: "Table Number",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("tableNumber")}</div>
    ),
  },
  {
    accessorKey: "customer",
    header: "Customer",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("customer")}</div>
    ),
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
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => (
      <div className="flex space-x-2 items-center justify-center">
        <Button
          variant="ghost"
          onClick={() => handleEditClick(row.original.orderNumber)}
        >
          <Pencil className="text-black h-50 w-50" />
        </Button>
        <Button
          variant="ghost"
          onClick={() => handleDeleteClick(row.original.id)}
        >
          <Trash2 className="text-black h-50 w-50" />
        </Button>
        {row.getIsExpanded() ? <ChevronUp className="opacity-40"/> : <ChevronDown className="opacity-40"/>}
      </div>
    ),
  },
];
