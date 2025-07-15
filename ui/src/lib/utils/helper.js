import axios from "axios";
import Cookies from "js-cookie";

export const getDistinctCategories = (data) => {
  const categorySet = new Set();
  categorySet.add("All");

  (data||[]).forEach((item) => {
    if (item.categorySet?.length > 0) {
      item.categorySet.map((cat) => categorySet.add(cat));
    }
  });
  const categoryList = [...categorySet];
  return categoryList;
};

export const encodeIdInURL = (id) => {
  return encodeURIComponent(btoa(id));
};

export const getSelectOptions = (data) => {
  return data?.map((item) => ({
    label: item.menuItemName,
    value: item.menuItemName,
  }));
};

export const getAxiosInstance = (cookies = "") => {
  // TODO add cookies logic when we try to to prefetching
  const axiosInstance = axios.create({
    withCredentials: true,
    headers: {
      "Content-Type": "application/json",
      ...(cookies && { Cookie: cookies })
    }
  })
  const refreshInstance = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
    headers: {
      "Content-Type": "application/json",
      ...(cookies && { Cookie: cookies }),
    },
  });
  const isServer = typeof window === "undefined";

  axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;  
      if (
        error.response?.status === 401 &&
        !originalRequest._retry &&
        !originalRequest.url.includes("/refresh")
      ) {
        originalRequest._retry = true;
        try {
          console.log("refresh cookie", cookies)
          await refreshInstance.post("/refresh");
          return axiosInstance(originalRequest);
        } catch (refreshError) {
          console.error("Token refresh failed", refreshError);
          if (!isServer) {
            window.location.href = "/login";
          }
        }
      }
      return Promise.reject(error);
    }
  );

  return {
    get: (url, config = {}) => axiosInstance.get(url, config),
    post: (url, body, config = {}) => axiosInstance.post(url, body, config),
    put: (url, body, config = {}) => axiosInstance.put(url, body, config),
    patch: (url, body, config = {}) => axiosInstance.patch(url, body, config),
    delete: (url, config = {}) => axiosInstance.delete(url, config),
    prefetch: (queryClient, queryOptions) => queryClient.prefetchQuery(queryOptions)
  }
}