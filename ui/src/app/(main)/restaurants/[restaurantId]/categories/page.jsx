"use client";
import { deleteCategoryItem, getCategoriesListOptions, patchUpdateCategoriesList } from "@/lib/api/api";
import { getCategoryColumns } from "./columns";
import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
import { getDistinctCategories } from "@/lib/utils/helper";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { usePathname, useRouter } from "next/navigation";

const Categories = () => {
   const router = useRouter();
   const pathName = usePathname();
   const id = pathName.split("/")[2];
   const queryClient = useQueryClient();
   const { data: categoriesList, isLoading, error } = useQuery(getCategoriesListOptions(id));
   const patchMutation = useMutation(patchUpdateCategoriesList(queryClient));
   const deleteMutation = useMutation(deleteCategoryItem(queryClient));
 
   if (isLoading) return <p>Loading categories...</p>;
   if (error) return <p>Error loading categories: {error.message}</p>;
 
  const handleToggle = (id, value) => {
    patchMutation.mutate({ id, fields: { active: value } });
    //  setTableData((prev) =>
    //   prev.map((row) => (row.id === id ? { ...row, public: value } : row))
    // );
  };

  const handleEditClick = (id) => {
    console.log("Edit clicked for id: ", id);
    router.push(`${pathName}/${id}`)
  }

  
  const handleDeleteClick = (id) => {
    console.log("Delete clicked for id: ", id);
    deleteMutation.mutate({ id });
  }

  const columns = getCategoryColumns(handleToggle, handleEditClick, handleDeleteClick);

  return (
    <div>
      <CustomDataTable
        columns={columns}
        data={categoriesList || []}
        tabName="Categories"
        handleHeaderButtonClick={() => {
          console.log("Add category URL");
        }}
        headerButtonName="Add Category"
        headerDialogType="category"
      />
    </div>
  );
};

export default Categories;




// import { useState } from "react";
// import { deleteCategoryItem, getCategoriesListOptions, patchUpdateCategoriesList } from "@/lib/api/api";
// import { getCategoryColumns } from "./columns";
// import { CustomDataTable } from "@/components/customUIComponents/CustomDataTable";
// import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
// import { usePathname, useRouter } from "next/navigation";
// import CustomPopup from "@/components/CustomPopup"; // Import your popup component

// const Categories = () => {
//   const router = useRouter();
//   const pathName = usePathname();
//   const id = pathName.split("/")[2];
//   const queryClient = useQueryClient();
  
//   const { data: categoriesList, isLoading, error } = useQuery(getCategoriesListOptions(id));
//   const patchMutation = useMutation(patchUpdateCategoriesList(queryClient));
//   const deleteMutation = useMutation(deleteCategoryItem(queryClient));

//   const [isPopupOpen, setIsPopupOpen] = useState(false);
//   const [categoryToEdit, setCategoryToEdit] = useState(null); // Store the category to edit
  
//   if (isLoading) return <p>Loading categories...</p>;
//   if (error) return <p>Error loading categories: {error.message}</p>;

//   // Toggles active/inactive status for category
//   const handleToggle = (id, value) => {
//     patchMutation.mutate({ id, fields: { active: value } });
//   };

//   const handleEditClick = (id) => {
//     console.log("Edit clicked for id: ", id);
//     const category = categoriesList.find((item) => item.id === id);
//     setCategoryToEdit(category); // Set the category to edit
//     setIsPopupOpen(true); // Open the popup
//   };

//   const handleDeleteClick = (id) => {
//     console.log("Delete clicked for id: ", id);
//     deleteMutation.mutate({ id });
//   };

//   const columns = getCategoryColumns(handleToggle, handleEditClick, handleDeleteClick);

//   return (
//     <div>
//       <CustomDataTable
//         columns={columns}
//         data={categoriesList || []}
//         tabName="Categories"
//         handleHeaderButtonClick={() => {
//           console.log("Add category URL");
//         }}
//         headerButtonName="Add Category"
//         headerDialogType="category"
//       />

//       {/* CustomPopup to edit category */}
//       {isPopupOpen && (
//         <CustomPopup
//           type="edit" // or any type you need
//           trigger={<></>} // Empty trigger since popup is directly controlled
//           dialogDescription="Edit Category"
//           onConfirm={() => {
//             // You could also call an API to update the category here if needed
//             console.log("Category updated:", categoryToEdit);
//             setIsPopupOpen(false); // Close the popup after confirming
//           }}
//           // Pass the category to the CustomPopup to allow editing
//           category={categoryToEdit}
//           onClose={() => setIsPopupOpen(false)} // Close the popup when canceled
//         />
//       )}
//     </div>
//   );
// };

// export default Categories;
