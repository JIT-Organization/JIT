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

export const getMenuListcolumns = (handleSwitchToggle, handleEditClick, handleDeleteClick) => [
  {
    accessorKey: "image",
    header: "Image",
    cell: ({ row }) => (
      <div className="capitalize">
        <Image src={row.getValue("image")} alt="Food" height={80} width={80} />
      </div>
    ),
  },
  {
    accessorKey: "name",
    header: "Name",
    cell: ({ row }) => <div className="capitalize">{row.getValue("name")}</div>,
  },
  {
    accessorKey: "cooks",
    header: "Cooks",
    cell: ({ row }) => (
      <div className="capitalize">{row.getValue("cooks")}</div>
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
        onCheckedChange={(value) => handleSwitchToggle(row.original.id, value)}
      />
    ),
  },
  {
    accessorKey: "category",
    header: "Categories",
    cell: ({ row }) => {
      const categories = row.getValue("category");
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
    accessorKey: "email",
    header: ({ column }) => {
      const isSorted = column.getIsSorted();

      return (
        <Button
          variant="ghost"
          onClick={() => {
            if (isSorted === "asc") {
              column.clearSorting();
            } else {
              column.toggleSorting(!isSorted);
            }
          }}
        >
          Email
          {isSorted === "asc" ? <ArrowUp className="ml-2" /> : null}
          {isSorted === "desc" ? <ArrowDown className="ml-2" /> : null}
        </Button>
      );
    },
    cell: ({ row }) => <div className="lowercase">{row.getValue("email")}</div>,
  },
  {
    id: "actions",
    header: "Actions",
    cell: ({ row }) => {
      return (
        <div className="flex space-x-2">
          <Button variant="ghost" onClick={() => handleEditClick(row.original.id)}>
            <Pencil className="text-black h-50 w-50" />
          </Button>
          <Button variant="ghost" onClick={() => handleDeleteClick(row.original.id)}>
            <Trash2 className="text-black h-50 w-50" />
          </Button>
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
