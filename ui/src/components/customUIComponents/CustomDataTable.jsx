"use client";
import React from "react";
import {
  useReactTable,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  flexRender,
} from "@tanstack/react-table";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableHeader from "./DataTableHeader";
import DataTablePagination from "./DataTablePagination";
import { ChevronLeft, Filter } from "lucide-react";
import { useRouter } from "next/navigation";
import { Separator } from "../ui/separator";
import CustomPopup from "./CustomPopup";
import { checkPermission } from "@/lib/utils/checkPerimission";

export function CustomDataTable({
  columns = [],
  data = [],
  handleRowClick = () => {},
  tabName,
  handleHeaderButtonClick = () => {},
  headerButtonName,
  headerDialogType,
  categories = [],
  expandableRowContent = () => {},
  onSubmitClick,
  selectOptions,
  permissionIdentifier
}) {
  const [sorting, setSorting] = React.useState([]);
  const [columnFilters, setColumnFilters] = React.useState([]);
  const [columnVisibility, setColumnVisibility] = React.useState({});
  const [rowSelection, setRowSelection] = React.useState({});
  const [globalFilter, setGlobalFilter] = React.useState("");
  const [activeCategory, setActiveCategory] = React.useState("All");

  const table = useReactTable({
    data,
    columns,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    getGlobalFilteredRowModel: getFilteredRowModel(),
    onGlobalFilterChange: setGlobalFilter,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection,
      globalFilter,
    },
  });

  const router = useRouter();

  const handlePreviousClick = () => {
    router.back();
  };

  const handleActiveToggle = (label) => {
    setActiveCategory(label);
    if (label === "All") {
      setColumnFilters([]);
    } else {
      setColumnFilters([{ id: "categorySet", value: label }]);
    }
  };

  const buttonDisplayPermissions = {
    users: checkPermission("P101")
  }

  return (
    <div className="w-full">
      <DataTableHeader
        tabName={tabName}
        globalFilter={globalFilter}
        setGlobalFilter={setGlobalFilter}
        headerButtonName={headerButtonName}
        headerButtonClick={handleHeaderButtonClick}
        headerDialogType={headerDialogType}
        categories={categories}
        activeCategory={activeCategory}
        setActiveCategory={setActiveCategory}
        setColumnFilters={setColumnFilters}
        onSubmitClick={onSubmitClick}
        selectOptions={selectOptions}
      />

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows.length ? (
              table.getRowModel().rows.map((row) => (
                <React.Fragment key={row.id}>
                  <TableRow
                    onClick={() => handleRowClick(row)}
                    data-state={row.getIsSelected() && "selected"}
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell key={cell.id} className="text-center">
                        {flexRender(
                          cell.column.columnDef.cell,
                          cell.getContext()
                        )}
                      </TableCell>
                    ))}
                  </TableRow>
                  {row.getIsExpanded() && (
                    <TableRow>
                      <TableCell colSpan={row.getVisibleCells().length}>
                        {expandableRowContent(row)}
                      </TableCell>
                    </TableRow>
                  )}
                </React.Fragment>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <DataTablePagination table={table} />
    </div>
  );
}
