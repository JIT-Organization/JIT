'use client';

import { useForm } from 'react-hook-form';
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import MultiSelect from '@/components/customUIComponents/MultiSelect';
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group';
import { forwardRef, useEffect, useImperativeHandle } from 'react';
import ImageUploader from './ImageUploader';
import TimeIntervalSetInput from './TimeIntervalSetInput';
import { DateTimePicker } from '@/components/customUIComponents/CustomeDateTimePicker';
import { useQuery } from '@tanstack/react-query';
import { getCategoriesListOptions, getUsersListOptions } from '@/lib/api/api';
import { Textarea } from '@/components/ui/textarea';

const defaultFormValues = {
  menuItemName: '',
  price: '',
  description: '',
  cookSet: [],
  count: '',
  timeIntervalSet: [{ startTime: '', endTime: '' }],
  availability: [],
  offerPrice: '',
  offerFrom: '',
  offerTo: '',
  preparationTime: '',
  acceptBulkOrders: false,
  onlyVeg: true,
  onlyForCombos: false,
  active: true,
  hotelSpecial: false,
  categorySet: [],
  images: [],
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
  { value: 'monday', label: 'Monday' },
  { value: 'tuesday', label: 'Tuesday' },
  { value: 'wednesday', label: 'Wednesday' },
  { value: 'thursday', label: 'Thursday' },
  { value: 'friday', label: 'Friday' },
  { value: 'saturday', label: 'Saturday' },
  { value: 'sunday', label: 'Sunday' },
];

const renderField = (fieldConfig, formField) => {
  switch (fieldConfig.type) {
    case 'input':
      return <Input 
      className="border p-2 w-full rounded bg-yellow-50" {...formField} placeholder={fieldConfig.placeholder} />;
    case 'textarea':
      return (
        <Textarea
          {...formField}
          placeholder={fieldConfig.placeholder}
          className="w-full bg-yellow-50"
        />
      );
    case 'multiSelect':
      return (
        <MultiSelect
          className="border p-2 w-full rounded bg-yellow-50"
          {...formField}
          options={fieldConfig.options}
          placeholder={fieldConfig.placeholder}
        />
      );
    case 'toggleGroup':
      return (
        <ToggleGroup
          variant="outline"
          type="single"
          value={formField.value ? fieldConfig.options[0] : fieldConfig.options[1]}
          onValueChange={(val) => formField.onChange(val === fieldConfig.options[0])}
        >
          {fieldConfig.options.map((opt) => (
            <ToggleGroupItem  
              className={`
                border p-2 w-full rounded
                hover:bg-yellow-50
                data-[state=on]:bg-yellow-50
                transition-colors duration-200
              `} 
              key={opt} value={opt}>
              {opt.charAt(0).toUpperCase() + opt.slice(1)}
            </ToggleGroupItem>
          ))}
        </ToggleGroup>
      );
    case 'dateTimePicker':
      return <DateTimePicker className="border p-2 w-full rounded bg-yellow-50" {...formField} />;
    case 'imageUploader':
      return <ImageUploader {...formField} multiple />;
    case 'custom':
      const CustomComponent = fieldConfig.Component;
      return <CustomComponent value={formField.value} onChange={formField.onChange} />;
    default:
      return null;
  }
};

const FoodForm = forwardRef(({ onFormChange, onSubmit, defaultValues, isLoading }, ref) => {

  const { data: categoriesList, isLoading: isCategoryListLoading } = useQuery(getCategoriesListOptions());
  const { data: usersListData, isLoading: isUserListLoading } = useQuery(getUsersListOptions());

  const categoryOptions = categoriesList?.map((cat) => ({
    value: cat.id,
    label: cat.categoryName,
  })) ?? [];

  const cooksOptions = usersListData?.map((user) => ({
    value: user.id,
    label: user.name,
  })) ?? [];

  const formFields = [
    { name: 'menuItemName', label: 'Food Name', type: 'input', placeholder: 'Enter food name', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'price', label: 'Price', type: 'input', placeholder: 'Enter price', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'description', label: 'Description', type: 'textarea', placeholder: 'Enter food description', fieldCol: 'col-span-12 md:col-span-12', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'timeIntervalSet', label: 'Timings', type: 'custom', Component: TimeIntervalSetInput , fieldCol: 'col-span-12 md:col-span-8', labelCol: 'col-span-12', controlCol: 'col-span-12'},
    { name: 'count', label: 'Count', type: 'input', placeholder: 'e.g. 120 / day', fieldCol: 'col-span-12 md:col-span-4', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'cookSet', label: 'Responsible Cooks', type: 'multiSelect', options: cooksOptions, placeholder: 'Select cooks', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'availability', label: 'Availability', type: 'multiSelect', options: availabilityOptions, placeholder: 'Select available days', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'offerPrice', label: 'Offer Price', type: 'input', placeholder: 'Enter offer price', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'offerFrom', label: 'Offer From', type: 'dateTimePicker', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'offerTo', label: 'Offer To', type: 'dateTimePicker', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'preparationTime', label: 'Preparation Time', type: 'input', placeholder: 'Time in minutes', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'categorySet', label: 'Categories', type: 'multiSelect', options: categoryOptions, placeholder: 'Select categories', fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12',},
    { name: 'acceptBulkOrders', label: 'Accept Bulk Orders', type: 'toggleGroup', options: ['yes', 'no'], fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'onlyVeg', label: 'Food Type', type: 'toggleGroup', options: ['veg', 'non-veg'], fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'onlyForCombos', label: 'Only for Combos', type: 'toggleGroup', options: ['yes', 'no'], fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12' },
    { name: 'hotelSpecial', label: 'Hotel Special', type: 'toggleGroup', options: ['yes', 'no'], fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12'},
    { name: 'active', label: 'Active', type: 'toggleGroup', options: ['yes', 'no'], fieldCol: 'col-span-12 md:col-span-6', labelCol: 'col-span-12', controlCol: 'col-span-12',},
    { name: 'images', type: 'imageUploader', fieldCol: 'col-span-12 md:col-span-12', labelCol: 'col-span-0', controlCol: 'col-span-12',},
  ];

  const form = useForm({
    defaultValues: defaultFormValues,
  });
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
      form.handleSubmit(onSubmit)();
    },
  }));

  return (
    <div className="grid grid-cols-12">
      <Form {...form}>
        {formFields.map((fieldConfig) => (
          <FormField
            key={fieldConfig.name}
            control={form.control}
            name={fieldConfig.name}
            render={({ field }) => (
              <FormItem className={`mb-4 mr-4 ${fieldConfig.fieldCol ?? 'col-span-12'}`}>
                <div className="grid grid-cols-12 gap-2 items-start sm:items-center">
                  <FormLabel className={`${fieldConfig.labelCol ?? 'col-span-3'}`}>
                    {fieldConfig.label}
                  </FormLabel>
                  <FormControl className={`${fieldConfig.controlCol ?? 'col-span-9'} w-full`}>
                    {renderField(fieldConfig, field)}
                  </FormControl>
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
        ))}
      </Form>
    </div>
  );
});

FoodForm.displayName = 'FoodForm';
export default FoodForm;
