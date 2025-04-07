"use client";
import { getStaffMemberColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteUserItem, getUsersListOptions, patchUpdateUserItemList } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { usePathname, useRouter } from "next/navigation";

const Users = () => {
  const router = useRouter();
  const pathName = usePathname();
  const id = pathName.split("/")[2];
  const queryClient = useQueryClient();
  const { data: usersListData, isLoading, error } = useQuery(getUsersListOptions(id));
  const patchMutation = useMutation(patchUpdateUserItemList(queryClient));
  const deleteMutation = useMutation(deleteUserItem(queryClient));

  if (isLoading) return <p>Loading Users...</p>;
  if (error) return <p>Error loading users: {error.message}</p>;

  const handleToggle = (userName, value) => {
    console.log(userName, value);
    patchMutation.mutate({ id, fields: { active: value } });
    // setTableData((prev) =>
    //   prev.map((row) => (row.userName === userName ? { ...row, status: value ? "active" : "inactive" } : row))
    // );
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
    router.push(`${pathName}/${id}`)
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const columns = getStaffMemberColumns(handleToggle, handleEditClick, handleDeleteClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={usersListData}
        tabName="Users"
        handleHeaderButtonClick={() => {
          console.log("Header Button Clicked");
        }}
        headerButtonName="Add User"
        headerDialogType="user"
      />
    </div>
  );
};

export default Users;
