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
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getCategoryColumns = ( handleSwitchToggle, handleEditClick, handleDeleteClick ) => [
    {
      accessorKey: "categoryName",
      header: "Category Name",
      cell: ({ row }) => <div className="capitalize">{row.getValue("categoryName")}</div>,
    },
    {
      accessorKey: "foodCount",
      header: "Food Count",
      cell: ({ row }) => <div>{row.getValue("foodCount")}</div>,
    },
    {
      accessorKey: "public",
      header: "Public",
      cell: ({ row }) => (
        <Switch
                checked={row.original.public}
                onCheckedChange={(value) => handleSwitchToggle(row.original.id, value)}
              />
        // <div>{row.getValue("public") ? "Yes" : "No"}</div>
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
                    <Button variant="ghost">
                      <Pencil className="text-black h-50 w-50" />
                    </Button>
                  }
                  dialogDescription={"Category Info"}
                />
          {/* <Button variant="ghost" onClick={() => handleEditClick(row.original.id)}>
            <Pencil className="text-black h-50 w-50" />
          </Button> */}
          <Button variant="ghost" onClick={() => handleDeleteClick(row.original.id)}>
            <Trash2 className="text-black h-50 w-50" />
          </Button>
        </div>
      ),
    },
    // {
    //   id: "dotActions",
    //   enableHiding: false,
    //   cell: ({ row }) => {
    //     const category = row.original;
  
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
    //             onClick={() => navigator.clipboard.writeText(category.id)}
    //           >
    //             Copy category ID
    //           </DropdownMenuItem>
    //           <DropdownMenuSeparator />
    //           <DropdownMenuItem>Edit category</DropdownMenuItem>
    //           <DropdownMenuItem>Change category visibility</DropdownMenuItem>
    //         </DropdownMenuContent>
    //       </DropdownMenu>
    //     );
    //   },
    // },
  ];
  