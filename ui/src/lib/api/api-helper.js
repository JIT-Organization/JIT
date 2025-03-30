import axios from "axios";
import { getAxiosInstance } from "../utils/helper";

const axiosInstance = getAxiosInstance();

export const getRequest = async (url, errorMessage = "Failed to fetch data") => {
  try {
    const response = await axiosInstance.get(url);
    if (response.status !== 200) {
      throw new Error(`Failed to fetch from ${url}`);
    }
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      throw new Error(error.response?.data?.message || errorMessage);
    }
    throw new Error(errorMessage);
  }
};


export const patchRequest = async (url, data) => {
  const response = await axiosInstance.patch(url, data);
  return response.data;
};

export const deleteRequest = async (url) => {
  const response = await axiosInstance.delete(url);
  return response.data;
};

export const handleMutate = async (queryClient, queryKey, id, fields) => {
  await queryClient.cancelQueries(queryKey);

  const previousData = queryClient.getQueryData(queryKey);

  queryClient.setQueryData(queryKey, (oldData) => {
    if (!oldData) return oldData;
    return oldData.map((row) =>
      row.id === id ? { ...row, ...fields } : row
    );
  });

  return { previousData };
};

export const handleError = (queryClient, queryKey, context) => {
  if (context?.previousData) {
    queryClient.setQueryData(queryKey, context.previousData);
  }
};
