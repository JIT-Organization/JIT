"use client";
import * as React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getTableColumns = (
  handleEditClick,
  handleDeleteClick,
  onSubmit
) => [
  {
    accessorKey: "tableNumber",
    header: "Table Number",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("tableNumber")}</div>
    ),
  },
  {
    accessorKey: "chairs",
    header: "Seating Capacity",
    cell: ({ row }) => <div className="pl-2">{row.getValue("chairs")}</div>,
  },
  {
    accessorKey: "isAvailable",
    header: "Availability",
    cell: ({ row }) => {
      console.log(row.getValue("isAvailable"));
      return (
        <div>{row.getValue("isAvailable") ? "Available" : "Not Available"}</div>
      );
    },
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <CustomPopup
            type="table"
            trigger={
              <Button variant="ghost" colorVariant="none">
                <Pencil className="h-50 w-50" />
          </Button>
            }
            // dialogDescription={"Tables Info"}
            data={handleEditClick(row.original)}
            onSubmit={onSubmit(row.original)}
          />
          <CustomPopup
            type="delete"
            trigger={
              <Button variant="ghost" colorVariant="none">
                <Trash2 className="text-red-600 h-50 w-50" />
              </Button>
            }
            onConfirm={() => handleDeleteClick(row.original.tableNumber)}
          />
        </div>
      );
    },
  },
];
