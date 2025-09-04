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
import { useMemo, useState } from "react";
import { getSelectOptions } from "@/lib/utils/helper";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";
import { useToast } from "@/hooks/use-toast";

const Categories = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [loadingItems, setLoadingItems] = useState({});

  const clearLoadingItem = (id) =>
    setLoadingItems((prev) => {
      const newState = { ...prev };
      delete newState[id];
      return newState;
    });

  const { data: categoriesList, isLoading, error } = useQuery(getCategoriesListOptions());
  
  const patchMutation = useMutation({
    ...patchUpdateCategoriesList(queryClient),
    onMutate: ({ id }) => {
      setLoadingItems((prev) => ({ ...prev, [id]: "updating" }));
    },
    onSuccess: (_, { id }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Category updated successfully",
      });
      clearLoadingItem(id);
    },
    onError: (error, { id }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update category",
        variant: "destructive",
      });
      clearLoadingItem(id);
    },
  });

  const deleteMutation = useMutation({
    ...deleteCategoryItem(queryClient),
    onMutate: ({ id }) => {
      setLoadingItems((prev) => ({ ...prev, [id]: "deleting" }));
    },
    onSuccess: (_, { id }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Category deleted successfully",
      });
      clearLoadingItem(id);
    },
    onError: (error, { id }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete category",
        variant: "destructive",
      });
      clearLoadingItem(id);
    },
  });

  const postMutation = useMutation({
    ...createCategory(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Category created successfully",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create category",
        variant: "destructive",
      });
    },
  });

  const { data: menuItemsList } = useQuery(getMenuItemListOptions())

  const selectOptions = useMemo(() => {
    console.log(menuItemsList)
    return getSelectOptions(menuItemsList || [])
  }, [menuItemsList])

  if (isLoading) {
    return <LoadingState message="Loading categories..." />;
  }

  if (error) {
    return <ErrorState title="Error loading categories" message={error.message} />;
  }

  if (!categoriesList?.length) {
    return <ErrorState title="No Categories" message="No categories found." />;
  }

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
