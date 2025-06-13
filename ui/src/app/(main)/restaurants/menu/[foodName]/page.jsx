"use client";

import { useRef, useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { ChevronLeft } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Card, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import FoodForm from "@/components/FoodForm";
import FoodCard from "@/components/customUIComponents/FoodCard";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useIsMobile } from "@/hooks/use-mobile";
import { useToast } from "@/hooks/use-toast";
import { Loader2 } from "lucide-react";
import LoadingState from "@/components/customUIComponents/LoadingState";
import ErrorState from "@/components/customUIComponents/ErrorState";

import {
  createMenuItemFood,
  patchUpdateMenuItemList,
  getMenuItemFood,
  deleteMenuItem,
} from "@/lib/api/api";
import { getChangedFields } from "@/lib/utils/helper";

const MenuFood = () => {
  const router = useRouter();
  const { foodName } = useParams();
  const queryClient = useQueryClient();
  const formRef = useRef();
  const [formData, setFormData] = useState({});
  const isEdit = foodName && foodName !== "add_food";
  const isMobile = useIsMobile();
  const [showPreview, setShowPreview] = useState(false);
  const { toast } = useToast();

  const { data: existingData, isLoading, error } = useQuery({
    ...getMenuItemFood(foodName),
    enabled: isEdit && !!foodName,
  });

  useEffect(() => {
    if (existingData) {
      setFormData({
        ...existingData,
        tag: existingData.hotelSpecial ? "Hotel special" : "illa",
      });
    }
  }, [existingData]);

  const handleFormChange = (data) => {
    setFormData({
      ...data,
      tag: data.hotelSpecial ? "Hotel special" : "illa",
    });
  };

  const handleFormSubmit = () => {
    formRef.current?.submitForm();
  };

  const handleFormError = (errors) => {
    const firstError = Object.values(errors)[0];
    if (firstError?.message) {
      toast({
        variant: "destructive",
        title: "Validation Error",
        description: firstError.message,
      });
    }
  };

  const deleteMutation = useMutation({
    ...deleteMenuItem(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Food item deleted successfully",
      });
      router.back();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete food item",
        variant: "destructive",
      });
    },
  });

  const createMutation = useMutation({
    ...createMenuItemFood(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Food item created successfully",
      });
      router.back();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create food item",
        variant: "destructive",
      });
    },
  });

  const updateMutation = useMutation({
    ...patchUpdateMenuItemList(queryClient),
    onSuccess: () => {
      toast({
        variant: "success",
        title: "Success",
        description: "Food item updated successfully",
      });
      router.back();
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update food item",
        variant: "destructive",
      });
    },
  });

  const handleCreateSubmit = (data) => {
    createMutation.mutate({
      menuItemName: foodName,
      fields: data,
    });
  }

  const handleUpdateSubmit = (data) => {
    const formValues = getChangedFields(existingData, data);
    if (Object.keys(formValues).length !== 0) {
      updateMutation.mutate({
        menuItemName: foodName,
        fields: formValues,
      });
    }
  };

    const handleSubmit = async (data) => {
    isEdit ? handleUpdateSubmit(data) : handleCreateSubmit(data);
  };

  const handleDelete = () => {
    deleteMutation.mutate({ menuItemName: foodName });
  };

  const handleBackClick = () => {
    router.back();
  };

  if (isEdit && error) {
    return (
      <ErrorState 
        title={`Error loading ${foodName} details`} 
        message={error.message}
        action={
          <Button onClick={handleBackClick} className="mt-4">
            Go Back
          </Button>
        }
      />
    );
  }

  if (isEdit && isLoading) {
    return <LoadingState message={`Loading ${foodName} details...`} />;
  }

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
              disabled={deleteMutation.isPending || createMutation.isPending || updateMutation.isPending}
            >
              <ChevronLeft />
            </Button>
            <h1 className="text-2xl font-bold">
              {isEdit ? "Edit Food" : "Add Food"}
            </h1>
          </div>
          <div className="space-x-2 flex">
            {isEdit && (
              <Button
                variant="destructive"
                onClick={handleDelete}
                className="px-4"
                disabled={deleteMutation.isPending || createMutation.isPending || updateMutation.isPending}
              >
                {deleteMutation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Deleting...
                  </>
                ) : (
                  "Delete"
                )}
              </Button>
            )}
            <Button
              className="bg-yellow-500 hover:bg-yellow-600 text-black px-4"
              onClick={handleFormSubmit}
              disabled={deleteMutation.isPending || createMutation.isPending || updateMutation.isPending}
            >
              {createMutation.isPending || updateMutation.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {isEdit ? "Updating..." : "Creating..."}
                </>
              ) : (
                "Submit"
              )}
            </Button>
          </div>
        </div>
      </CardTitle>

      <CardContent className="sticky top-16 z-10 overflow-hidden">
        <div className="flex flex-1 overflow-hidden">
          <div
            className="flex-1 overflow-y-auto p-2 pt-4"
            style={{ maxHeight: "calc(100vh - 170px)" }}
          >
            <FoodForm
              ref={formRef}
              onFormChange={handleFormChange}
              onSubmit={handleSubmit}
              defaultValues={formData}
              isLoading={isLoading}
              onError={handleFormError}
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

      {isMobile && (
        <>
          <Button
            onClick={() => setShowPreview(true)}
            className="fixed bottom-4 right-4 bg-yellow-500 hover:bg-yellow-600 text-black rounded-full p-4 shadow-lg z-50"
          >
            👁️
          </Button>

          {showPreview && (
            <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
              <div className="bg-white w-11/12 max-w-md max-h-[90vh] rounded-lg p-4 relative flex flex-col">
                <Button
                  variant="ghost"
                  className="absolute top-2 right-2 text-red-500 text-xl"
                  onClick={() => setShowPreview(false)}
                >
                  ❌
                </Button>
                <h2 className="text-lg font-semibold text-gray-700 mb-4 text-center">
                  Food preview
                </h2>
                <ScrollArea className="flex justify-center">
                  <div className="p-4">
                    <FoodCard food={formData} />
                    <p className="text-sm text-gray-500 mt-4 text-center px-2">
                      This is how your food will be displayed. <br />
                      Prepare wisely.
                    </p>
                  </div>
                </ScrollArea>
              </div>
            </div>
          )}
        </>
      )}
    </Card>
  );
};

export default MenuFood;
