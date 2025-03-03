"use client";
import { getTableColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteTableItem, getTablesListOptions } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { usePathname, useRouter } from "next/navigation";

const Tables = () => {
  const router = useRouter();
  const pathName = usePathname();
  const id = pathName.split("/")[2];
  const queryClient = useQueryClient();
  const { data: tablesListData, isLoading, error } = useQuery(getTablesListOptions(id));
  const deleteMutation = useMutation(deleteTableItem(queryClient));

  if (isLoading) return <p>Loading Tables ...</p>;
  if (error) return <p>Error loading tables: {error.message}</p>;

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
    router.push(`${pathName}/${id}`)
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const columns = getTableColumns( handleEditClick, handleDeleteClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={tablesListData}
        tabName="Tables"
        handleHeaderButtonClick={() => {
          console.log("Add Table URL");
        }}
        headerButtonName="Add Table"
        headerDialogType="table"
      />
    </div>
  );
};

export default Tables;
