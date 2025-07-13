"use client";
import * as React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import AddOnInput from "@/components/AddOnInput";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getAddOnColumns = (
  handleEditClick,
  handleDeleteClick,
  onSubmit
) => [
  {
    accessorKey: "label",
    header: "Add-On Label",
    cell: ({ row }) => <div>{row.getValue("label")}</div>,
  },
  {
    accessorKey: "price",
    header: "Price",
    cell: ({ row }) => <div>{row.getValue("price") ?? "-"}</div>,
  },
  {
    accessorKey: "options",
    header: "Options",
    cell: ({ row }) => (
      <div>
        {row.original.options && row.original.options.length > 0
          ? row.original.options.map((opt, idx) => (
              <div key={idx}>{opt.name} ({opt.price})</div>
            ))
          : "-"}
      </div>
    ),
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => (
      <div className="flex space-x-2">
                <CustomPopup
          type="add-on"
          trigger={
            <Button variant="ghost" colorVariant="none">
              <Pencil className="h-50 w-50" />
            </Button>
          }
          dialogDescription={"Edit Add-On"}
          data={handleEditClick(row.original)}
          onSubmit={onSubmit(row.original)}
        />
        <Button
          variant="ghost" 
          colorVariant="none"
          onClick={() => handleDeleteClick(row.original.label)}
        >
          <Trash2 className="text-red-600 h-50 w-50" />
        </Button>
      </div>
    ),
  },
]; 