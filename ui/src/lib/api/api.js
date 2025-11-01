import { URLS } from "./urls";
import { getRequest, patchRequest, deleteRequest, handleMutate, handleError, postRequest, generateQueryString } from "./api-helper";
import Cookies from "js-cookie";

const cacheConfig = {
  staleTime: 60 * 60 * 1000,  
  gcTime: 2 * 60 * 60 * 1000, 
};

const resCodes = Cookies.get("resCodes")
console.log(resCodes)

export const login = (data) => {
  postRequest("http://localhost:8080/login", data, {withCredential: true})
}

export const refresh = () => {
  try{
    postRequest("http://localhost:8080/refresh", {}, {withCredential: true})
  } catch (e) {
    console.log(e)
  }
}

export const getMenuItemListOptions = () => ({
  queryKey: ["menuItemList"],
  queryFn: () => getRequest(`${URLS.menuItemList}/TGSR`, "Failed to fetch Menu Item List"),
  // queryFn: () => getRequest("/api/menu-items", "Failed to fetch Menu Item List"),
  select: (data) =>
    data.map(({ image, menuItemName, cookSet, price, offerPrice, active, categorySet, foodType }) => ({
      image,
      menuItemName,
      cookSet,
      price,
      offerPrice,
      active,
      categorySet,
      foodType
    })),
  ...cacheConfig
});

export const getMenuItemsListForOrder = () => ({
  queryKey: ["menuItemList"],
  queryFn: () => getRequest(`${URLS.menuItemList}/TGSR`, "Failed to fetch Menu Item List"),
  ...cacheConfig
});

export const getMenuItemFood = (menuItemName, foodType) => ({
  queryKey: ['menuItemFood', menuItemName],
  queryFn: () => getRequest(`${URLS.menuItemList}/TGSR/${menuItemName}?foodType=${foodType}`, 'Failed to fetch Menu Item'),
  enabled: menuItemName && menuItemName !== "add_food",
  ...cacheConfig,
});

export const patchUpdateMenuItemList = (queryClient) => ({
  mutationFn: async ({ menuItemName, foodType, fields }) => {
    return await patchRequest(`${URLS.menuItemList}/TGSR/${menuItemName}?foodType=${foodType}`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ menuItemName, fields }) => handleMutate(queryClient, ["menuItemList"], menuItemName, fields, "menuItemName"),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["menuItemList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["menuItemList"]);
  },
});

export const createMenuItemFood = (queryClient) => ({
  mutationFn: async ({ id, foodType, fields }) => {
    return await postRequest(`${URLS.menuItemList}/TGSR?foodType=${foodType}`, {
      ...fields
    });
  },
  onMutate: async ({ fields }) => handleMutate(queryClient, ["menuItemList"], null, fields, "create"),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["menuItemList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["menuItemList"]);
  },
});

export const deleteMenuItem = (queryClient) => ({
  mutationFn: async ({ menuItemName, foodType }) => deleteRequest(`${URLS.menuItemList}/TGSR/${menuItemName}?foodType=${foodType}`),
  onMutate: async ({ menuItemName }) => handleMutate(queryClient, ["menuItemList"], menuItemName, null, "menuItemName", "delete"),
  onError: (err, variables, context) => handleError(queryClient, ["menuItemList"], context),
  onSettled: () => {
    queryClient.invalidateQueries(["menuItemList"]);
  },
});


export const getOrdersListOptions = () => ({
  queryKey: ["ordersList"],
  queryFn: () => getRequest(`${URLS.ordersList}/TGSR`, "Failed to fetch Orders List"),
  ...cacheConfig
});

export const deleteOrderItem = (queryClient) => ({
  mutationFn: async ({ id }) => deleteRequest(`${URLS.ordersList}/${id}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["ordersList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["ordersList"], context),
});

export const getCategoriesListOptions = () => ({
  queryKey: ["categoriesList"],
  queryFn: () => getRequest(`${URLS.categoriesList}/getAll/TGSR`, "Failed to fetch Categories List"),
  ...cacheConfig
});

export const createCategory = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await postRequest(`${URLS.categoriesList}/TGSR`, {
      ...fields
    });
  },
  onMutate: async ({ fields }) => handleMutate(queryClient, ["categoriesList"], null, fields, "", "create"),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["categoriesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const patchUpdateCategoriesList = (queryClient) => ({
  mutationFn: async ({ categoryName, fields }) => {
    console.log("ccc", categoryName)
    return await patchRequest(`${URLS.categoriesList}/TGSR/${categoryName}`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ categoryName, fields }) => handleMutate(queryClient, ["categoriesList"], categoryName, fields, "categoryName"),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["categoriesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const deleteCategoryItem = (queryClient) => ({
  mutationFn: async ({ categoryName }) => deleteRequest(`${URLS.categoriesList}/TGSR/${categoryName}`),
  onMutate: async ({ categoryName }) => handleMutate(queryClient, ["categoriesList"], categoryName, null, "categoryName", "delete"),
  onError: (err, variables, context) => handleError(queryClient, ["categoriesList"], context),
  onSettled: () => {
    queryClient.invalidateQueries(["categoriesList"]);
  },
});

export const getUsersListOptions = (resCode) => ({
  queryKey: ["usersList"],
  queryFn: () => getRequest(`${URLS.usersList}/${resCode}`, "Failed to fetch Users List"),
  ...cacheConfig
});

export const patchUpdateUserItemList = (queryClient) => ({
  mutationFn: async ({ username, fields }) => {
    return await patchRequest(`${URLS.usersList}/TGSR/${username}`, {
      dto: { ...fields },
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

export const createUser = (queryClient) => ({
  mutationFn: async ({ fields }) => {
    return await postRequest(URLS.usersList, {
      ...fields
    });
  },
  onMutate: async ({ fields }) => handleMutate(queryClient, ["usersList"], null, fields, "create"),
  onError: (error, variables, context) => {
    console.error("Failed to create item:", error);
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

export const patchTables = (queryClient) => ({
  mutationFn: async ({ fields }) => {
    return await patchRequest(`${URLS.tablesList}/TGSR`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ tableNumber, fields }) => handleMutate(queryClient, ["tablesList"], tableNumber, fields),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["tablesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["tablesList"]);
  },
})

export const createTable = (queryClient) => ({
  mutationFn: async ({ payload }) => {
    return await postRequest(`${URLS.tablesList}/TGSR`, payload);
  },
  onMutate: async ({ tableNumber, fields }) => handleMutate(queryClient, ["tablesList"], tableNumber, fields),
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    handleError(queryClient, ["tablesList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["tablesList"]);
  },
})

export const deleteTableItem = (queryClient) => ({
  mutationFn: async ({ restaurantCode, tableNumber }) => deleteRequest(`${URLS.tablesList}/${restaurantCode}/${tableNumber}`),
  onMutate: async ({ id }) => handleMutate(queryClient, ["tablesList"], id),
  onError: (err, variables, context) => handleError(queryClient, ["tablesList"], context),
  onSettled: () => {
    queryClient.invalidateQueries(["tablesList"]);
  },
});

export const getAddOnsListOptions = () => ({
  queryKey: ["addOnsList"],
  queryFn: () => getRequest(`${URLS.addOns}/TGSR`, "Failed to fetch Add-Ons List"),
  ...cacheConfig
});

export const createAddOn = (queryClient) => ({
  mutationFn: async ({ fields }) => {
    return await postRequest(`${URLS.addOns}/TGSR`, fields);
  },
  onMutate: async ({ fields }) => handleMutate(queryClient, ["addOnsList"], null, fields, "create"),
  onError: (error, variables, context) => {
    console.error("Failed to create add-on:", error);
    handleError(queryClient, ["addOnsList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["addOnsList"]);
  },
});

export const patchUpdateAddOn = (queryClient) => ({
  mutationFn: async ({ label, fields }) => {
    return await patchRequest(`${URLS.addOns}/TGSR`, {
      dto: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ label, fields }) => handleMutate(queryClient, ["addOnsList"], label, fields, "label"),
  onError: (error, variables, context) => {
    console.error("Failed to update add-on:", error);
    handleError(queryClient, ["addOnsList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["addOnsList"]);
  },
});

export const deleteAddOn = (queryClient) => ({
  mutationFn: async ({ label }) => deleteRequest(`${URLS.addOns}/TGSR/${label}`),
  onMutate: async ({ label }) => handleMutate(queryClient, ["addOnsList"], label, null, "label", "delete"),
  onError: (err, variables, context) => handleError(queryClient, ["addOnsList"], context),
  onSettled: () => {
    queryClient.invalidateQueries(["addOnsList"]);
  },
});

export const validateField = (type, value) => {
  const validationConfigs = {
    menuItem: {
      queryKey: ["validateMenuItemName", value],
      url: `${URLS.menuItemList}/validate/${value}`,
      errorMessage: "Failed to validate menu item name"
    },
    category: {
      queryKey: ["validateCategoryName", value],
      url: `${URLS.categoriesList}/validate/${value}`,
      errorMessage: "Failed to validate category name"
    },
    table: {
      queryKey: ["validateTableNumber", value],
      url: `${URLS.tablesList}/validate/${value}`,
      errorMessage: "Failed to validate table number"
    }
  };

  const config = validationConfigs[type];
  if (!config) {
    throw new Error(`Invalid validation type: ${type}`);
  }

  return {
    queryKey: config.queryKey,
    queryFn: () => getRequest(config.url, config.errorMessage),
    enabled: !!value,
    ...cacheConfig
  };
};

export const createOrder = (queryClient) => ({
  mutationFn: async ({ orderDTO }) => {
    return await postRequest(`${URLS.ordersList}/TGSR`, orderDTO);
  },
  onMutate: async ({ orderDTO }) => handleMutate(queryClient, ["ordersList"], null, orderDTO, "create"),
  onError: (error, variables, context) => {
    console.error("Failed to create order:", error);
    handleError(queryClient, ["ordersList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["ordersList"]);
  },
});

export const getOrderDetails = (orderNumber) => ({
  queryKey: ['orderDetails', orderNumber],
  queryFn: () => getRequest(`${URLS.ordersList}/TGSR/${orderNumber}`, "Failed to fetch order details"),
  enabled: !!orderNumber,
  ...cacheConfig
});

export const updateOrder = (queryClient) => ({
  mutationFn: async ({ orderNumber, ...orderDTO }) => {
    return await patchRequest(`${URLS.ordersList}/TGSR/${orderNumber}`, orderDTO, "Failed to update order");
  },
  onMutate: async ({ orderNumber, ...orderDTO }) => handleMutate(queryClient, ["ordersList"], orderNumber, orderDTO, "orderNumber"),
  onError: (error, variables, context) => {
    console.error("Failed to update order:", error);
    handleError(queryClient, ["ordersList"], context);
  },
  onSettled: () => {
    queryClient.invalidateQueries(["ordersList"]);
    queryClient.invalidateQueries(["orderDetails"]);
  },
});
export const registerUser = () => ({
  mutationFn: async ({ token, userData }) => {
    const url = generateQueryString(URLS.register, { token });
    return postRequest(url, userData);
  }
})

export const sendInviteToUser = () => ({
  mutationFn: async (userData) => {
    return postRequest(URLS.sendInvite, userData)
  }
})

export const getPermisisonsForRole = (role) => ({
  queryKey: ['permissions', role],
  queryFn: () => getRequest(`${URLS.permissions}/${role}`, `Failed to fetch permissions for role ${role}`),
  enabled: !!role,
  ...cacheConfig,
});

export const getDashboardDataOptions = (restaurantCode = "TGSR") => ({
  queryKey: ["dashboardData", restaurantCode],
  queryFn: () => getRequest(`${URLS.dashboard}/${restaurantCode}`, "Failed to fetch Dashboard Data"),
  enabled: !!restaurantCode,
  ...cacheConfig,
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
