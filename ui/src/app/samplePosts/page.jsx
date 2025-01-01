import PageProvider from "@/components/providers/PageProvider";
import { postsQueryOptions } from "@/lib/api";
import PostsPage from "@/components/_pages/PostsPage";

export default async function SamplePostsPage() {
  return (
    <PageProvider queryOptions={postsQueryOptions}>
      <PostsPage />
    </PageProvider>
  );
}
