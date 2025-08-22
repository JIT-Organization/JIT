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
          colorVariant="none"
          onClick={(e) => {
            e.stopPropagation();
            handleEditClick(row.original.orderNumber);
          }}
        >
          <Pencil className="h-5" />
        </Button>
        <Button
          variant="ghost"
          colorVariant="none"
          onClick={(e) => {
            e.stopPropagation();
            handleDeleteClick(row.original.id);
          }}
        >
          <Trash2 className="text-red-600 h-5" />
        </Button>
        {row.getIsExpanded() ? <ChevronUp className="opacity-40"/> : <ChevronDown className="opacity-40"/>}
      </div>
    ),
  },
];
