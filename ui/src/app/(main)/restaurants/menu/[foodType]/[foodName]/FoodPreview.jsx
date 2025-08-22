'use client';

import Image from "next/image";

const FoodPreview = ({ formData }) => {
  return (
    <div className="border p-4 rounded-lg bg-yellow-50 sticky top-4 w-full max-w-sm">
      <div className="border-2 border-yellow-500 rounded overflow-hidden">
        <Image
          src={formData.images?.find((x) => x.isMain)?.preview}
          alt="Preview"
          className="w-full h-40 object-cover"
        />
        <div className="p-2">
          <div className="text-lg font-bold">{formData.menuItemName || 'Food Name'}</div>
          <div className="text-sm text-gray-600">Madurai Muniees</div>
          <div className="text-xs text-gray-500">Elcot IT Park, Hosur</div>
          <div className="text-right text-sm">
            ₹ {formData.price || '0.00'} • {formData.preparationTime || '20'} mins
          </div>
          <div className="mt-2 font-semibold">Add to Hotel’s Special</div>
          <div className="flex gap-4 mt-1">
            <span className={`px-4 py-1 rounded ${formData.hotelSpecial ? 'bg-yellow-500 text-white' : 'border'}`}>Yes</span>
            <span className={`px-4 py-1 rounded ${!formData.hotelSpecial ? 'bg-yellow-500 text-white' : 'border'}`}>No</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FoodPreview;
