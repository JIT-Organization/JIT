"use client";
import React from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ChevronLeft, Filter, Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import CustomPopup from "./CustomPopup";
import { Separator } from "../ui/separator";

export default function DataTableHeader({
  tabName,
  globalFilter,
  setGlobalFilter,
  headerButtonName,
  headerButtonClick,
  headerDialogType,
  categories = [],
  activeCategory,
  setActiveCategory,
  setColumnFilters,
  onSubmitClick,
  selectOptions,
  disabled = false, // Add disabled prop for loading state
}) {
  const router = useRouter();

  const handlePreviousClick = () => router.back();

  const handleActiveToggle = (label) => {
    setActiveCategory(label);
    if (label === "All") {
      setColumnFilters([]);
    } else {
      setColumnFilters([{ id: "categorySet", value: label }]);
    }
  };

  return (
    <>
      <div className="flex items-center justify-between py-4">
        <div className="flex items-center ml-2 space-x-3">
          <Button variant="ghost" onClick={handlePreviousClick} >
            <ChevronLeft />
          </Button>
          <span className="ml-3 text-2xl font-semibold">{tabName}</span>
        </div>
        <div className="flex space-x-2 items-center">
          <Input
            placeholder="Search across all columns..."
            value={globalFilter}
            onChange={(e) => setGlobalFilter(e.target.value)}
            className="max-w-md w-96"
          />
          <Button variant="ghost" colorVariant="none">
            <Filter />
          </Button>
          {headerDialogType && headerButtonName ? (
            <CustomPopup
              onSubmit={onSubmitClick}
              selectOptions={selectOptions}
              type={headerDialogType}
              trigger={<Button>{headerButtonName}</Button>}
            />
          ) : (
            headerButtonName && (
              <Button 
                onClick={headerButtonClick}
                disabled={disabled}
                className="min-w-[120px]"
              >
                {disabled ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Processing...
                  </>
                ) : (
                  headerButtonName
                )}
              </Button>
            )
          )}
        </div>
      </div>

      {categories.length > 0 && (
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
                />
              </div>
            ))}
          </div>
          <Separator className="mb-4" />
        </div>
      )}
    </>
  );
}
