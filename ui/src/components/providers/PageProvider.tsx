import React from "react";
import { HydrationBoundary, dehydrate } from "@tanstack/react-query";
import { getQueryClient } from "../../lib/utils/get-query-client";

export default async function PageProvider({ queryOptions, children }) {
  const queryClient = getQueryClient();

  if (queryOptions) {
    await queryClient.prefetchQuery(queryOptions);
  }

  const dehydratedState = dehydrate(queryClient);

  return (
    <main>
      <HydrationBoundary state={dehydratedState}>
        {children}
      </HydrationBoundary>
    </main>
  );
}
