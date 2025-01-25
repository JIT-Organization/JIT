export const postsQueryOptions = {
  queryKey: ["posts"],
  queryFn: async () => {
    const response = await fetch("https://jsonplaceholder.typicode.com/posts");
    if (!response.ok) {
      throw new Error("Failed to fetch posts");
    }
    return response.json();
  },
};
