"use client";
import { getCategoryColumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useState } from "react";

const categories = () => {
  const [tableData, setTableData] = useState([...data]);

  const handleToggle = (id, value) => {
    console.log(id, value);
    setTableData((prev) =>
      prev.map((row) => (row.id === id ? { ...row, public: value } : row))
    );
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getCategoryColumns(handleToggle, handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(tableData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Categories"
        handleHeaderButtonClick={() => {
          console.log("Add category URL");
        }}
        headerButtonName="Add Category"
      />
    </div>
  );
};

export default categories;
