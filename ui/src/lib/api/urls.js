const createUrl = (endpoint) => `http://localhost:8080/jit-api${endpoint}`;

export const URLS = {
  menuItemList: createUrl("/menu-items"),
  ordersList: createUrl("/orders"),
  categoriesList: createUrl("/categories"),
  tablesList: createUrl("/tables"),
  usersList: createUrl("/users"),
};
