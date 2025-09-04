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
      
      // Handle Network Errors (when backend is down)
      if (error.code === 'ERR_NETWORK') {
        // Only redirect to login if this is an authentication-related endpoint
        const isAuthRelatedEndpoint = originalRequest.url.includes('/jit-api/') || 
                                     originalRequest.url.includes('/api/');
        
        if (isAuthRelatedEndpoint && !isServer) {
          window.location.href = "/login";
        }
        return Promise.reject(error);
      }
      
      // Handle 401 Unauthorized errors
      if (
        error.response?.status === 401 &&
        !originalRequest._retry &&
        !originalRequest.url.includes("/refresh")
      ) {
        originalRequest._retry = true;
        try {
          console.log("🔄 Attempting token refresh...");
          await refreshInstance.post("/refresh");
          console.log("✅ Token refresh successful");
          return axiosInstance(originalRequest);
        } catch (refreshError) {
          console.error("❌ Token refresh failed", refreshError);
          if (!isServer) {
            console.log("🚪 Redirecting to login due to failed token refresh");
            window.location.href = "/login";
          }
          return Promise.reject(refreshError);
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

export const getChangedFields = (original, updated) => {
  const changed = {};
  Object.keys(updated).forEach((key) => {
    if (
      (Array.isArray(original?.[key]) && Array.isArray(updated[key]) &&
        JSON.stringify(original[key]) === JSON.stringify(updated[key]))
      || original?.[key] === updated[key]
    ) {
      return;
    }
    if (updated[key] === null || updated[key] === undefined) {
      return;
    }
    changed[key] = updated[key];
  });
  return changed;
};