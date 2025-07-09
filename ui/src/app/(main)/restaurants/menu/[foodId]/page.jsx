'use client';

import { useRef, useState, useEffect } from 'react';
import FoodForm from './FoodForm';
import FoodPreview from './FoodPreview';
import { useRouter, useParams } from 'next/navigation';
import { ChevronLeft } from 'lucide-react';
import {
  Card,
  CardTitle,
  CardContent,
} from '@/components/ui/card';
import { createMenuItemFood, patchUpdateMenuItemList, getMenuItemFood, deleteMenuItem } from '@/lib/api/api';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

const MenuFood = () => {
  const router = useRouter();
  const { foodId } = useParams();
  const queryClient = useQueryClient();
  const formRef = useRef();
  const [formData, setFormData] = useState({});
  const isEdit = foodId && !isNaN(Number(foodId));
  
  const { data:existingData, isLoading } = useQuery(getMenuItemFood(foodId));
  
  useEffect(() => {
    if (existingData) {
      setFormData(existingData);
    }
  }, [existingData]);
  
  const handleFormChange = (data) => {
    setFormData(data);
  };
  
  const handleSubmit = () => {
    formRef.current?.submitForm();
  };
  
  const deleteMutation = useMutation(deleteMenuItem(queryClient));
  const createMutation = useMutation(createMenuItemFood(queryClient));
  const updateMutation = useMutation(patchUpdateMenuItemList(queryClient));
  
  const handleFinalSubmit = (data) => {
    const mutationFn = isEdit ? updateMutation : createMutation;
    mutationFn.mutate({
      id: foodId,
      fields: data,
    });
  };
  
  const handleDelete = () => {
    console.log('Deleting:', formData);
    deleteMutation.mutate({id:foodId});
  };

  const handleBackClick = () => {
    router.back();
  };

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 bg-white shadow">
        <div className="p-4 border-b shadow flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <button
              onClick={handleBackClick}
              className="flex items-center text-yellow pr-4 rounded-md"
            >
              <ChevronLeft />
            </button>
            <h1 className="text-2xl font-bold">
              {isEdit ? 'Edit Food' : 'Add Food'}
            </h1>
          </div>
          <div className="space-x-2">
            {isEdit && (
              <button
                onClick={handleDelete}
                className="bg-red-600 text-white px-4 py-2 rounded"
              >
                Delete
              </button>
            )}
            <button
              onClick={handleSubmit}
              className="bg-yellow-500 text-black px-4 py-2 rounded"
            >
              Submit
            </button>
          </div>
        </div>
      </CardTitle>

      <CardContent className="sticky top-16 z-10 overflow-hidden">
        <div className="flex flex-1 overflow-hidden">
          <div
            className="flex-1 overflow-y-auto p-0 pt-4"
            style={{ maxHeight: 'calc(100vh - 170px)' }}
          >
            <FoodForm
              ref={formRef}
              onFormChange={handleFormChange}
              onSubmit={handleFinalSubmit}
              defaultValues={formData}
              isLoading={isLoading}
            />
          </div>

          <div className="hidden md:block w-[300px] border-l p-4 bg-white sticky top-0 overflow-y-auto">
            <FoodPreview formData={formData} />
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default MenuFood;
