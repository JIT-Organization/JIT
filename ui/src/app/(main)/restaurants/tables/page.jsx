"use client";
import { getTableColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { createTable, deleteTableItem, getTablesListOptions, patchTables } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

const Tables = () => {
  const queryClient = useQueryClient();
  const { data: tablesListData, isLoading, error } = useQuery(getTablesListOptions("TGSR"));
  const patchMutation = useMutation(patchTables(queryClient))
  const deleteMutation = useMutation(deleteTableItem(queryClient));
  const postMutation = useMutation(createTable(queryClient));

  if (isLoading) return <p>Loading Tables ...</p>;
  if (error) return <p>Error loading tables: {error.message}</p>;

  const handleEditClick = (row) => {
    return { ...row, isAvailable: row.isAvailable ? "yes" : "no"};
  }

  const handleUpdate = (row) => (values) => {
    const formValues = { ...values, isAvailable: values?.isAvailable === "yes" };
    const formValueKeys = Object.keys(formValues);
    formValueKeys.forEach((key) => {
      if (key != "tableNumber" && row[key] === formValues[key]) {
        delete formValues[key];
      }
    });
    if (Object.keys(formValues).length !== 0)
      patchMutation.mutate({ fields: { ...formValues } });
  };
  
  const handleDeleteClick = (tableNumber) => {
    deleteMutation.mutate({restaurantCode: "TGSR", tableNumber});
  }

  const handleCreate = (values) => {
    const payload = { ...values, isAvailable: values?.isAvailable === "yes" };
    postMutation.mutate({ payload })
  }

  const columns = getTableColumns( handleEditClick, handleDeleteClick, handleUpdate);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tablesListData}
        tabName="Tables"
        headerButtonName="Add Table"
        headerDialogType="table"
        onSubmitClick={handleCreate}
      />
    </div>
  );
};

export default Tables;
