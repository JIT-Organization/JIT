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
