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
import CustomPopup from "@/components/customUIComponents/CustomPopup";

export const getTableColumns = ( handleEditClick, handleDeleteClick) => [
    {
      accessorKey: "tableNumber",
      header: "Table Number",
      cell: ({ row }) => <div className="capitalize">{row.getValue("tableNumber")}</div>,
    },
    {
      accessorKey: "seatingCapacity",
      header: "Seating Capacity",
      cell: ({ row }) => <div>{row.getValue("seatingCapacity")}</div>,
    },
    {
      accessorKey: "availability",
      header: "Availability",
      cell: ({ row }) => (
        <div className="capitalize">
          {row.getValue("availability") ? "Available" : "Not Available"}
        </div>
      ),
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => {
        return (
          <div className="flex space-x-2">
            <CustomPopup
                    type="user"
                    trigger={
                      <Button variant="ghost">
                        <Pencil className="text-black h-50 w-50" />
                      </Button>
                    }
                    dialogDescription={"User Info"}
                  />
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
    //     const table = row.original;
  
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
    //             onClick={() => navigator.clipboard.writeText(table.id)}
    //           >
    //             Copy table ID
    //           </DropdownMenuItem>
    //           <DropdownMenuSeparator />
    //           <DropdownMenuItem>View table details</DropdownMenuItem>
    //           <DropdownMenuItem>Change availability</DropdownMenuItem>
    //         </DropdownMenuContent>
    //       </DropdownMenu>
    //     );
    //   },
    // },
  ]
  