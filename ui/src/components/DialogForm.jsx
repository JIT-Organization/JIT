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
import { Switch } from "./ui/switch";
import { AddOnSingleInput } from "./AddOnInput";
import { useEffect, useState } from "react";
import { getPermisisonsForRole } from "@/lib/api/api";
import { useQuery } from "@tanstack/react-query";
import { checkPermission } from "@/lib/utils/checkPerimission";

export default function DialogForm({ type, data, onSubmit, selectOptions, isLoading }) {
  const isEdit = !!data;

  const [viewPermissions, setViewPermissions] = useState(false);
  const [addPermissions, setAddPermissions] = useState(false);
  const [editPermissions, setEditPermissions] = useState(false);
  const [delPermissions, setDelPermissions] = useState(false);

  const setPermissions = () => {
    if (checkPermission("P203")) setViewPermissions(true);
    if (checkPermission("P201")) setAddPermissions(true);
    if (checkPermission("P202")) setEditPermissions(true);
    if (checkPermission("P204")) setDelPermissions(true)
  }

  useEffect(() => {
    setPermissions();
  }, []);

  const getDefaultValues = (type) => {
    switch (type) {
      case "category":
        return {
          categoryName: "",
          foodItems: [],
          visibility: "public",
        };

      case "user":
        return {
          firstName: "",
          lastName: "",
          phoneNumber: "",
          email: "",
          role: "",
          shift: "",
          isActive: true,
        };

      case "table":
        return {
          tableNumber: "",
          seatingCapacity: "",
          isAvailable: "yes",
        };

      case "add-on":
        return {
          label: "",
          price: "",
          options: [],
        };

      default:
        return;
    }
  };

  const fieldConfig = {
    user: [
      {
        name: "firstName",
        label: "First Name",
        rules: { required: "First Name is required" },
        render: (props) => (
          <Input type="text" placeholder="Enter your first name" {...props} />
        ),
      },
      {
        name: "lastName",
        label: "Last Name",
        render: (props) => (
          <Input type="text" placeholder="Enter your last name" {...props} />
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
            { label: "Manager", value: "MANAGER" },
            { label: "Cook", value: "COOK" },
            { label: "Server", value: "SERVER" },
            { label: "Admin", value: "ADMIN" },
          ];

          return (
            <Select value={props?.value} onValueChange={props.onChange}>
              <SelectTrigger>
                <SelectValue placeholder="Role" />
              </SelectTrigger>
              <SelectContent className="bg-white">
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
              <SelectContent className="bg-white">
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
        name: "isActive",
        label: "Active",
        dontDisplay: !isEdit,
        render: (props) => {
          return (
            <Switch
              checked={props.value === true}
              onCheckedChange={(value) => {
                props.onChange(value);
              }}
              onBlur={props.onBlur}
            />
          );
        },
      },
      {
        name: "restaurantCodes",
        label: "Restaurants",
        dontDisplay: isEdit,
        render: (props) => {
          const options = [
            { label: "The Gourment Spot", value: "TGSR" }
          ];

          return (
            <div>
              <MultiSelect
                options={options}
                value={props.value || []}
                onChange={props.onChange}
                placeholder="Select restaurants"
              />
              <div className="flex flex-wrap gap-2 mt-2 bg-gray-600/20 p-6 overflow-auto h-20">
                {(props.value || []).map((val) => {
                  const option = selectOptions?.find((o) => o.value === val);
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
        name: "permissionCodes",
        label: "Permissions",
        dontDisplay: !viewPermissions || (isEdit && !editPermissions),
        render: (props) => {
          const options = permissionOptions.map(permission => ({
            label: permission.title,
            value: permission.permissionCode,
          }));
          return (
            <div>
              <MultiSelect
                options={options}
                value={props.value || []}
                onChange={props.onChange}
                placeholder="Select permissions"
                disabled={!addPermissions}
              />
              <div className="flex flex-wrap gap-2 mt-2 bg-gray-600/20 p-6 overflow-auto h-20">
                {(props.value || []).map((val) => {
                  console.log(props)
                  const option = options?.find((o) => o.value === val);
                  console.log(option)
                  console.log(options)
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
      }
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
        name: "foodItems",
        label: "Food Items",
        rules: { required: "Food Items are required" },
        render: (props) => {
          return (
            <div>
              <MultiSelect
                options={selectOptions}
                value={props.value || []}
                onChange={props.onChange}
                placeholder="Select foods"
              />
              <div className="flex flex-wrap gap-2 mt-2 bg-gray-600/20 p-6 overflow-auto h-20">
                {(props.value || []).map((val) => {
                  const option = selectOptions?.find((o) => o.value === val);
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
        render: (props) => {
          const isEditMode = data?.tableNumber !== undefined;
          return isEditMode ? (
            <div>{props.value}</div>
          ) : (
            <Input type="text" placeholder="Enter table number" {...props} />
          );
        },
      },
      {
        name: "chairs",
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
        name: "isAvailable",
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

  const form = useForm({
    defaultValues: data || getDefaultValues(type),
    mode: "onBlur",
  });
  const role = form.watch("role");
  const { data: permissionOptions = [] } = useQuery(getPermisisonsForRole(role));

  // This function will be called by the form submit event
  const handleSubmit = async (fields) => {
    if (onSubmit) {
      // Pass a closeDialog callback to parent
      await onSubmit(fields);
    }
  };

  if (type === "add-on") {
    return (
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(handleSubmit)}
          className="space-y-4"
        >
          <AddOnSingleInput
            value={form.watch()}
            onChange={(val) => {
              form.reset(val);
            }}
            showRemove={false}
            highlight={false}
          />
          <div className="flex space-x-4 items-center">
            <DialogClose>
              <div className="hover:bg-gray-600/10 py-2 px-4 rounded-lg">
                Cancel
              </div>
            </DialogClose>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "Saving..." : "Save"}
            </Button>
          </div>
        </form>
      </Form>
    );
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
          <div
            className={`grid gap-6 ${(fieldConfig?.[type]?.length || 0) > 6
                ? "grid-cols-1 sm:grid-cols-2"
                : "grid-cols-1"
              }`}
          >
            {fieldConfig?.[type]?.map((field) => (
              <FormField
                key={field.name}
                control={form.control}
                name={field.name}
                rules={field.rules}
                render={({ field: formField }) =>
                  !field?.dontDisplay && (
                    <FormItem>
                      <div className="grid grid-cols-1 sm:grid-cols-3 items-center gap-2">
                        <FormLabel className="mb-0 sm:col-span-1">
                          {field.label}
                        </FormLabel>
                        <FormControl className="sm:col-span-2">
                          {field.render(formField)}
                        </FormControl>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )
                }
              />
            ))}
          </div>
          <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-2 sm:space-y-0 items-start sm:items-center pt-4">
            <DialogClose>
              <div className="hover:bg-gray-600/10 py-2 px-4 rounded-lg w-full sm:w-auto text-center">
                Cancel
              </div>
            </DialogClose>
            <DialogClose asChild>
              <Button type="submit" disabled={isLoading}>
                {isEdit ? "Submit" : "Add"}
                {isLoading ? "Saving..." : "Save"}
              </Button>
            </DialogClose>
          </div>
        </form>
      </div>
    </Form>
  );
}
