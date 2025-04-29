"use client";
import {
  createCategory,
  deleteCategoryItem,
  getCategoriesListOptions,
  getMenuItemListOptions,
  patchUpdateCategoriesList,
} from "@/lib/api/api";
import { getCategoryColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useMemo } from "react";
import { getSelectOptions } from "@/lib/utils/helper";

const Categories = () => {
  const queryClient = useQueryClient();
  const { data: categoriesList, isLoading, error } = useQuery(getCategoriesListOptions());
  const patchMutation = useMutation(patchUpdateCategoriesList(queryClient));
  const deleteMutation = useMutation(deleteCategoryItem(queryClient));
  const postMutation = useMutation(createCategory(queryClient));
  const { data: menuItemsList } = useQuery(getMenuItemListOptions())

  const selectOptions = useMemo(() => {
    return getSelectOptions(menuItemsList || [])
  }, [menuItemsList])

  if (isLoading) return <p>Loading categories...</p>;
  if (error) return <p>Error loading categories: {error.message}</p>;

  const handleToggle = (categoryName, value) => {
    patchMutation.mutate({ categoryName, fields: { isPublic: value } });
  };

  const handleEditClick = (row) => {
    const obj = {
      categoryName: row.original.categoryName,
      foodItems: row.original.foodItems || [],
      visibility: row.original.isPublic ? "public" : "private",
    };
    return obj;
  };

  const onUpdateSubmit = (row) => (values) => {
    const formValues = { ...values, isPublic: values.visibility === "public" };
    const categoryName = row.categoryName;
    delete formValues["visibility"];
    const formValueKeys = Object.keys(formValues);
    formValueKeys.forEach((key) => {
      if (Array.isArray(row[key]) && Array.isArray(formValues[key])) {
        if (JSON.stringify(row[key]) === JSON.stringify(formValues[key])) {
          delete formValues[key];
        }
      }
      if (row[key] === formValues[key]) {
        delete formValues[key];
      }
    });
    if (Object.keys(formValues).length !== 0)
      patchMutation.mutate({ categoryName, fields: { ...formValues } });
  };

  const onAddSubmit = (values) => {
    const formValues = { ...values, isPublic: values.visibility === "public" };
    delete formValues["visibility"];
    postMutation.mutate({ fields: formValues });
  };

  const handleDeleteClick = (categoryName) => {
    deleteMutation.mutate({ categoryName });
  };

  const columns = getCategoryColumns(
    handleToggle,
    handleEditClick,
    handleDeleteClick,
    onUpdateSubmit,
    selectOptions
  );

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={categoriesList || []}
        tabName="Categories"
        headerButtonName="Add Category"
        headerDialogType="category"
        onSubmitClick={onAddSubmit}
        selectOptions={selectOptions}
      />
    </div>
  );
};

export default Categories;
