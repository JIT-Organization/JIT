"use client";
import { getMenuListcolumns } from "@/app/samplePosts/columns";
import PostsList from "../PostList";
import { CustomDataTable } from "../customUIComponents/CustomDataTable";
import { data } from "@/app/samplePosts/data";
import { useState } from "react";
import { getDistinctCategories } from "@/lib/utils/helper";

export default function PostsPage() {

  return (
    <div>
      <h1>Posts</h1>
      <CustomDataTable
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
