"use client";
import { getStaffMemberColumns } from "./columns";
import { data } from "./data";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useState } from "react";

const Users = () => {
  const [tableData, setTableData] = useState([...data]);

  const handleToggle = (userName, value) => {
    console.log(userName, value);
    setTableData((prev) =>
      prev.map((row) => (row.userName === userName ? { ...row, status: value ? "active" : "inactive" } : row))
    );
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
  }

  const columns = getStaffMemberColumns(handleToggle, handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(tableData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tableData}
        tabName="Users"
        handleHeaderButtonClick={() => {
          console.log("Header Button Clicked");
        }}
        headerButtonName="Add User"
      />
    </div>
  );
};

export default Users;
