"use client";
import { getTableColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { createTable, deleteTableItem, getTablesListOptions, patchTables } from "@/lib/api/api";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";
import { useToast } from "@/hooks/use-toast";
import { useState, useEffect } from "react";
import useWebSocket from "@/lib/utils/webSocketUtils";
import { useNotifications } from "@/contexts/NotificationContext";
import { NOTIFICATION_TYPES } from "@/lib/constants/notifications";

const Tables = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const { addNotification } = useNotifications();
  const [loadingItems, setLoadingItems] = useState({});

  const clearLoadingItem = (tableNumber) =>
    setLoadingItems((prev) => {
      const newState = { ...prev };
      delete newState[tableNumber];
      return newState;
    });

  const { data: tablesListData, isLoading, error } = useQuery(getTablesListOptions("TGSR"));
  const { subscribe, isConnected } = useWebSocket("ws://localhost:8080/ws");

  // WebSocket connection status
  useEffect(() => {
    if (isConnected) {
      console.log("Tables WebSocket connection established.");
    } else {
      console.log("Tables WebSocket connection lost.");
    }
  }, [isConnected]);

  // WebSocket subscriptions for tables page
  useEffect(() => {
    if (!subscribe) return;

    console.log("Setting up Tables WebSocket subscriptions...");

    // Handle table status updates
    const handleTableStatusUpdate = (message) => {
      console.log("%cReceived table status update:", "color: #9b59b6;", message);
      
      const statusMessages = {
        'AVAILABLE': 'is now available',
        'OCCUPIED': 'is now occupied',
        'RESERVED': 'has been reserved',
        'CLEANING': 'is being cleaned',
        'OUT_OF_ORDER': 'is out of order'
      };

      const statusMessage = statusMessages[message.status] || `status updated to ${message.status}`;
      
      addNotification({
        type: NOTIFICATION_TYPES.TABLE_STATUS,
        message: `Table ${message.tableName || message.tableNumber || 'Unknown'} ${statusMessage}`,
        priority: ['OCCUPIED', 'RESERVED'].includes(message.status) ? 'medium' : 'high',
        data: message
      });

      // Invalidate tables query to refresh the list
      queryClient.invalidateQueries(['tables']);
    };

    // Handle table assignment notifications
    const handleTableAssignmentUpdate = (message) => {
      console.log("%cReceived table assignment update:", "color: #e67e22;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.TABLE_ASSIGNED,
        message: `Table ${message.tableName || message.tableNumber || 'Unknown'} assigned to ${message.assignedTo || 'staff member'}`,
        priority: 'high',
        data: message
      });

      // Invalidate tables query to refresh assignments
      queryClient.invalidateQueries(['tables']);
    };

    // Handle table maintenance updates
    const handleTableMaintenance = (message) => {
      console.log("%cReceived table maintenance update:", "color: #e74c3c;", message);
      
      addNotification({
        type: NOTIFICATION_TYPES.TABLE_STATUS,
        message: `Table ${message.tableName || message.tableNumber || 'Unknown'}: ${message.maintenanceMessage || 'Maintenance required'}`,
        priority: 'high',
        data: message
      });

      queryClient.invalidateQueries(['tables']);
    };

    // Subscribe to table-specific channels
    const unsubTableStatus = subscribe("/topic/tableStatus", handleTableStatusUpdate);
    const unsubTableAssignment = subscribe("/topic/tableAssignment", handleTableAssignmentUpdate);
    const unsubTableMaintenance = subscribe("/topic/tableMaintenance", handleTableMaintenance);

    // Subscribe to user-specific table notifications
    const unsubPersonalTableAssignment = subscribe("/user/queue/tableAssigned", (message) => {
      addNotification({
        type: NOTIFICATION_TYPES.TABLE_ASSIGNED,
        message: `You have been assigned to Table ${message.tableName || message.tableNumber || 'Unknown'}`,
        priority: 'high',
        data: message
      });
      queryClient.invalidateQueries(['tables']);
    });

    // Cleanup function
    return () => {
      console.log("Cleaning up Tables WebSocket subscriptions.");
      unsubTableStatus();
      unsubTableAssignment();
      unsubTableMaintenance();
      unsubPersonalTableAssignment();
    };
  }, [subscribe, addNotification, queryClient]);
  
  const patchMutation = useMutation({
    ...patchTables(queryClient),
    onMutate: ({ tableNumber }) => {
      setLoadingItems((prev) => ({ ...prev, [tableNumber]: "updating" }));
    },
    onSuccess: (_, { tableNumber }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Table updated successfully",
      });
      clearLoadingItem(tableNumber);
    },
    onError: (error, { tableNumber }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update table",
        variant: "destructive",
      });
      clearLoadingItem(tableNumber);
    },
  });

  const deleteMutation = useMutation({
    ...deleteTableItem(queryClient),
    onMutate: ({ tableNumber }) => {
      setLoadingItems((prev) => ({ ...prev, [tableNumber]: "deleting" }));
    },
    onSuccess: (_, { tableNumber }) => {
      toast({
        variant: "success",
        title: "Success",
        description: "Table deleted successfully",
      });
      clearLoadingItem(tableNumber);
    },
    onError: (error, { tableNumber }) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete table",
        variant: "destructive",
      });
      clearLoadingItem(tableNumber);
    },
  });

  const postMutation = useMutation({
    ...createTable(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Table created successfully",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create table",
        variant: "destructive",
      });
    },
  });

  if (isLoading) {
    return <LoadingState message="Loading tables..." />;
  }

  if (error) {
    return <ErrorState title="Error loading tables" message={error.message} />;
  }

  if (!tablesListData?.length) {
    return <ErrorState title="No Tables" message="No tables found." />;
  }

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
