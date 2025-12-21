import React from "react";
import { Form, FormField, FormItem, FormLabel, FormControl, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { Textarea } from "@/components/ui/textarea";
import ImageUploader from "./ImageUploader";
import TimeIntervalSetInput from "@/components/TimeIntervalSetInput";
import { DateTimePicker } from "@/components/customUIComponents/CustomeDateTimePicker";

const renderField = (fieldConfig, formField, formRef) => {
  switch (fieldConfig.type) {
    case "input":
      return (
        <Input
          className={fieldConfig.inputClassName || "border p-2 w-full rounded input"}
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
          className={fieldConfig.inputClassName || "w-full input"}
        />
      );
    case "multiSelect":
      return (
        <MultiSelect
          className={fieldConfig.inputClassName || "border p-2 w-full rounded input"}
          {...formField}
          options={fieldConfig.options}
          placeholder={fieldConfig.placeholder}
          showAllOption={fieldConfig.showAllOption}
          disabled={fieldConfig.disabled}
          containerRef={formRef}
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
                hover:bg-[var(--button-bg)] hover:text-[var(--button-text)]
                data-[state=on]:bg-[var(--button-bg)] data-[state=on]:text-[var(--button-text)]
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
          className={fieldConfig.inputClassName || "border p-2 w-full rounded input"}
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
    case "select":
      return (
        <select
          className={fieldConfig.inputClassName || "input flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-base shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"}
          {...formField}
          disabled={fieldConfig.disabled}
        >
          {/* <option value="" disabled>{fieldConfig.placeholder || "Select an option"}</option> */}
          {fieldConfig.options && fieldConfig.options.map((opt) => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>
      );
    default:
      return null;
  }
};

const CommonForm = ({ form, formFields, formRef }) => (
  <Form {...form}>
    <form ref={formRef}>
      {formFields.map((fieldConfig) => (
        !fieldConfig.hidden && (
          <FormField
            key={fieldConfig.name}
            control={form.control}
            name={fieldConfig.name}
            rules={fieldConfig.rules}
            render={({ field }) => (
              <FormItem className={`mb-4 mr-4 ${fieldConfig.fieldCol ?? "col-span-12"}`}>
                <FormLabel className={`${fieldConfig.labelCol ?? "col-span-3"}`}>{fieldConfig.label}</FormLabel>
                <FormControl className={`${fieldConfig.controlCol ?? "col-span-9"} w-full`}>
                  {renderField(fieldConfig, field, formRef)}
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )
      ))}
    </form>
  </Form>
);

export default CommonForm; 