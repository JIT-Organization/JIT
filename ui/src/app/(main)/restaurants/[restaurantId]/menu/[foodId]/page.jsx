'use client'

import CustomPopup from "@/components/customUIComponents/CustomPopup";

export default function Food() {
  return (
    <div className="flex flex-col">
      <h2>Food Item</h2>
      <CustomPopup
        type="delete"
        trigger={<div>Delete</div>}
        onConfirm={() => console.log("Delete Clicked")}
        dialogDescription={"Delete confirmation"}
      />

      <CustomPopup
        type="category"
        trigger={<div>Category</div>}
        dialogDescription={"Category Info"}
      />

      <CustomPopup
        type="user"
        trigger={<div>User</div>}
        dialogDescription={"User Info"}
      />

      <CustomPopup
        type="table"
        trigger={<div>Table</div>}
        dialogDescription={"Table Info"}
      />
    </div>
  );
}
