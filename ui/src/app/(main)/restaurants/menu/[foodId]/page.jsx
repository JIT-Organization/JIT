'use client';

import { useRef, useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { ChevronLeft } from 'lucide-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Card,
  CardTitle,
  CardContent,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import FoodForm from './FoodForm';
import FoodCard from '@/components/customUIComponents/FoodCard';

import {
  createMenuItemFood,
  patchUpdateMenuItemList,
  getMenuItemFood,
  deleteMenuItem
} from '@/lib/api/api';

const MenuFood = () => {
  const router = useRouter();
  const { foodId } = useParams();
  const queryClient = useQueryClient();
  const formRef = useRef();
  const [formData, setFormData] = useState({});
  const isEdit = foodId && !isNaN(Number(foodId));

  const { data: existingData, isLoading } = useQuery(getMenuItemFood(foodId));

  useEffect(() => {
    if (existingData) {
      setFormData({
        ...existingData,
        tag: existingData.hotelSpecial ? 'Hotel special' : 'illa',
      });
    }
  }, [existingData]);

  const handleFormChange = (data) => {
    setFormData({
      ...data,
      tag: data.hotelSpecial ? 'Hotel special' : 'illa',
    });
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
    deleteMutation.mutate({ id: foodId });
  };

  const handleBackClick = () => {
    router.back();
  };

  return (
    <Card>
      <CardTitle className="sticky top-16 z-20 bg-white shadow">
        <div className="p-4 border-b shadow flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <Button
              variant="ghost"
              onClick={handleBackClick}
              className="flex items-center text-yellow pr-4"
              size="icon"
            >
              <ChevronLeft />
            </Button>
            <h1 className="text-2xl font-bold">
              {isEdit ? 'Edit Food' : 'Add Food'}
            </h1>
          </div>
          <div className="space-x-2 flex">
            {isEdit && (
              <Button
                variant="destructive"
                onClick={handleDelete}
                className="px-4"
              >
                Delete
              </Button>
            )}
            <Button
              className="bg-yellow-500 hover:bg-yellow-600 text-black px-4"
              onClick={handleSubmit}
            >
              Submit
            </Button>
          </div>
        </div>
      </CardTitle>

      <CardContent className="sticky top-16 z-10 overflow-hidden">
        <div className="flex flex-1 overflow-hidden">
          <div
            className="flex-1 overflow-y-auto p-2 pt-4"
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

          <div className="hidden md:flex w-[300px] border-l p-4 bg-white sticky top-0 overflow-y-auto items-center justify-center flex-col">
            <h2 className="text-lg font-semibold text-gray-700 mb-2">
              Food preview
            </h2>

            <FoodCard food={formData} />

            <p className="text-sm text-gray-500 mt-4 text-center px-2">
              This is how your food will be displayed. <br />
              Prepare wisely.
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default MenuFood;
