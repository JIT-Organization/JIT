const createUrl = (endpoint) => `${getBaseUrl()}/jit-api${endpoint}`;

export const URLS = {
  menuItemList: createUrl("/items"),
  ordersList: createUrl("/orders"),
  categoriesList: createUrl("/categories"),
  tablesList: createUrl("/tables"),
  usersList: createUrl("/users"),
  addOns: createUrl("/addons"),
  register: createUrl("/register"),
  login: createUrl("/login"),
  refresh: createUrl("/refresh"),
  sendInvite: createUrl("/users/send-invite"),
  permissions: createUrl("/permissions")
};

export function getBaseUrl() {
  return process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'
}
