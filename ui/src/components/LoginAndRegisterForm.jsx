"use client";
import { Button } from "@/components/ui/button";
import axios from "axios";
import { useRouter, useSearchParams } from "next/navigation";
import React, { useEffect } from "react";
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
import { registerUser } from "@/lib/api/api";
import { useMutation } from "@tanstack/react-query";
import { Card, CardDescription, CardHeader } from "./ui/card";
import { URLS } from "@/lib/api/urls";
import { useToast } from "@/hooks/use-toast";

const LoginAndRegisterForm = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const encodedParams = searchParams.get("params");
  const decodedParams = encodedParams ? atob(encodedParams) : "";
  const urlParams = new URLSearchParams(decodedParams);
  const token = urlParams.get("token");
  const paramsEmail = urlParams.get("email");

  const isRegister = Boolean(token);

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

    if (!isRegister) {
      // Login flow
      await axios.post(
        URLS.login,
        { email, password },
        { withCredentials: true }
      );
      router.push(`/restaurants/menu`);
    } else {
      // Register flow
      const userData = { email, password };
      registerUserMutation
        .mutateAsync({ token, userData })
        .then(() => router.push("/login"));
    }
  };

  useEffect(() => {
    form.reset({
      email: paramsEmail ? paramsEmail : "",
      password: "",
    });
  }, [form, paramsEmail]);

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

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 px-4 py-8">
      <div className="text-3xl font-semibold text-gray-800 mb-6">Welcome to JIT</div>

      <div className="w-full max-w-md">
        <Card className="p-6 shadow-lg rounded-xl bg-white space-y-6">
          <div>
            <CardHeader className="p-0 font-bold text-2xl text-gray-800">
              {isRegister ? "Register" : "Login"}
            </CardHeader>
            <CardDescription className="text-sm text-gray-500 mt-1">
              {isRegister
                ? "Create a new account with your email and password"
                : "Enter your credentials to continue"}
            </CardDescription>
          </div>

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
                      <FormLabel className="text-sm text-gray-700">
                        {field.label}
                      </FormLabel>
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
                <Button
                  type="submit"
                  className="w-full text-white py-2 text-sm rounded-md transition"
                >
                  {isRegister ? "Register" : "Login"}
                </Button>
              </div>
            </form>
          </Form>
        </Card>
      </div>
    </div>
  );
};

export default LoginAndRegisterForm;
