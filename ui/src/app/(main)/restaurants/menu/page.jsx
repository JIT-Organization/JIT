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

  const handleToggle = async (id, value) => {
    patchMutation.mutate({ id, fields: { active: value } });
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id, pathName);
    router.push(`${pathName}/${id}`)
  };

  const handleDeleteClick = async (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  };

  const columns = getMenuListcolumns(
    handleToggle,
    handleEditClick,
    handleDeleteClick
  );

  const categories = getDistinctCategories(menuItemListData);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={menuItemListData || []}
        tabName="Our Menu"
        handleHeaderButtonClick={() => {
          console.log("Header Button Clicked");
        }}
        headerButtonName="Add Food"
        categories={categories}
      />
    </div>
  );
};

export default MenuList;
