"use client";
import { getMenuListcolumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteMenuItem, getMenuItemListOptions, patchUpdateMenuItemList } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { usePathname, useRouter } from "next/navigation";

const MenuList = () => {
  const router = useRouter();
  const pathName = usePathname();
  const queryClient = useQueryClient();
  const { data: menuItemListData, isLoading, error } = useQuery(getMenuItemListOptions());
  const patchMutation = useMutation(patchUpdateMenuItemList(queryClient));
  const deleteMutation = useMutation(deleteMenuItem(queryClient));

  if (isLoading) return <p>Loading menu items...</p>;
  if (error) return <p>Error loading menu items: {error.message}</p>;

  const handleToggle = async (index, value) => {
    const menuItemName = menuItemListData[index].menuItemName
    console.log(menuItemName)
    patchMutation.mutate({ menuItemName, fields: { active: value } });
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id, pathName);
    router.push(`${pathName}/${id}`)
  };

  const handleDeleteClick = async (menuItemName) => {
    console.log("Delete clicked for menuItemName: ", menuItemName);
    deleteMutation.mutate({ menuItemName });
  };

  const columns = getMenuListcolumns(
    handleToggle,
    handleEditClick,
    handleDeleteClick
  );

  const handleHeaderButtonClick = () => {
    router.push(`${pathName}/${'add_food'}`)
  };

  const categories = getDistinctCategories(menuItemListData);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={menuItemListData || []}
        tabName="Our Menu"
        handleHeaderButtonClick={handleHeaderButtonClick}
        headerButtonName="Add Food"
        categories={categories}
      />
    </div>
  );
};

export default MenuList;
