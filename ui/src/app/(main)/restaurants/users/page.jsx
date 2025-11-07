"use client";
import { getStaffMemberColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteUserItem, getUsersListOptions, patchUpdateUserItemList, sendInviteToUser } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

const Users = () => {
  const queryClient = useQueryClient();
  const { data: usersListData, isLoading, error } = useQuery(getUsersListOptions());
  const patchMutation = useMutation(patchUpdateUserItemList(queryClient));
  const deleteMutation = useMutation(deleteUserItem(queryClient));
  const postMutation = useMutation(sendInviteToUser());

  if (isLoading) return <p>Loading Users...</p>;
  if (error) return <p>Error loading users: {error.message}</p>;

  const handleToggle = (username, value) => {
    patchMutation.mutate({ username, fields: { isActive: value } });
  };

  const handleEditClick = (data) => {
    const obj = {
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      phoneNumber: data.phoneNumber,
      role: data.role,
      shift: data?.shift,
      username: data?.username,
      isActive: data?.isActive,
      permissionCodes: data?.permissionCodes,
      onToggle: handleToggle
    }
    console.log(obj);
    return obj
  }

  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const onUpdateSubmit = (row) => (values) => {
    let formValues = { ...values };
    formValues = Object.fromEntries(
      Object.entries(formValues).filter(([_, v]) => typeof v !== "function")
    );
    const username = row.username;
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
      patchMutation.mutate({ username, fields: { ...formValues } });
  };

  const onAddSubmit = (values) => {
    const userData = values;
    postMutation.mutate(userData);
  };

  function handleResendClick (data) {
    const obj = {
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      phoneNumber: data.phoneNumber,
      role: data.role,
      shift: data?.shift,
      username: data?.username,
      isActive: data?.isActive,
      permissionCodes: data?.permissionCodes
    }
    postMutation.mutate(obj);
  }

  const columns = getStaffMemberColumns(handleToggle, handleEditClick, handleDeleteClick, onUpdateSubmit, handleResendClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={usersListData}
        tabName="Users"
        headerButtonName="Add User"
        headerDialogType="user"
        onSubmitClick={onAddSubmit}
        permissionIdentifier="users"
      />
    </div>
  );
};

export default Users;
