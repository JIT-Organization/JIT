"use client";
import { getMenuListcolumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useState } from "react";

const MenuList = () => {
  const [tableData, setTableData] = useState([...data]);

  const handleToggle = (id, value) => {
    console.log(id, value);
    setTableData((prev) =>
      prev.map((row) => (row.id === id ? { ...row, active: value } : row))
    );
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getMenuListcolumns(handleToggle, handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(tableData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Our Menu"
        handleHeaderButtonClick={() => {
          console.log("Add Food page URL");
        }}
        headerButtonName="Add Food"
        categories={categories}
      />
    </div>
  );
};

export default MenuList;
