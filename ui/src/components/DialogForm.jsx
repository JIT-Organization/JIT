"use client";

import { Button } from "./ui/button";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "./ui/form";
import { useForm } from "react-hook-form";
import { Input } from "./ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./ui/select";
import { DialogClose } from "./ui/dialog";
import { ToggleGroup, ToggleGroupItem } from "./ui/toggle-group";
import MultiSelect from "./customUIComponents/MultiSelect";
import { X } from "lucide-react";

export default function DialogForm({ type }) {
  const getDefaultValues = (type) => {
    switch (type) {
      case "category":
        return {
          categoryName: "",
          foodItem: "",
          visibility: "public",
        };

      case "user":
        return {
          name: "",
          phoneNumber: "",
          email: "",
          role: "",
          shift: "",
        };

      case "table":
        return {
          tableNumber: "",
          seatingCapacity: "",
          available: "yes",
        };

      default:
        return;
    }
  };

  const fieldConfig = {
    user: [
      {
        name: "name",
        label: "Name",
        rules: { required: "Name is required" },
        render: (props) => (
          <Input type="text" placeholder="Enter your name" {...props} />
        ),
      },
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
        name: "phoneNumber",
        label: "Phone Number",
        rules: {
          required: "Phone Number is required",
          pattern: {
            value: /^\+?\d{0,15}$/,
            message: "Invalid phone number format",
          },
        },
        render: (props) => (
          <Input type="tel" placeholder="Enter your phone number" {...props} />
        ),
      },
      {
        name: "role",
        label: "Role",
        rules: { required: "Role is required" },
        render: (props) => {
          const options = [
            { label: "Manager", value: "manager" },
            { label: "Cook", value: "cook" },
            { label: "Server", value: "server" },
          ];

          return (
            <Select value={props?.value} onValueChange={props.onChange}>
              <SelectTrigger>
                <SelectValue placeholder="Role" />
              </SelectTrigger>
              <SelectContent>
                {options.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          );
        },
      },
      {
        name: "shift",
        label: "Shift",
        render: (props) => {
          const options = [
            { label: "Full Time", value: "fullTime" },
            { label: "Part Time", value: "partTime" },
          ];

          return (
            <Select value={props?.value} onValueChange={props.onChange}>
              <SelectTrigger>
                <SelectValue placeholder="Select shift" />
              </SelectTrigger>
              <SelectContent>
                {options.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          );
        },
      },
    ],
    category: [
      {
        name: "categoryName",
        label: "Category Name",
        rules: { required: "Category Name is required" },
        render: (props) => (
          <Input type="text" placeholder="Enter category name" {...props} />
        ),
      },
      {
        name: "foodItem",
        label: "Food Item",
        rules: { required: "Food Item is required" },
        render: (props) => {
          const options = [
            { label: "Idly", value: "idly" },
            { label: "Pongal", value: "pongal" },
            { label: "Dosa", value: "dosa" },
          ];

          return (
            <div>
              <MultiSelect
                options={options}
                value={props.value || []}
                onChange={props.onChange}
                placeholder="Select foods"
              />
              <div className="flex flex-wrap gap-2 mt-2 bg-gray-600/20 p-6 overflow-auto h-20">
                {(props.value || []).map((val) => {
                  const option = options.find((o) => o.value === val);
                  return (
                    <span
                      key={val}
                      className="flex items-center gap-1 rounded bg-black text-white px-2 py-1 text-sm"
                    >
                      {option?.label || val}
                      <X
                        className="cursor-pointer h-4 w-4"
                        onClick={(e) => {
                          e.stopPropagation();
                          props.onChange(props.value.filter((v) => v !== val));
                        }}
                      />
                    </span>
                  );
                })}
              </div>
            </div>
          );
        },
      },
      {
        name: "visibility",
        label: "Visibility",
        rules: { required: "Visibility is required" },
        render: (props) => (
          <ToggleGroup
            type="single"
            value={props.value}
            onValueChange={(value) => {
              if (!value) return;
              props.onChange(value);
            }}
          >
            <ToggleGroupItem value="public">Public</ToggleGroupItem>
            <ToggleGroupItem value="private">Private</ToggleGroupItem>
          </ToggleGroup>
        ),
      },
    ],
    table: [
      {
        name: "tableNumber",
        label: "Table Number",
        rules: { required: "Table Number is required" },
        render: (props) => (
          <Input type="text" placeholder="Enter table number" {...props} />
        ),
      },
      {
        name: "seatingCapacity",
        label: "Seating Capacity",
        rules: {
          required: "Seating Capacity is required",
          pattern: {
            value: /^\d+$/,
            message: "Seating Capacity must be a number",
          },
        },
        render: (props) => (
          <Input
            type="number"
            placeholder="Enter seating capacity"
            {...props}
          />
        ),
      },
      {
        name: "available",
        label: "Availability",
        rules: { required: "Availability is required" },
        render: (props) => (
          <ToggleGroup
            type="single"
            value={props.value}
            onValueChange={(value) => {
              if (!value) return;
              props.onChange(value);
            }}
          >
            <ToggleGroupItem value="yes">Yes</ToggleGroupItem>
            <ToggleGroupItem value="no">No</ToggleGroupItem>
          </ToggleGroup>
        ),
      },
    ],
  };

  const onSubmit = () => {
    console.log("Submit Clicked!", form.getValues());
  };

  const form = useForm({
    defaultValues: getDefaultValues(type),
    mode: "onBlur",
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        {fieldConfig?.[type]?.map((field) => (
          <FormField
            key={field.name}
            control={form.control}
            name={field.name}
            rules={field.rules}
            render={({ field: formField }) => (
              <FormItem>
                <div className="grid grid-cols-3 items-center">
                  <FormLabel className="mb-0 col-span-1">
                    {field.label}
                  </FormLabel>
                  <FormControl className="col-span-2 justify-start">
                    {field.render(formField)}
                  </FormControl>
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
        ))}
        <div className="flex space-x-4 items-center">
          <DialogClose>
            <div className="hover:bg-gray-600/10 py-2 px-4 rounded-lg">
              Cancel
            </div>
          </DialogClose>
          <Button type="submit">Submit</Button>
        </div>
      </form>
    </Form>
  );
}
