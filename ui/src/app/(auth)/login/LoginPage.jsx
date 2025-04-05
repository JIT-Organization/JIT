"use client";
import { Button } from "@/components/ui/button";
import axios from "axios";
import { useRouter } from "next/navigation";
import React, { useState } from "react";

const LoginPage = () => {
  const [email, setEmail] = useState("john.smith@example.com");
  const [password, setPassword] = useState("hashed_password");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      // login({email, password});
      await axios.post("http://localhost:8080/login", { email, password }, { withCredentials: true })
      router.push(`/restaurants/menu`);
    } catch (err) {
      setError("Invalid email or password");
      console.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-500 text-white">
      <h1 className="flex justify-center p-10">JIT Login</h1>
      <form onSubmit={handleSubmit} className="flex flex-col space-y-4 pb-10">
        <label className="flex space-x-2 justify-center">
          <div>Email</div>
          <input
            type="email"
            name="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="text-black pl-2"
          />
        </label>
        <label className="flex space-x-2 justify-center">
          <div>Password</div>
          <input
            type="password"
            name="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="text-black pl-2"
          />
        </label>
        <Button type="submit" disabled={loading} className="btn">
          {loading ? "Logging in..." : "Login"}
        </Button>
        {error && <p style={{ color: "red" }}>{error}</p>}
      </form>
    </div>
  );
};

export default LoginPage;
