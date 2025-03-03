const createUrl = (endpoint) => `http://localhost:8080${endpoint}`;

export const URLS = {
  menuItemList: createUrl("/menu-items"),
  ordersList: createUrl("/api/orders"),
  categoriesList: createUrl("/api/categories"),
  tablesList: createUrl("/api/tables"),
  usersList: createUrl("/api/users"),
};
