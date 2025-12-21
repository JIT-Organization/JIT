"use client";
import * as React from "react";
import {
  Pencil,
  Trash2,
  Loader2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import Image from "next/image";
import { Badge } from "@/components/ui/badge";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getMenuListcolumns = (
  handleSwitchToggle,
  handleEditClick,
  handleDeleteClick,
  loadingStates = { isDeleting: () => false, isUpdating: () => false }
) => [
  {
    accessorKey: "image",
    header: "Image",
    cell: ({ row }) => (
      <div className="capitalize">
        <Image
          src={
            row.getValue("image") ||
            "https://images.pexels.com/photos/1860208/pexels-photo-1860208.jpeg?cs=srgb&dl=cooked-food-1860208.jpg&fm=jpg"
          }
          alt="Food"
          height={80}
          width={80}
        />
      </div>
    ),
  },
  {
    accessorKey: "menuItemName",
    header: "Name",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("menuItemName")}</div>
    ),
  },
  {
    accessorKey: "cookSet",
    header: "Cooks",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("cookSet")}</div>
    ),
  },
  {
    accessorKey: "price",
    header: () => <div className="text-right">Price</div>,
    cell: ({ row }) => {
      const price = parseFloat(row.getValue("price"));
      const formatted = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "INR",
      }).format(price);

      return <div className="text-right font-medium">{formatted}</div>;
    },
  },
  {
    accessorKey: "offerPrice",
    header: () => <div className="text-right">Offer Price</div>,
    cell: ({ row }) => {
      const offerPrice = row.getValue("offerPrice");
      if (offerPrice === null || offerPrice === undefined) {
      return <div className="text-center font-medium">--</div>;
      }
      const formatted = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "INR",
      }).format(parseFloat(offerPrice));

      return <div className="text-right font-medium">{formatted}</div>;
    },
  },
  {
    accessorKey: "active",
    header: "Active",
    cell: ({ row }) => {
      const menuItemName = row.original.menuItemName;
      const isDeleting = loadingStates.isDeleting(menuItemName);
      const isUpdating = loadingStates.isUpdating(menuItemName);
      return (
        <div className="flex items-center space-x-2">
          <Switch
            checked={row.original.active}
            onCheckedChange={(value) => handleSwitchToggle(row.index, value)}
            disabled={isUpdating || isDeleting}
          />
          {isUpdating && <Loader2 className="h-4 w-4 animate-spin" />}
        </div>
      );
    },
  },
  {
    accessorKey: "categorySet",
    header: "Categories",
    cell: ({ row }) => {
      const categories = row.getValue("categorySet");
      return (
        <div className="flex items-end">
          <div className="flex flex-col space-y-2">
            {categories?.slice(0, 2).map((category, index) => (
              <div key={index} className="capitalize">
                <Badge>{category}</Badge>
              </div>
            ))}
          </div>
          {categories?.length > 2 && (
            <div className="capitalize">+{categories.length - 2}</div>
          )}
        </div>
      );
    },
    filterFn: "includesString",
  },
  {
    id: "actions",
    header: <div className="flex justify-center">Actions</div>,
    cell: ({ row }) => {
      const menuItemName = row.original.menuItemName;
      const foodType = row.original.foodType;
      const isDeleting = loadingStates.isDeleting(menuItemName);
      const isUpdating = loadingStates.isUpdating(menuItemName);

      return (
        <div className="flex justify-center">
          <Button
            className="cursor-pointer hover:bg-gray-600/10 h-10 w-10 flex justify-center items-center rounded-md"
            variant="ghost"
            colorVariant="none"
            onClick={() => handleEditClick(menuItemName,foodType)}
            disabled={isDeleting || isUpdating}
          >
            <Pencil className="h-5" />
          </Button>
          <CustomPopup
            type="delete"
            trigger={
              <Button
                className="cursor-pointer hover:bg-gray-600/20 h-10 w-10 flex justify-center items-center rounded-md"
                variant="ghost"
                colorVariant="none"
                disabled={isDeleting || isUpdating}
              >
                {isDeleting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Trash2 className="text-red-600 h-5" />}
              </Button>
            }
            onConfirm={() => handleDeleteClick(menuItemName, foodType)}
          />
        </div>
      );
    },
  },
];
