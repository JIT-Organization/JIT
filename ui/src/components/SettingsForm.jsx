"use client";


import * as React from "react";
import { useForm, FormProvider } from "react-hook-form";
import {
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "@/components/ui/form";
import { BiSolidChevronLeft } from "react-icons/bi";
import Image from "next/image";
import MapPopup from "./MapPopup";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import { Label } from "./ui/label";


export default function BusinessProfilePage() {
  const methods = useForm({
    defaultValues: {
      coverPic: null,
      profilePic: null,
      name: "",
      email: "",
      phone: "",
      upi: "",
      location: "",
      address: "",
    },
  });

  const [location, setLocation] = React.useState("");

  const onSubmit = (data) => {
    console.log(data);
    // handle form submission
  };

  const DEFAULT_CENTER = [12.971599, 77.594566]; // Bangalore, India

  return (
    <FormProvider {...methods}>
      <form onSubmit={methods.handleSubmit(onSubmit)} className="space-y-6">
        <div className="flex items-center p-0.5">
          <Button
            type="button"
            className="mr-4 text-yellow-500 font-extrabold text-5xl"
            onClick={() => window.history.back()}
          >
            <BiSolidChevronLeft />
          </Button>
          <h1 className="text-4xl font-extrabold">Business Profile</h1>
        </div>


        <div className="rounded-full border-2 border-solid relative">
          <Image
            src="/resources/images/background.jpeg"
            alt="Cover"
            className="w-full h-48 object-cover"
            width="100"
            height="100"
          />
          <div className="absolute top-2 right-2">
            <Label
              htmlFor="coverPic"
              className="cursor-pointer p-2 bg-white rounded-full shadow"
            >
              <span role="img" aria-label="edit" className="bg-white rounded-full shadow">
                ✏️
              </span>
            </Label>
            <Input
              type="file"
              id="coverPic"
              className="hidden"
              {...methods.register("coverPic")}
            />
          </div>
          
          <div className="rounded-full border-2 border-solid absolute -bottom-12 right-4">
            <div className="relative">
              <Image
                src="/resources/images/profile.jpeg"
                alt="Profile"
                className="w-32 h-32 rounded-full border-4 border-white object-cover"
                width="100"
                height="100"
              />
              <div className="absolute bottom-0 right-0">
                <Label
                  htmlFor="profilePic"
                  className="cursor-pointer p-1 bg-white rounded-full shadow"
                >
                  <span role="img" aria-label="edit">
                    ✏️
                  </span>
                </Label>
                <Input
                  type="file"
                  id="profilePic"
                  className="hidden"
                  {...methods.register("profilePic")}
                />
              </div>
            </div>
          </div>
        </div>

        
        <div className="mt-16 p-10 pb-1 w-[80%] mx-left grid grid-cols-1 md:grid-cols-2 gap-8">
          
          <FormItem>
            <FormLabel className="text-2xl font-extrabold">Business Name</FormLabel>
            <FormField
              name="name"
              control={methods.control}
              render={({ field }) => (
                <FormControl>
                  <Input
                    type="text"
                    placeholder="Name"
                    className="w-full rounded-lg border-2 border-solid border-black p-3 text-md"
                    {...field}
                  />
                </FormControl>
              )}
            />
            <FormMessage />
          </FormItem>

          
          <FormItem>
            <FormLabel className="text-2xl font-extrabold">Email</FormLabel>
            <FormField
              name="email"
              control={methods.control}
              render={({ field }) => (
                <FormControl>
                  <Input
                    type="email"
                    placeholder="Email"
                    className="w-full rounded-lg border-2 border-solid border-black p-3 text-md"
                    {...field}
                  />
                </FormControl>
              )}
            />
            <FormMessage />
          </FormItem>

          
          <FormItem>
            <FormLabel className="text-2xl font-extrabold">Phone</FormLabel>
            <FormField
              name="phone"
              control={methods.control}
              render={({ field }) => (
                <FormControl>
                  <Input
                    type="tel"
                    placeholder="Phone"
                    className="w-full rounded-lg border-2 border-solid border-black p-3 text-md"
                    {...field}
                  />
                </FormControl>
              )}
            />
            <FormMessage />
          </FormItem>

          
          <FormItem>
            <FormLabel className="text-2xl font-extrabold">UPI ID</FormLabel>
            <FormField
              name="upi"
              control={methods.control}
              render={({ field }) => (
                <FormControl>
                  <Input
                    type="text"
                    placeholder="UPI ID"
                    className="w-full rounded-lg border-2 border-solid border-black p-3 text-md"
                    {...field}
                  />
                </FormControl>
              )}
            />
            <FormMessage />
          </FormItem>
        </div>
 
        
        <div className="p-10 w-1/2 mx-left relative">
                  <FormItem>
                    <FormLabel className="text-2xl font-extrabold">Address</FormLabel>
                      <FormField
                        name="location"
                        control={methods.control}
                        render={({ field }) => (
                          <FormControl>
                            <MapPopup
                              value={location}
                              onChange={(address) => {
                                setLocation(address);
                                methods.setValue("location", address); 
                                field.onChange(address); 
                              }}
                            />
                        </FormControl>
                      )}
                    />
                    <FormMessage />
                  </FormItem>
        </div>
        
        <div className="pl-10 flex justify-left">
          <Button 
            type="submit" 
            className="bg-gray-800 text-white font-bold py-2 px-6 rounded-lg transition-all duration-300 hover:bg-black hover:shadow-lg hover:scale-105"
          >
            Save
          </Button>
        </div>
      </form>
    </FormProvider>
  );
}
