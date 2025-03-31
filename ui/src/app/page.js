"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = sessionStorage.getItem("jwtToken");

    if (!token) {
      const refreshToken = getCookie("refreshToken");

      if (refreshToken) {
        refreshAccessToken(refreshToken);
      } else {
        router.push("/login");
      }
    }
  }, [router]);

  const getCookie = (name) => {
    const cookies = document.cookie.split("; ");
    for (let cookie of cookies) {
      const [key, value] = cookie.split("=");
      if (key === name) {
        return value;
      }
    }
    return null;
  };

  const refreshAccessToken = async () => {
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
