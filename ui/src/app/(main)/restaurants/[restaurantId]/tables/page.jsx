"use client";
import { getTableColumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useState } from "react";

const tables = () => {
  const [tableData, setTableData] = useState([...data]);
  
  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getTableColumns( handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(tableData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Tables"
        handleHeaderButtonClick={() => {
          console.log("Add Table URL");
        }}
        headerButtonName="Add Table"
        
      />
    </div>
  );
};

export default tables;
