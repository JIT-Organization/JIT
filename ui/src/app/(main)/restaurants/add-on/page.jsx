"use client";
import { getAddOnColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { data as addOnData } from "./data";
import { useState } from "react";
const AddOns = () => {
  const [data, setData] = useState(addOnData);

  const handleEditClick = (row) => row;

  const handleDeleteClick = (label) => {
    setData((prev) => prev.filter((item) => item.label !== label));
  };

  const handleUpdate = (row) => (values) => {
    setData((prev) =>
      prev.map((item) => (item.label === row.label ? { ...item, ...values } : item))
    );
  };

  const handleCreate = (values) => {
    setData((prev) => [
      ...prev,
      { ...values},
    ]);
    setShowAddDialog(false);
  };

  const columns = getAddOnColumns(handleEditClick, handleDeleteClick, handleUpdate);

  return (
    <div>
        <CustomDataTable
          columns={columns}
          data={data}
          tabName="Add-Ons"
          headerButtonName="Add Add-On"
          headerDialogType="add-on"
          onSubmitClick={(data) => console.log(data)}
        />
    </div>
  );
};

export default AddOns; 