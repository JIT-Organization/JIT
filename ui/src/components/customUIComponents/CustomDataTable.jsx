"use client";
import * as React from "react";
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
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
  categories,
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
      <div className="flex items-center justify-between py-4">
        <Button variant="ghost" onClick={handlePreviousClick}>
          <ChevronLeft />
          {tabName}
        </Button>
        <div className="flex ">
          <Input
            placeholder="Search across all columns..."
            value={globalFilter}
            onChange={(event) => setGlobalFilter(event.target.value)}
            className="max-w-md w-96"
          />
          <Button variant="ghost">
            <Filter />
          </Button>
          {buttonDisplayPermissions[permissionIdentifier] && (headerDialogType && headerButtonName ? (
            <CustomPopup
              type={headerDialogType}
              trigger={<Button>{headerButtonName}</Button>}
              onSubmit={onSubmitClick}
              selectOptions={selectOptions}
            />
          ) : (
            headerButtonName && (
              <Button onClick={handleHeaderButtonClick}>{headerButtonName}</Button>
            )
          ))}
        </div>
      </div>
      {categories && (
        <div>
          <div className="flex space-x-5 ml-4">
            {categories.map((category) => (
              <div
                key={category}
                className="cursor-pointer"
                onClick={() => handleActiveToggle(category)}
              >
                <div className="text-sm">{category}</div>
                <div
                  className={`w-full border-b-4 border-black transition-transform duration-300 ease-in-out origin-center ${
                    activeCategory === category ? "scale-x-100" : "scale-x-0"
                  }`}
                ></div>
              </div>
            ))}
          </div>
          <Separator className="mb-4" />
        </div>
      )}
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
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  onClick={handleRowClick}
                  data-state={row.getIsSelected() && "selected"}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-between space-x-2 py-4">
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          Previous
        </Button>
        <span className="text-sm font-medium">
          Page {table.getState().pagination.pageIndex + 1} of{" "}
          {table.getPageCount()} | Total Records:{" "}
          {table.getPrePaginationRowModel().rows.length}
        </span>
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
