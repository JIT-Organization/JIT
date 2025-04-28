"use client";
import { getOrderColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteMenuItem, getOrdersListOptions } from "@/lib/api/api";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { usePathname, useRouter } from "next/navigation";

const Orders = () => {
  const router = useRouter();
   const pathName = usePathname();
   const id = pathName.split("/")[2];
   const queryClient = useQueryClient();
   const { data: orderListData, isLoading, error } = useQuery(getOrdersListOptions(id));
   const deleteMutation = useMutation(deleteMenuItem(queryClient));
 
   if (isLoading) return <p>Loading orders list...</p>;
   if (error) return <p>Error loading orders list: {error.message}</p>;
 
  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
    router.push(`${pathName}/${id}`)
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const toggleRowClick = (row) => {
    console.log(row)
    row.toggleExpanded()
  }

  const columns = getOrderColumns( handleEditClick, handleDeleteClick);

  const categories = getDistinctCategories(orderListData);
  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={orderListData}
        tabName="Orders"
        handleHeaderButtonClick={() => {
          console.log("Orders Page URL");
        }}
        headerButtonName="New Order"
        handleRowClick={toggleRowClick}
        expandableRowContent={() => {}}
      />
    </div>
  );
};

export default Orders;
