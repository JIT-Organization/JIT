"use client";
import FoodCard from "@/components/customUIComponents/FoodCard";
import { getOrderColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { deleteMenuItem, getOrdersListOptions } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter, usePathname } from "next/navigation";

const Orders = () => {
  const router = useRouter();
   const pathName = usePathname();
  const queryClient = useQueryClient();
  const {
    data: orderListData,
    isLoading,
    error,
  } = useQuery(getOrdersListOptions());
  const deleteMutation = useMutation(deleteMenuItem(queryClient));

  if (isLoading) return <p>Loading orders list...</p>;
  if (error) return <p>Error loading orders list: {error.message}</p>;

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
    router.push(`${pathName}/${id}`);
  };

  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  };

  const toggleRowClick = (row) => {
    row.toggleExpanded();
  };

  const columns = getOrderColumns(handleEditClick, handleDeleteClick);

  const expandableRowContent = (row) => {
    console.log(row.original);
    return (
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 p-4">
        {row.original.orderItems.map((item) => (
          <div key={item.itemName}>
            <FoodCard food={item} quantity={item.quantity}/>
          </div>
        ))}
      </div>
    );
  };

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
        expandableRowContent={expandableRowContent}
      />
    </div>
  );
};

export default Orders;
