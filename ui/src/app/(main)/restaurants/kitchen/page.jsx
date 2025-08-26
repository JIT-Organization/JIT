"use client";
import React, { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { getMenuItemListOptions } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import FoodCard from "@/components/customUIComponents/FoodCard";
import DataTableHeader from "@/components/customUIComponents/DataTableHeader";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useIsMobile } from "@/hooks/use-mobile";

const CreateOrder = () => {
  const router = useRouter();
  const isMobile = useIsMobile();
  const [showPopup, setShowPopup] = useState(false);

  const { data: menuItems = [], isLoading, error } = useQuery(getMenuItemListOptions());
  const [globalFilter, setGlobalFilter] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

  const filteredMenuItems = useMemo(() => {
    return menuItems.filter((item) => {
      const categoryList = item?.categorySet ?? [];
      const matchesCategory = activeCategory === "All" || categoryList.includes(activeCategory);
      const matchesSearch = (item?.menuItemName ?? "")
        .toLowerCase()
        .includes(globalFilter.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [menuItems, globalFilter, activeCategory]);

  const categories = getDistinctCategories(menuItems);

  if (isLoading) return <p>Loading menu...</p>;
  if (error) return <p>Error loading menu: {error.message}</p>;

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 shadow bg-white">
        <DataTableHeader
          tabName="Kitchen Orders"
          globalFilter={globalFilter}
          setGlobalFilter={setGlobalFilter}
          categories={categories}
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
          setColumnFilters={() => {}}
        />
      </CardTitle>

      <CardContent className="mt-0 px-2 py-0">
        <div className="flex flex-1 overflow-hidden" style={{ height: "calc(100vh - 191px)" }}>
          <div className="flex-1 overflow-y-auto p-0 pb-4">
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
              {filteredMenuItems.map((food, index) => (
                <div
                  key={food.id + (index + 1)}
                  className="w-full"
                >
                  <FoodCard
                    food={food}
                    quantity={food.id} //qty update pannanum
                    mode="kitchen"
                    status="STARTED"
                    onActionClick={(id, x) => console.log(id, x)}
                  />
                </div>
              ))}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default CreateOrder;
