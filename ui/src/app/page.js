"use client";
import { useRouter } from "next/navigation";
import axios from "axios";

export default function Home() {
  const router = useRouter();
  async () => {
    try {
      const res = await axios.post("http://localhost:8080/refresh", null, {
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
