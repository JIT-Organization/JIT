"use client";
import * as React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export const getPaymentsHistory = (handleEditClick, handleDeleteClick) => [
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
        <Button variant="ghost" onClick={() => handleEditClick(row.original.id)}>
          <Pencil className="text-black h-50 w-50" />
        </Button>
        <Button variant="ghost" onClick={() => handleDeleteClick(row.original.id)}>
          <Trash2 className="text-black h-50 w-50" />
        </Button>
      </div>
    ),
  }
];
