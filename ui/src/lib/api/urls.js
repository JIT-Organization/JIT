const createUrl = (endpoint) => `http://localhost:8080/jit-api${endpoint}`;

export const URLS = {
  menuItemList: createUrl("/items"),
  ordersList: createUrl("/orders"),
  categoriesList: createUrl("/categories"),
  tablesList: createUrl("/tables"),
  usersList: createUrl("/users"),
  addOns: createUrl("/addons"),
  register: "http://localhost:8080/register",
  sendInvite: createUrl("/users/send-invite"),
  permissions: createUrl("/permissions")
};
