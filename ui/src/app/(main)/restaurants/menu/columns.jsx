"use client";
import * as React from "react";
import {
  Pencil,
  Trash2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import Image from "next/image";
import { Badge } from "@/components/ui/badge";
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getMenuListcolumns = (
  handleSwitchToggle,
  handleEditClick,
  handleDeleteClick
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
      const offerPrice = parseFloat(row.getValue("offerPrice"));
      const formatted = new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "INR",
      }).format(offerPrice);

      return <div className="text-right font-medium">{formatted}</div>;
    },
  },
  {
    accessorKey: "active",
    header: "Active",
    cell: ({ row }) => (
      <Switch
        checked={row.original.active}
        onCheckedChange={(value) => handleSwitchToggle(row.index, value)}
      />
    ),
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
      return (
        <div className="flex justify-center">
          <Button className="cursor-pointer hover:bg-gray-600/10 h-10 w-10 flex justify-center items-center rounded-md" variant="ghost" onClick={() => handleEditClick(row.original.menuItemName)}>
            <Pencil className="text-black h-5" />
          </Button>
          <CustomPopup
            type="delete"
            trigger={
              <Button className="cursor-pointer hover:bg-gray-600/20 h-10 w-10 flex justify-center items-center rounded-md" variant="ghost">
                <Trash2 className="text-black h-5" />
              </Button>
            }
            onConfirm={() => handleDeleteClick(row.original.menuItemName)}
          />
        </div>
      );
    },
  },
  // {
  //   id: "dotActions",
  //   enableHiding: false,
  //   cell: ({ row }) => {
  //     const payment = row.original;

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
  //             onClick={() => navigator.clipboard.writeText(payment.id)}
  //           >
  //             Copy payment ID
  //           </DropdownMenuItem>
  //           <DropdownMenuSeparator />
  //           <DropdownMenuItem>View customer</DropdownMenuItem>
  //           <DropdownMenuItem>View payment details</DropdownMenuItem>
  //         </DropdownMenuContent>
  //       </DropdownMenu>
  //     );
  //   },
  // },
];
