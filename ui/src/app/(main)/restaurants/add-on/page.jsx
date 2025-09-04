"use client";
import { getAddOnColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getAddOnsListOptions, createAddOn, patchUpdateAddOn, deleteAddOn } from "@/lib/api/api";
import { useToast } from "@/hooks/use-toast";
import React from "react";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";

const AddOns = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [loadingItems, setLoadingItems] = React.useState({});

  const { data = [], isLoading, error } = useQuery(getAddOnsListOptions());

  const createMutation = useMutation({
    ...createAddOn(queryClient),
    onMutate: ({ fields }) => {
      setLoadingItems((prev) => ({ ...prev, creating: true }));
    },
    onSuccess: () => {
      toast({ variant: "success", title: "Success", description: "Add-On created successfully" });
      setLoadingItems((prev) => ({ ...prev, creating: false }));
    },
    onError: (error) => {
      toast({ title: "Error", description: error.message || "Failed to create add-on", variant: "destructive" });
      setLoadingItems((prev) => ({ ...prev, creating: false }));
    },
  });

  const updateMutation = useMutation({
    ...patchUpdateAddOn(queryClient),
    onMutate: ({ label }) => {
      setLoadingItems((prev) => ({ ...prev, [label]: "updating" }));
    },
    onSuccess: (_, { label }) => {
      toast({ variant: "success", title: "Success", description: "Add-On updated successfully" });
      setLoadingItems((prev) => ({ ...prev, [label]: false }));
    },
    onError: (error, { label }) => {
      toast({ title: "Error", description: error.message || "Failed to update add-on", variant: "destructive" });
      setLoadingItems((prev) => ({ ...prev, [label]: false }));
    },
  });

  const deleteMutation = useMutation({
    ...deleteAddOn(queryClient),
    onMutate: ({ label }) => {
      setLoadingItems((prev) => ({ ...prev, [label]: "deleting" }));
    },
    onSuccess: (_, { label }) => {
      toast({ variant: "success", title: "Success", description: "Add-On deleted successfully" });
      setLoadingItems((prev) => ({ ...prev, [label]: false }));
    },
    onError: (error, { label }) => {
      toast({ title: "Error", description: error.message || "Failed to delete add-on", variant: "destructive" });
      setLoadingItems((prev) => ({ ...prev, [label]: false }));
    },
  });

  const handleEditClick = (row) => row;
  const handleDeleteClick = (label) => {
    deleteMutation.mutate({ label });
  };
  const handleUpdate = (row) => async (fields, closeDialog) => {
    updateMutation.mutate(
      { label: row.label, fields },
      {
        onSuccess: () => {
          closeDialog && closeDialog();
          toast({ variant: "success", title: "Success", description: "Add-On updated successfully" });
        },
        onError: (error) => {
          toast({ title: "Error", description: error.message || "Failed to update add-on", variant: "destructive" });
        },
      }
    );
  };
  const handleCreate = async (fields, closeDialog) => {
    createMutation.mutate(
      { fields },
      {
        onSuccess: () => {
          closeDialog && closeDialog();
          toast({ variant: "success", title: "Success", description: "Add-On created successfully" });
        },
        onError: (error) => {
          toast({ title: "Error", description: error.message || "Failed to create add-on", variant: "destructive" });
        },
      }
    );
  };

  const columns = getAddOnColumns(handleEditClick, handleDeleteClick, handleUpdate, loadingItems);

  if (isLoading) {
    return <LoadingState message="Loading add-ons..." />;
  }

  if (error) {
    return <ErrorState title="Error loading add-ons" message={error.message} />;
  }

  if (!data?.length) {
    return <ErrorState title="No Add-ons" message="No add-ons found." />;
  }

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={data}
        tabName="Add-Ons"
        headerButtonName={loadingItems.creating ? "Adding..." : "Add Add-On"}
        headerDialogType="add-on"
        onSubmitClick={handleCreate}
      />
    </div>
  );
};

export default AddOns; 