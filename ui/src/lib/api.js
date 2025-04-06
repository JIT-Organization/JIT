import axios from "axios";
import { URLS } from "./url/urls";
import { encodeIdInURL } from "./utils/helper";

export const postsQueryOptions = {
  queryKey: ["posts"],
  queryFn: async () => {
    const response = await fetch("https://jsonplaceholder.typicode.com/posts");
    if (!response.ok) {
      throw new Error("Failed to fetch posts");
    }
    return response.json();
  },
};

export const getMenuItemListOptions = (id) => ({
  queryKey: ["menuItemList"],
  queryFn: async () => {
    try {
      const response = await axios.get(`${URLS.menuItemList}/${id}`);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          error.response?.data?.message || "Failed to fetch Menu Item List"
        );
      }
      throw new Error("Failed to fetch Menu Item List");
    }
  },
  staleTime: 60 * 60 * 1000,
  gcTime: 2 * 60 * 60 * 1000,
});

export const patchUpdateMenuItemList = (queryClient) => ({
  mutationFn: async ({ id, fields }) => {
    return await axios.patch(`${URLS.menuItemList}/${id}`, {
      menuItemDTO: { ...fields },
      propertiesToBeUpdated: Object.keys(fields),
    });
  },
  onMutate: async ({ id, fields }) => {
    await queryClient.cancelQueries(["menuItemList"]);

    const previousData = queryClient.getQueryData(["menuItemList"]);

    queryClient.setQueryData(["menuItemList"], (oldData) => {
      if (!oldData) return oldData;
      return oldData.map((row) =>
        row.id === id ? { ...row, ...fields } : row
      );
    });

    return { previousData };
  },
  onError: (error, variables, context) => {
    console.error("Failed to update item:", error);
    if (context?.previousData) {
      queryClient.setQueryData(["menuItemList"], context.previousData);
    }
  },
  onSettled: () => {
    queryClient.invalidateQueries(["menuItemList"]);
  },
});

export const deleteMenuItem = (queryClient) => ({
  mutationFn: async ({ id }) => {
    return await axios.delete(`${URLS.menuItemList}/${id}`);
  },
  onMutate: async ({ id }) => {
    await queryClient.cancelQueries(["menuItemList"]);

    const previousData = queryClient.getQueryData(["menuItemList"]);

    queryClient.setQueryData(["menuItemList"], (oldData) => {
      if (!oldData) return oldData;
      return oldData.filter((row) => row.id !== id);
    });

    return { previousData };
  },
  onError: (err, variables, context) => {
    if (context?.previousData) {
      queryClient.setQueryData(["menuItemList"], context.previousData);
    }
  },
});
