import React from "react";
import { Form, FormField, FormItem, FormLabel, FormControl, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { Textarea } from "@/components/ui/textarea";
import ImageUploader from "./ImageUploader";
import TimeIntervalSetInput from "@/components/TimeIntervalSetInput";
import { DateTimePicker } from "@/components/customUIComponents/CustomeDateTimePicker";

const renderField = (fieldConfig, formField, form) => {
  switch (fieldConfig.type) {
    case "input":
      return (
        <Input
          className={fieldConfig.inputClassName || "border p-2 w-full rounded bg-yellow-50"}
          {...formField}
          placeholder={fieldConfig.placeholder}
          type={fieldConfig.inputType || "text"}
          onBlur={fieldConfig.onBlur || formField.onBlur}
        />
      );
    case "textarea":
      return (
        <Textarea
          {...formField}
          placeholder={fieldConfig.placeholder}
          className={fieldConfig.inputClassName || "w-full bg-yellow-50"}
        />
      );
    case "multiSelect":
      return (
        <MultiSelect
          className={fieldConfig.inputClassName || "border p-2 w-full rounded bg-yellow-50"}
          {...formField}
          options={fieldConfig.options}
          placeholder={fieldConfig.placeholder}
          showAllOption={fieldConfig.showAllOption}
          disabled={fieldConfig.disabled}
        />
      );
    case "toggleGroup":
      return (
        <ToggleGroup
          variant="outline"
          type="single"
          value={fieldConfig.value !== undefined ? fieldConfig.value : (formField.value ? fieldConfig.options[0] : fieldConfig.options[1])}
          onValueChange={fieldConfig.onValueChange || ((val) => formField.onChange(val === fieldConfig.options[0]))}
        >
          {fieldConfig.options.map((opt) => (
            <ToggleGroupItem
              className={`
                border p-2 w-full rounded
                hover:bg-yellow-50
                data-[state=on]:bg-yellow-50
                transition-colors duration-200
              `}
              key={opt}
              value={opt}
            >
              {opt.charAt(0).toUpperCase() + opt.slice(1)}
            </ToggleGroupItem>
          ))}
        </ToggleGroup>
      );
    case "dateTimePicker":
      return (
        <DateTimePicker
          className={fieldConfig.inputClassName || "border p-2 w-full rounded bg-yellow-50"}
          {...formField}
        />
      );
    case "imageUploader":
      return <ImageUploader {...formField} multiple />;
    case "custom":
      const CustomComponent = fieldConfig.Component;
      return (
        <CustomComponent
          value={formField.value}
          onChange={formField.onChange}
        />
      );
    default:
      return null;
  }
};

const CommonForm = ({ form, formFields }) => (
  <Form {...form}>
    {formFields.map((fieldConfig) => (
      !fieldConfig.hidden && (
        <FormField
          key={fieldConfig.name}
          control={form.control}
          name={fieldConfig.name}
          rules={fieldConfig.rules}
          render={({ field }) => (
            <FormItem className={`mb-4 mr-4 ${fieldConfig.fieldCol ?? "col-span-12"}`}>
              <div className="grid grid-cols-12 gap-2 items-start sm:items-center">
                <FormLabel className={`${fieldConfig.labelCol ?? "col-span-3"}`}>{fieldConfig.label}</FormLabel>
                <FormControl className={`${fieldConfig.controlCol ?? "col-span-9"} w-full`}>
                  {renderField(fieldConfig, field, form)}
                </FormControl>
              </div>
              <FormMessage />
            </FormItem>
          )}
        />
      )
    ))}
  </Form>
);

export default CommonForm; 