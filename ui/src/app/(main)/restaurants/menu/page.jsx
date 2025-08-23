"use client";
import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getMenuListcolumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteMenuItem, getMenuItemListOptions, patchUpdateMenuItemList } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useToast } from "@/hooks/use-toast";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";

const MenuList = () => {
  const router = useRouter();
  const pathName = usePathname();
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [loadingItems, setLoadingItems] = useState({});
  const clearLoadingItem = (menuItemName) =>
    setLoadingItems((prev) => {
      const newState = { ...prev };
      delete newState[menuItemName];
      return newState;
    });
  const { data: menuItems, isLoading, error } = useQuery(getMenuItemListOptions());
  const patchMutation = useMutation({
    ...patchUpdateMenuItemList(queryClient),
    onMutate: ({ menuItemName }) => {
      setLoadingItems((prev) => ({ ...prev, [menuItemName]: "updating" }));
    },
    onSuccess: (_, { menuItemName }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Menu item status updated successfully",
      });
      clearLoadingItem(menuItemName);
    },
    onError: (error, { menuItemName }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update menu item status",
        variant: "destructive",
      });
      clearLoadingItem(menuItemName);
    },
  });

  const deleteMutation = useMutation({
    ...deleteMenuItem(queryClient),
    onMutate: ({ menuItemName }) => {
      setLoadingItems((prev) => ({ ...prev, [menuItemName]: "deleting" }));
    },
    onSuccess: (_, { menuItemName }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Menu item deleted successfully",
      });
      clearLoadingItem(menuItemName);
    },
    onError: (error, { menuItemName }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete menu item",
        variant: "destructive",
      });
      clearLoadingItem(menuItemName);
    },
  });

  if (isLoading) {
    return <LoadingState message="Loading menu items..." />;
  }

  if (error) {
    return <ErrorState title="Error loading menu items" message={error.message} />;
  }

  if (!menuItems?.length) {
    return <ErrorState title="No Menu Items" message="No items found in the menu list." />;
  }

  const handleToggle = (index, value) => {
    const menuItem = menuItems[index];
    const menuItemName = menuItem.menuItemName;
    const foodType = menuItem.foodType;
    patchMutation.mutate({ menuItemName, foodType, fields: { active: value } });
  };

  const handleEditClick = (menuItemName, foodType) => {
    router.push(`${pathName}/${foodType}/${menuItemName}`);
  };

  const handleDeleteClick = async (menuItemName, foodType) => {
    deleteMutation.mutate({ menuItemName, foodType });
  };

  const columns = getMenuListcolumns(
    handleToggle,
    handleEditClick,
    handleDeleteClick,
    {
      isDeleting: (menuItemName) => loadingItems[menuItemName] === 'deleting',
      isUpdating: (menuItemName) => loadingItems[menuItemName] === 'updating'
    }
  );

  const categories = getDistinctCategories(menuItems);
  const handleHeaderButtonClick = () => {
    router.push(`${pathName}/${'add_food'}`)
  };

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={menuItems}
        tabName="Our Menu"
        handleHeaderButtonClick={handleHeaderButtonClick}
        headerButtonName="Add Food"
        categories={categories}
      />
    </div>
  );
};

export default MenuList;
