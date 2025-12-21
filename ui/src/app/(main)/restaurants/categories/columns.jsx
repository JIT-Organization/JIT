"use client";
import * as React from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getCategoryColumns = (
  handleSwitchToggle,
  handleEditClick,
  handleDeleteClick,
  onSubmit,
  options
) => [
  {
    accessorKey: "categoryName",
    header: "Category Name",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("categoryName")}</div>
    ),
  },
  {
    accessorKey: "foodCount",
    header: "Food Count",
    cell: ({ row }) => <div>{row.getValue("foodCount")}</div>,
  },
  {
    accessorKey: "isPublic",
    header: "Public",
    cell: ({ row }) => (
      <Switch
        checked={row.original.isPublic}
        onCheckedChange={(value) => handleSwitchToggle(row.original.categoryName, value)}
      />
    ),
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => (
      <div className="flex space-x-2">
        <CustomPopup
          type="category"
          trigger={
            <Button variant="ghost" colorVariant="none">
              <Pencil className="h-5" />
            </Button>
          }
          dialogDescription={"Category Info"}
          data={handleEditClick(row)}
          onSubmit={onSubmit(row.original)}
          selectOptions={options}
        />
        <CustomPopup
          type="delete"
          trigger={
            <Button
              variant="ghost"
              colorVariant="none"
            >
              <Trash2 className="text-red-600 h-5" />
            </Button>
          }
          onConfirm={() => handleDeleteClick(row.original.categoryName)}
        />
      </div>
    ),
  },
];
