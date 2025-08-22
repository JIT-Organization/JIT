"use client";
import React from "react";
import OrderPageWrapper from "@/components/OrderPageWrapper";
import { getMenuItemsListForOrder } from "@/lib/api/api";
import { useParams } from "next/navigation";

const EditOrder = () => {
  const { orderNumber } = useParams();

  return (
    <OrderPageWrapper
      mode="edit"
      queryOptions={getMenuItemsListForOrder()}
      orderNumber={orderNumber}
    />
  );
};

export default EditOrder;
