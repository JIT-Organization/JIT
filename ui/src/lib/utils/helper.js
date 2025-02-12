export const getDistinctCategories = (data) => {
  const categorySet = new Set();
  categorySet.add("All");

  data.forEach((item) => {
    if (item.category) {
      const categories = Array.isArray(item.category)
        ? item.category
        : [item.category];
      categories.forEach((cat) => categorySet.add(cat));
    }
  });

  return Array.from(categorySet).map((cat) => ({
    id: cat.toLowerCase(),
    label: cat,
  }));
};
