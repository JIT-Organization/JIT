"use client";
import { Button } from "@/components/ui/button";
import axios from "axios";
import { useRouter, useSearchParams } from "next/navigation";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "./ui/form";
import { Input } from "./ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "./ui/tabs";
import { registerUser } from "@/lib/api/api";
import { useMutation } from "@tanstack/react-query";
import { Card, CardDescription, CardHeader } from "./ui/card";

const LoginAndRegisterForm = ({ defaultIsLogin = true }) => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const encodedParams = searchParams.get("params");
  const decodedParams = atob(encodedParams);
  const urlParams = new URLSearchParams(decodedParams);
  const token = urlParams.get("token");
  const paramsEmail = urlParams.get("email");

  const [activeTab, setActiveTab] = useState(defaultIsLogin ? "login" : "register");

  const form = useForm({
    defaultValues: {
      email: paramsEmail || "",
      password: "",
    },
    mode: "onBlur",
  });

  const registerUserMutation = useMutation(registerUser());

  const onSubmit = async () => {
    const email = form.getValues().email;
    const password = form.getValues().password;

    if (activeTab === "login") {
      await axios.post(
        "http://localhost:8080/login",
        { email, password },
        { withCredentials: true }
      );
      router.push(`/restaurants/menu`);
    } else {
      const userData = { email, password };
      registerUserMutation.mutateAsync({ token, userData }).then(() => router.push("/login"));
    }
  };

  useEffect(() => {
    form.reset({
      email: paramsEmail ? paramsEmail : "",
      password: "",
    });
  }, [activeTab, form, paramsEmail]);

  const fields = [
    {
      name: "email",
      label: "Email",
      rules: {
        required: "Email is required",
        pattern: {
          value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
          message: "Invalid email format",
        },
      },
      render: (props) => (
        <Input type="email" placeholder="Enter your email" {...props} />
      ),
    },
    {
      name: "password",
      label: "Password",
      rules: {
        required: "Password is required",
      },
      render: (props) => (
        <Input type="password" placeholder="Enter your password" {...props} />
      ),
    },
  ];

  const renderForm = () => (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
        {fields.map((field) => (
          <FormField
            key={field.name}
            control={form.control}
            name={field.name}
            rules={field.rules}
            render={({ field: formField }) => (
              <FormItem>
                <FormLabel className="text-sm text-gray-700">{field.label}</FormLabel>
                <FormControl>
                  {field.render({
                    ...formField,
                    className:
                      "w-full px-3 py-2 mt-1 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500",
                  })}
                </FormControl>
                <FormMessage className="text-red-500 text-xs mt-1" />
              </FormItem>
            )}
          />
        ))}
        <div className="flex justify-center">
          <Button type="submit" className="w-full text-white py-2 text-sm rounded-md transition">
            {activeTab === "login" ? "Login" : "Register"}
          </Button>
        </div>
      </form>
    </Form>
  );

  const handleTabChange = (value) => {
    setActiveTab(value);
    router.replace(`/${value}`, { scroll: false });
  };


  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 px-4 py-8">
      <div className="text-3xl font-semibold text-gray-800 mb-6">Welcome to JIT</div>

      <div className="w-full max-w-md">
        <Tabs
          value={activeTab}
          onValueChange={handleTabChange}
          className="w-full"
        >
          <TabsList className="grid grid-cols-2 bg-gray-200 rounded-lg p-1 mb-4">
            <TabsTrigger
              value="login"
              className="rounded-md text-gray-700 data-[state=active]:bg-white data-[state=active]:shadow-sm data-[state=active]:text-black px-4 transition-all"
            >
              Login
            </TabsTrigger>
            <TabsTrigger
              value="register"
              className="rounded-md text-gray-700 data-[state=active]:bg-white data-[state=active]:shadow-sm data-[state=active]:text-black px-4 transition-all"
            >
              Register
            </TabsTrigger>
          </TabsList>

          <TabsContent value="login">
            <Card className="p-6 shadow-lg rounded-xl bg-white space-y-6">
              <div>
                <CardHeader className="p-0 font-bold text-2xl text-gray-800">Login</CardHeader>
                <CardDescription className="text-sm text-gray-500 mt-1">
                  Enter your credentials to continue
                </CardDescription>
              </div>
              {renderForm()}
            </Card>
          </TabsContent>

          <TabsContent value="register">
            <Card className="p-6 shadow-lg rounded-xl bg-white space-y-6">
              <div>
                <CardHeader className="p-0 font-bold text-2xl text-gray-800">Register</CardHeader>
                <CardDescription className="text-sm text-gray-500 mt-1">
                  Create a new account with your email and password
                </CardDescription>
              </div>
              {renderForm()}
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default LoginAndRegisterForm;
