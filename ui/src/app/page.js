"use client";
import { useRouter } from "next/navigation";
import axios from "axios";
import { URLS } from "../lib/api/urls";

export default function Home() {
  const router = useRouter();
  async () => {
    try {
      const res = await axios.post(URLS.refresh, null, {
        withCredentials: true,
      });

      const newAccessToken = res.data.accessToken;
      sessionStorage.setItem("jwtToken", newAccessToken);
    } catch (error) {
      console.error("Token refresh failed:", error.message);
      router.push("/login");
    }
  };

  return (
    <></>
  );
}
