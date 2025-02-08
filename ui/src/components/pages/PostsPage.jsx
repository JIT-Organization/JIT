"use client";
import { getMenuListcolumns } from "@/app/samplePosts/columns";
import PostsList from "../PostList";
import { DataTable } from "../customUIComponents/DataTable";
import { data } from "@/app/samplePosts/data";
import { useState } from "react";
import { getDistinctCategories } from "@/lib/utils/helper";

export default function PostsPage() {
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
      <h1>Posts</h1>
      <DataTable
        columns={columns}
        data={tableData}
        tabName="Our Menu"
        handleHeaderButtonClick={() => {
          console.log("Header Button Clicked");
        }}
        headerButtonName="Add Food"
        categories={categories}
      />
      <PostsList />
    </div>
  );
}
