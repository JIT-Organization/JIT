"use client";
import { getOrderColumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useState } from "react";

const orders = () => {
  const [tableData, setTableData] = useState([...data]);

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getOrderColumns( handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(tableData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Orders"
        handleHeaderButtonClick={() => {
          console.log("Orders Page URL");
        }}
        headerButtonName="New Order"
      />
    </div>
  );
};

export default orders;
