"use client";
import { getStaffMemberColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { createUser, deleteUserItem, getUsersListOptions, patchUpdateUserItemList } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

const Users = () => {
  const queryClient = useQueryClient();
  const { data: usersListData, isLoading, error } = useQuery(getUsersListOptions("TGSR"));
  const patchMutation = useMutation(patchUpdateUserItemList(queryClient));
  const deleteMutation = useMutation(deleteUserItem(queryClient));
  const postMutation = useMutation(createUser(queryClient));

  if (isLoading) return <p>Loading Users...</p>;
  if (error) return <p>Error loading users: {error.message}</p>;

  const handleToggle = (username, value) => {
    patchMutation.mutate({ username, fields: { isActive: value } });
  };

  const handleEditClick = (data) => {
    const obj = {
      name: data.firstName + " " + data.lastName,
      email: data.email,
      phoneNumber: data.phoneNumber,
      role: data.role,
      shift: data?.shift,
      username: data?.username,
      onToggle: handleToggle
    }
    return obj
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const onUpdateSubmit = (row) => (values) => {
    console.log(row)
    const formValues = { ...values };
    console.log(formValues)
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
    // postMutation.mutate({ fields: values });
    console.log(values)
    // TODO create a user along with sending the user email and password from the mail server
  };

  const columns = getStaffMemberColumns(handleToggle, handleEditClick, handleDeleteClick, onUpdateSubmit);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={usersListData.data}
        tabName="Users"
        headerButtonName="Add User"
        headerDialogType="user"
        onSubmitClick={onAddSubmit}
      />
    </div>
  );
};

export default Users;
