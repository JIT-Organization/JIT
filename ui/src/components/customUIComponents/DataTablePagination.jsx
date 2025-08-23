import React from "react";
import { Button } from "@/components/ui/button";

export default function DataTablePagination({ table }) {
  return (
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
        Page {table.getState().pagination.pageIndex + 1} of {table.getPageCount()} | Total Records:{" "}
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
  );
}
