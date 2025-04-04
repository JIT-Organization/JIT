import { URLS } from "./urls";
import { getRequest, patchRequest, deleteRequest, handleMutate, handleError, postRequest } from "./api-helper";

const cacheConfig = {
  staleTime: 60 * 60 * 1000,  
  gcTime: 2 * 60 * 60 * 1000, 
};

export const getMenuItemListOptions = () => ({
  queryKey: ["menuItemList"],
  queryFn: () => getRequest(`${URLS.menuItemList}`, "Failed to fetch Menu Item List"),
  ...cacheConfig
});

export const patchUpdateMenuItemList = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await patchRequest(`${URLS.menuItemList}/${id}`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ id, fields }) => handleMutate(queryClient, ["menuItemList"], id, fields),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["menuItemList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["menuItemList"]);
  },
});

export const deleteMenuItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.menuItemList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["menuItemList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["menuItemList"], context),
});


export const getOrdersListOptions = (id) => ({
  queryKey: ["ordersList"],
  queryFn: () => getRequest(`${URLS.ordersList}/${id}`, "Failed to fetch Orders List"),
  ...cacheConfig
});

export const deleteOrderItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.ordersList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["ordersList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["ordersList"], context),
});

export const getCategoriesListOptions = () => ({
  queryKey: ["categoriesList"],
  queryFn: () => getRequest(`${URLS.categoriesList}/getAll`, "Failed to fetch Categories List"),
  ...cacheConfig
});

export const createCategory = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await postRequest(URLS.categoriesList, {
      ...fields
    });
  },
  onMutate: async ({ fields }) => handleMutate(queryClient, ["categoriesList"], null, fields, "create"),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["categoriesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const patchUpdateCategoriesList = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await patchRequest(`${URLS.categoriesList}/${id}`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ id, fields }) => handleMutate(queryClient, ["categoriesList"], id, fields),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["categoriesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const deleteCategoryItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.categoriesList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["categoriesList"], id, null, "delete"),
  onError: (err, variables, context) => handleError(queryClient, ["categoriesList"], context),
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const getUsersListOptions = (id) => ({
  queryKey: ["usersList"],
  queryFn: () => getRequest(`${URLS.usersList}/${id}`, "Failed to fetch Users List"),
  ...cacheConfig
});

export const patchUpdateUserItemList = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await patchRequest(`${URLS.usersList}/${id}`, {
      userItemDTO: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ id, fields }) => handleMutate(queryClient, ["usersList"], id, fields),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["usersList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["usersList"]);
  },
});

export const deleteUserItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.usersList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["usersList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["usersList"], context),
});

export const getTablesListOptions = (id) => ({
  queryKey: ["tablesList"],
  queryFn: () => getRequest(`${URLS.tablesList}/${id}`, "Failed to fetch Tables List"),
  ...cacheConfig
});

export const deleteTableItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.tablesList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["tablesList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["tablesList"], context),
});

// export const getMenuItemListOptions = (id) => ({
//   queryKey: ["menuItemList"],
//   queryFn: async () => {
//     try {
//       const response = await axios.get(`${URLS.menuItemList}/${id}`);
//       return response.data;
//     } catch (error) {
//       if (axios.isAxiosError(error)) {
//         throw new Error(
//           error.response?.data?.message || "Failed to fetch Menu Item List"
//         );
//       }
//       throw new Error("Failed to fetch Menu Item List");
//     }
//   },
//   staleTime: 60 * 60 * 1000,
//   gcTime: 2 * 60 * 60 * 1000,
// });

// export const getOrdersListOptions = (id) => ({
//   queryKey: ["ordersList"],
//   queryFn: async () => {
//     try {
//       const response = await axios.get(`${URLS.OrdersList}/${id}`);
//       return response.data;
//     } catch (error) {
//       if (axios.isAxiosError(error)) {
//         throw new Error(
//           error.response?.data?.message || "Failed to fetch Orders  List"
//         );
//       }
//       throw new Error("Failed to fetch Orders List");
//     }
//   },
//   staleTime: 60 * 60 * 1000,
//   gcTime: 2 * 60 * 60 * 1000,
// });

// export const patchUpdateMenuItemList = (queryClient) => ({
//   mutationFn: async ({ id, fields }) => {
//     return await axios.patch(`${URLS.menuItemList}/${id}`, {
//       menuItemDTO: { ...fields },
//       propertiesToBeUpdated: Object.keys(fields),
//     });
//   },
//   onMutate: async ({ id, fields }) => {
//     await queryClient.cancelQueries(["menuItemList"]);

//     const previousData = queryClient.getQueryData(["menuItemList"]);

//     queryClient.setQueryData(["menuItemList"], (oldData) => {
//       if (!oldData) return oldData;
//       return oldData.map((row) =>
//         row.id === id ? { ...row, ...fields } : row
//       );
//     });

//     return { previousData };
//   },
//   onError: (error, variables, context) => {
//     console.error("Failed to update item:", error);
//     if (context?.previousData) {
//       queryClient.setQueryData(["menuItemList"], context.previousData);
//     }
//   },
//   onSettled: () => {
//     queryClient.invalidateQueries(["menuItemList"]);
//   },
// });

// export const deleteMenuItem = (queryClient) => ({
//   mutationFn: async ({ id }) => {
//     return await axios.delete(`${URLS.menuItemList}/${id}`);
//   },
//   onMutate: async ({ id }) => {
//     await queryClient.cancelQueries(["menuItemList"]);

//     const previousData = queryClient.getQueryData(["menuItemList"]);

//     queryClient.setQueryData(["menuItemList"], (oldData) => {
//       if (!oldData) return oldData;
//       return oldData.filter((row) => row.id !== id);
//     });

//     return { previousData };
//   },
//   onError: (err, variables, context) => {
//     if (context?.previousData) {
//       queryClient.setQueryData(["menuItemList"], context.previousData);
//     }
//   },
// });
