"use client";
import { getPaymentsHistory } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { useState } from "react";

const Payments = () => {
  const [tableData, setTableData] = useState([...data]);

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getPaymentsHistory(handleEditClick, handleDeleteClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Payment History"
      />
    </div>
  );
};

export default Payments;
