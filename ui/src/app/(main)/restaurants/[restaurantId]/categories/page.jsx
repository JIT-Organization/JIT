"use client";
import { getCategoryColumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useRouter } from "next/navigation";



import { useState } from "react";

const Categories = () => {
  const [tableData, setTableData] = useState([...data]);
  const router = useRouter();



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
          router.push("/addFood"); // Change to the desired route
          console.log("Add category URL");
        }}
        headerButtonName="Add Category"
      />
    </div>
  );
};

export default Categories;
