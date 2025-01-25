'use client'

import React from "react";
import { useQuery } from "@tanstack/react-query";
import { postsQueryOptions } from "@/lib/api";

export default function PostsList() {
  const { data, isLoading, error } = useQuery(postsQueryOptions);

  if (isLoading) return <p>Loading posts...</p>;
  if (error) return <p>Error loading posts: {error.message}</p>;

  return (
    <ul>
      {data.map((post) => (
        <li key={post.id}>
          <strong>{post.title}</strong>
          <p>{post.body}</p>
        </li>
      ))}
    </ul>
  );
}
