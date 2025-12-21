"use client";

import { useForm } from "react-hook-form";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { forwardRef, useEffect, useImperativeHandle } from "react";
import ImageUploader from "./ImageUploader";
import TimeIntervalSetInput from "@/components/TimeIntervalSetInput";
import { DateTimePicker } from "@/components/customUIComponents/CustomeDateTimePicker";
import { useQuery } from "@tanstack/react-query";
import { getCategoriesListOptions, getUsersListOptions, validateField } from "@/lib/api/api";
import { Textarea } from "@/components/ui/textarea";
import CommonForm from "./CommonForm";
import AddOnInput from "./AddOnInput";
import ComboItemsInput from "./ComboItemsInput";

const defaultFormValues = {
  menuItemName: "",
  foodType: "MENU_ITEM",
  price: "",
  description: "",
  cookSet: [],
  count: "",
  timeIntervalSet: [{ startTime: "", endTime: "" }],
  availability: [],
  offerPrice: "",
  offerFrom: "",
  offerTo: "",
  preparationTime: "",
  acceptBulkOrders: false,
  onlyVeg: true,
  onlyForCombos: false,
  active: true,
  hotelSpecial: false,
  categorySet: [],
  images: [],
  addOns: [],
};

// const categoryOptions = [
//   { value: 'breakfast', label: 'Breakfast' },
//   { value: 'lunch', label: 'Lunch' },
//   { value: 'dinner', label: 'Dinner' },
//   { value: 'snacks', label: 'Snacks' },
//   { value: 'dessert', label: 'Dessert' },
// ];

// const cooksOptions = [
//   { value: 'cook1', label: 'Cook 1' },
//   { value: 'cook2', label: 'Cook 2' },
//   { value: 'cook3', label: 'Cook 3' },
//   { value: 'cook4', label: 'Cook 4' },
//   { value: 'cook5', label: 'Cook 5' },
// ];

const availabilityOptions = [
  { value: "SUNDAY", label: "Sunday" },
  { value: "MONDAY", label: "Monday" },
  { value: "TUESDAY", label: "Tuesday" },
  { value: "WEDNESDAY", label: "Wednesday" },
  { value: "THURSDAY", label: "Thursday" },
  { value: "FRIDAY", label: "Friday" },
  { value: "SATURDAY", label: "Saturday" },
];

//  const renderField = (fieldConfig, formField, form) => {
//   switch (fieldConfig.type) {
//     case "input":
//       return (
//         <Input
//           className="border p-2 w-full rounded bg-yellow-50"
//           {...formField}
//           placeholder={fieldConfig.placeholder}
//           onBlur={fieldConfig.name === "menuItemName" ? async (e) => {
//             const value = e.target.value;
//             if (value && !value) {
//               const { data: isValid } = await validateField("menuItem", value).queryFn();
//               if (!isValid) {
//                 formField.onChange(value);
//                 formField.onBlur();
//                 form.setError("menuItemName", {
//                   type: "manual",
//                   message: "This menu item name already exists",
//                 });
//               }
//             }
//           } : formField.onBlur}
//         />
//       );
//     case "textarea":
//       return (
//         <Textarea
//           {...formField}
//           placeholder={fieldConfig.placeholder}
//           className="w-full bg-yellow-50"
//         />
//       );
//     case "multiSelect":
//       return (
//         <MultiSelect
//           className="border p-2 w-full rounded bg-yellow-50"
//           {...formField}
//           options={fieldConfig.options}
//           placeholder={fieldConfig.placeholder}
//           showAllOption={fieldConfig.showAllOption}
//           disabled={fieldConfig.disabled}
//         />
//       );
//     case "toggleGroup":
//       return (
//         <ToggleGroup
//           variant="outline"
//           type="single"
//           value={fieldConfig.value !== undefined ? fieldConfig.value : (formField.value ? fieldConfig.options[0] : fieldConfig.options[1])}
//           onValueChange={fieldConfig.onValueChange || ((val) => formField.onChange(val === fieldConfig.options[0]))}
//         >
//           {fieldConfig.options.map((opt) => (
//             <ToggleGroupItem
//               className={`
//                 border p-2 w-full rounded
//                 hover:bg-yellow-50
//                 data-[state=on]:bg-yellow-50
//                 transition-colors duration-200
//               `}
//               key={opt}
//               value={opt}
//             >
//               {opt.charAt(0).toUpperCase() + opt.slice(1)}
//             </ToggleGroupItem>
//           ))}
//         </ToggleGroup>
//       );
//     case "dateTimePicker":
//       return (
//         <DateTimePicker
//           className="border p-2 w-full rounded bg-yellow-50"
//           {...formField}
//         />
//       );
//     case "imageUploader":
//       return <ImageUploader {...formField} multiple />;
//     case "custom":
//       const CustomComponent = fieldConfig.Component;
//       return (
//         <CustomComponent
//           value={formField.value}
//           onChange={formField.onChange}
//         />
//       );
//     default:
//       return null;
//   }
// };

const FoodForm = forwardRef(
  ({ onFormChange, onSubmit, defaultValues, isLoading, onError, isEdit}, ref) => {
    const { data: categoriesList, isLoading: isCategoryListLoading } = useQuery(
      getCategoriesListOptions()
    );
    const { data: usersListData, isLoading: isUserListLoading } = useQuery(
      getUsersListOptions("TGSR")
    );

    const categoryOptions =
      categoriesList?.map((cat) => ({
        value: cat.categoryName,
        label: cat.categoryName,
      })) ?? [];

    const cooksOptions =
      usersListData?.map((user) => ({
        value: user.username,
        label: user.username,
      })) ?? [];

    const form = useForm({
      defaultValues: defaultFormValues,
    });
    const offerPrice = form.watch("offerPrice");
    const foodType = form.watch("foodType");
    
    const formFields = [
      {
        name: "menuItemName",
        label: "Food Name",
        type: "input",
        placeholder: "Enter food name",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Food name is required",
          validate: async (value) => {
            if (!value) return true;
            return true
            const { data: isValid } = await validateField("menuItem", value).queryFn();
            return isValid || "This menu item name already exists";
          },
        },
        onBlur: async (e) => {
          const value = e.target.value;
          if (value && !value) {
            const { data: isValid } = await validateField("menuItem", value).queryFn();
            if (!isValid) {
              form.setValue("menuItemName", value);
              form.trigger("menuItemName");
              form.setError("menuItemName", {
                type: "manual",
                message: "This menu item name already exists",
              });
            }
          }
        },
      },
      {
        name: "foodType",
        label: "Food Type",
        type: "select",
        options: [
          { value: "MENU_ITEM", label: "Food" },
          { value: "COMBO", label: "Combo" },
          // { value: "Variant", label: "Variant" },
        ],
        placeholder: "Select food type",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Food type is required",
        },
        disabled: isEdit,
      },
      {
        name: "comboItemSet",
        label: "Combo Items",
        type: "custom",
        Component: ComboItemsInput,
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        hidden: foodType !== "COMBO",
        rules: {
          validate: (value) => {
            if (foodType !== "COMBO") return true;
            if (!value || !Array.isArray(value) || value.length === 0) {
              return "At least one combo item is required";
            }
            for (const row of value) {
              if (!row.item) return "Each combo item must have a menu item selected";
              if (!row.qty || isNaN(row.qty) || Number(row.qty) < 1) return "Each combo item must have a valid quantity";
            }
            return true;
          }
        }
      },
      {
        name: "price",
        label: "Price",
        type: "input",
        placeholder: "Enter price",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Price is required",
          pattern: {
            value: /^\d+(\.\d{1,2})?$/,
            message: "Please enter a valid price (e.g. 10.99)",
          },
        },
      },
      {
        name: "description",
        label: "Description",
        type: "textarea",
        placeholder: "Enter food description",
        fieldCol: "col-span-12 md:col-span-12",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "timeIntervalSet",
        label: "Timings",
        type: "custom",
        Component: TimeIntervalSetInput,
        fieldCol: "col-span-12 md:col-span-8",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Timings are required",
          validate: (value) => {
            if (!value || !Array.isArray(value)) {
              return "Timings are required";
            }
            
            for (const interval of value) {
              if (!interval.startTime || interval.startTime.trim() === "") {
                return "Available from time is required";
              }
              
              if (!interval.endTime || interval.endTime.trim() === "") {
                return "Available to time is required";
              }
              
              const startTime = new Date(`2000-01-01T${interval.startTime}`);
              const endTime = new Date(`2000-01-01T${interval.endTime}`);
              
              if (startTime >= endTime) {
                return "Available from time must be earlier than available to time";
              }
            }
            
            return true;
          }
        }
      },
      {
        name: "count",
        label: "Count",
        type: "input",
        placeholder: "e.g. 120 / day",
        fieldCol: "col-span-12 md:col-span-4",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Count is required",
          pattern: {
            value: /^\d+$/,
            message: "Please enter a valid number",
          },
        },
      },
      {
        name: "cookSet",
        label: "Responsible Cooks",
        type: "multiSelect",
        options: cooksOptions,
        placeholder: isUserListLoading ? "Loading cooks..." : "Select cooks",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "At least one cook must be selected",
          validate: (value) => value.length > 0 || "At least one cook must be selected",
        },
        showAllOption: true,
        disabled: isUserListLoading
      },
      {
        name: "availability",
        label: "Availability",
        type: "multiSelect",
        options: availabilityOptions,
        placeholder: "Select available days",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "At least one day must be selected",
          validate: (value) => value.length > 0 || "At least one day must be selected",
        },
        showAllOption: true
      },
      {
        name: "offerPrice",
        label: "Offer Price",
        type: "input",
        placeholder: "Enter offer price",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          validate: (value) => {
            if (!value) return true;
            return /^\d+(\.\d{1,2})?$/.test(value) || "Please enter a valid offer price";
          },
        },
      },
      {
        name: "offerFrom",
        label: "Offer From",
        type: "dateTimePicker",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        hidden: !offerPrice,
        rules: {
          validate: (value) => {
            if (!form.getValues("offerPrice")) return true;
            if (!value) return "Offer start date is required when offer price is set";
            const offerTo = form.getValues("offerTo");
            if (offerTo && new Date(value) >= new Date(offerTo)) {
              return "Offer start date must be earlier than offer end date";
            }
            return true;
          },
        },
      },
      {
        name: "offerTo",
        label: "Offer To",
        type: "dateTimePicker",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        hidden: !offerPrice,
        rules: {
          validate: (value) => {
            if (!form.getValues("offerPrice")) return true;
            if (!value) return "Offer end date is required when offer price is set";
            const offerFrom = form.getValues("offerFrom");
            if (offerFrom && new Date(value) <= new Date(offerFrom)) {
              return "Offer end date must be later than offer start date";
            }
            return true;
          },
        },
      },
      {
        name: "preparationTime",
        label: "Preparation Time",
        type: "input",
        placeholder: "Time in minutes",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "Preparation time is required",
          pattern: {
            value: /^\d+$/,
            message: "Please enter a valid number",
          },
        },
      },
      {
        name: "categorySet",
        label: "Categories",
        type: "multiSelect",
        options: categoryOptions,
        placeholder: isCategoryListLoading ? "Loading categories..." : "Select categories",
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
        rules: {
          required: "At least one category must be selected",
          validate: (value) => value.length > 0 || "At least one category must be selected",
        },
        showAllOption: true,
        disabled: isCategoryListLoading
      },
      {
        name: "acceptBulkOrders",
        label: "Accept Bulk Orders",
        type: "toggleGroup",
        options: ["yes", "no"],
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "onlyVeg",
        label: "Food Type",
        type: "toggleGroup",
        options: ["veg", "non-veg"],
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "onlyForCombos",
        label: "Only for Combos",
        type: "toggleGroup",
        options: ["yes", "no"],
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "hotelSpecial",
        label: "Hotel Special",
        type: "toggleGroup",
        options: ["yes", "no"],
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "active",
        label: "Active",
        type: "toggleGroup",
        options: ["yes", "no"],
        fieldCol: "col-span-12 md:col-span-6",
        labelCol: "col-span-12",
        controlCol: "col-span-12",
      },
      {
        name: "images",
        type: "imageUploader",
        fieldCol: "col-span-12 md:col-span-12",
        labelCol: "col-span-0",
        controlCol: "col-span-12",
      },
      {
        name: "addOns",
        label: "Add Ons",
        type: "custom",
        Component: AddOnInput,
        fieldCol: "col-span-12 md:col-span-12",
        labelCol: "col-span-6",
        controlCol: "col-span-12",
      },
    ];

    useEffect(() => {
      if (defaultValues && !isLoading) {
        form.reset({ ...defaultFormValues, ...defaultValues });
      }
    }, [defaultValues, isLoading, form]);

    useEffect(() => {
      const subscription = form.watch((value) => {
        onFormChange?.(value);
      });
      return () => subscription.unsubscribe();
    }, [form.watch, onFormChange]);

    useImperativeHandle(ref, () => ({
      submitForm: () => {
        form.handleSubmit(
          onSubmit,
          (errors) => {
            onError?.(errors);
          }
        )();
      },
    }));

    return (
      <div className="grid grid-cols-12">
        <CommonForm form={form} formFields={formFields} />
      </div>
    );
  }
);

FoodForm.displayName = "FoodForm";
export default FoodForm;
