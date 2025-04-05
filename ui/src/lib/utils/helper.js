import axios from "axios";

export const getDistinctCategories = (data) => {
  const categorySet = new Set();
  categorySet.add("All");

  data.forEach((item) => {
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

export const getAxiosInstance = () => {
  const jwtToken = sessionStorage.getItem("jwtToken")
  console.log(jwtToken)
  if (!jwtToken) {
    window.location.href = "/login";
    throw new Error("Unauthorized: No JWT Token Found");
  }

  const axiosInstance = axios.create({
    withCredentials: true,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${jwtToken}`,
    }
  })

  return {
    get: (url, config = {}) => axiosInstance.get(url, config),
    post: (url, body, config = {}) => axiosInstance.post(url, body, config),
    put: (url, body, config = {}) => axiosInstance.put(url, body, config),
    patch: (url, body, config = {}) => axiosInstance.patch(url, body, config),
    delete: (url, config = {}) => axiosInstance.delete(url, config),
  }
}