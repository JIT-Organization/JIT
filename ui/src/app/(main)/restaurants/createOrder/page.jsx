"use client";
import React from "react";
import OrderPageWrapper from "@/components/OrderPageWrapper";
import { getMenuItemsListForOrder } from "@/lib/api/api";

const CreateOrder = () => {
  return (
    <OrderPageWrapper
      mode="create"
      queryOptions={getMenuItemsListForOrder()}
    />
  );
};

export default CreateOrder;
