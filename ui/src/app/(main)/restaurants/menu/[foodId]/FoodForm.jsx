'use client';

import { useEffect, useState } from 'react';
import ImageUploader from './ImageUploader';
import MultiSelect from '@/components/customUIComponents/MultiSelect';

const FoodForm = ({ onFormChange }) => {
  const [formData, setFormData] = useState({
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
    hotelSpecial: 'no',
    categorySet: [],
    images: []
  });

  useEffect(() => {
    onFormChange(formData);
  }, [formData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleTimingChange = (index, field, value) => {
    const newTimings = [...formData.timeIntervalSet];
    newTimings[index][field] = value;
    setFormData(prev => ({ ...prev, timeIntervalSet: newTimings }));
  };
  
  const addTiming = () => {
    setFormData(prev => ({
      ...prev,
      timeIntervalSet: [...prev.timeIntervalSet, { startTime: '', endTime: '' }]
    }));
  };
  
  const removeTiming = (index) => {
    const newTimings = formData.timeIntervalSet.filter((_, i) => i !== index);
    setFormData(prev => ({ ...prev, timeIntervalSet: newTimings }));
  };

  const categoryOptions = [
    { value: 'breakfast', label: 'Breakfast' },
    { value: 'lunch', label: 'Lunch' },
    { value: 'dinner', label: 'Dinner' },
    { value: 'snacks', label: 'Snacks' },
    { value: 'dessert', label: 'Dessert' },
  ];

  const cooksOptions = [
    { value: 'breakfast', label: 'cook 1' },
    { value: 'lunch', label: 'cook 2' },
    { value: 'dinner', label: 'cook 3' },
    { value: 'snacks', label: 'cook 4' },
    { value: 'dessert', label: 'cook 5' },

  ];
  const availabilityOptions = [
    { value: 'monday', Label: 'Monday' },
    { value: 'tuesday', label: 'Tuesday' },
    { value: 'wednesday', label: 'Wednesday' },
    { value: 'thursday', label: 'Thursday' },
    { value: 'friday', label: 'Friday' },
    { value: 'saturday', label: 'Saturday' },
    { value: 'sunday', label: 'Sunday' },
  ]

  return (
    <form className="space-y-4" onSubmit={(e) => e.preventDefault()}>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="font-bold block">Food Name</label>
          <input
            name="menuItemName"
            value={formData.menuItemName}
            onChange={handleChange}
            className="border p-2 w-full rounded"
          />
        </div>
        <div>
          <label className="font-bold block">Price</label>
          <input
            name="price"
            value={formData.price}
            onChange={handleChange}
            className="border p-2 w-full rounded"
          />
        </div>
      </div>

        <div>
          <label className="font-bold block">Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            className="border p-2 w-full rounded bg-yellow-50"
          />
        </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="font-bold block">Responsible Cooks</label>
          <MultiSelect
            options={cooksOptions}
            value={formData.cookSet}
            onChange={(val) =>
              setFormData((prev) => ({
                ...prev,
                cookSet: val,
              }))
            }
            placeholder="Select cooks"
            className="border p-2 w-full rounded bg-yellow-50"
          />
          {/* <select
            name="cookSet"
            value={formData.cookSet}
            onChange={handleChange}
            className="border p-2 w-full rounded bg-yellow-50"
          >
            <option value="">Select</option>
            <option value="Cook 1, Cook 2">Cook 1, Cook 2</option>
            <option value="Cook 3">Cook 3</option>
          </select> */}
        </div>

        <div>
          <label className="font-bold block">Count</label>
          <input
            name="count"
            type="text"
            value={formData.count}
            onChange={handleChange}
            placeholder="e.g. 120 / day"
            className="border p-2 w-full rounded bg-yellow-50"
          />
        </div>
      </div>

        <div className="mb-4">
          <label className="font-bold block mb-2">Timings</label>
          <div className="bg-gray-200 p-4 rounded">
            {formData.timeIntervalSet.map((timeSlot, index) => (
              <div key={index} className="flex gap-4 mb-2 items-center">
                <div className="flex-1">
                  <label className="text-sm">Available From</label>
                  <input
                    type="time"
                    value={timeSlot.startTime}
                    onChange={(e) => handleTimingChange(index, 'from', e.target.value)}
                    className="border p-2 w-full rounded bg-yellow-50"
                  />
                </div>
                <div className="flex-1">
                  <label className="text-sm">Available To</label>
                  <input
                    type="time"
                    value={timeSlot.endTime}
                    onChange={(e) => handleTimingChange(index, 'to', e.target.value)}
                    className="border p-2 w-full rounded bg-yellow-50"
                  />
                </div>
                {formData.timeIntervalSet.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeTiming(index)}
                    className="text-red-500 text-xl"
                  >
                    &times;
                  </button>
                )}
              </div>
            ))}

            <button
              type="button"
              onClick={addTiming}
              className="mt-2 px-3 py-1 bg-orange-500 text-white rounded-full"
            >
              +
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="font-bold block">Availability</label>
            <MultiSelect
              options={availabilityOptions}
              value={formData.availability}
              onChange={(val) =>
                setFormData((prev) => ({
                  ...prev,
                  availability: val,
                }))
              }
              placeholder="Select availabile days"
            />
            {/* <select
              name="availability"
              value={formData.availability}
              onChange={handleChange}
              className="border p-2 w-full rounded bg-yellow-50"
            >
              <option value="">Select Days</option>
              <option value="Saturday, Sunday">Saturday, Sunday</option>
              <option value="All Days">All Days</option>
            </select> */}
          </div>

          <div>
            <label className="font-bold block">Offer Price</label>
            <input
              name="offerPrice"
              value={formData.offerPrice}
              onChange={handleChange}
              className="border p-2 w-full rounded bg-yellow-50"
            />
          </div>
        {/* </div> */}

        {/* <div className="flex gap-4 items-end"> */}
          <div className="flex-1">
            <label className="font-bold block">Offer From</label>
            <input
              name="offerFrom"
              type="datetime-local"
              value={formData.offerFrom}
              onChange={handleChange}
              className="border p-2 w-full rounded"
            />
          </div>
          <div className="flex-1">
            <label className="font-bold block">Offer To</label>
            <input
              name="offerTo"
              type="datetime-local"
              value={formData.offerTo}
              onChange={handleChange}
              className="border p-2 w-full rounded"
            />
          </div>
        {/* </div> */}

        <div>
          <label className="font-bold block">Preparation Time</label>
          <input
            name="preparationTime"
            value={formData.preparationTime}
            onChange={handleChange}
            placeholder="Time in minutes"
            className="border p-2 w-full rounded bg-yellow-50"
          />
        </div>

        <div>
          <label className="font-bold block">Accept Bulk Orders</label>
          <div className="flex gap-2">
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.acceptBulkOrders === true ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, acceptBulkOrders: true }))}
            >
              Yes
            </button>
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.acceptBulkOrders === false ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, acceptBulkOrders: false }))}
            >
              No
            </button>
          </div>
        </div>

        <div>
          <label className="font-bold block">Food Type</label>
          <div className="flex gap-2">
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.onlyVeg === true ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, onlyVeg: true }))}
            >
              Veg
            </button>
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.onlyVeg === false ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, onlyVeg: false }))}
            >
              Non-Veg
            </button>
          </div>
        </div>

        <div>
          <label className="font-bold block">Only for Combos</label>
          <div className="flex gap-2">
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.onlyForCombos === true ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, onlyForCombos: true }))}
            >
              Yes
            </button>
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.onlyForCombos === false ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, onlyForCombos: false }))}
            >
              No
            </button>
          </div>
        </div>

        <div>
          <label className="font-bold block">Add to Hotel’s Special</label>
          <div className="flex gap-2">
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.hotelSpecial === 'yes' ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, hotelSpecial: 'yes' }))}
            >
              Yes
            </button>
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.hotelSpecial === 'no' ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, hotelSpecial: 'no' }))}
            >
              No
            </button>
          </div>
        </div>

        <div>
          <label className="font-bold block">Active</label>
          <div className="flex gap-2">
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.active === true ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, active: true }))}
            >
              Yes
            </button>
            <button
              type="button"
              className={`px-4 py-2 rounded ${formData.active === false ? 'bg-yellow-500' : 'bg-gray-200'}`}
              onClick={() => setFormData(prev => ({ ...prev, active: false }))}
            >
              No
            </button>
          </div>
        </div>
        </div>

        <div>
          <label className="font-bold block mb-1">Categories</label>
          <MultiSelect
            options={categoryOptions}
            value={formData.categorySet}
            onChange={(val) =>
              setFormData((prev) => ({
                ...prev,
                categorySet: val,
              }))
            }
            placeholder="Select categories"
          />
          <div className="flex flex-wrap gap-2 mt-2 bg-gray-600/20 p-6 overflow-auto h-20">
            {(formData.categorySet || []).map((val) => {
              const option = categoryOptions.find((o) => o.value === val);
              return (
                <span
                  key={val}
                  className="flex items-center gap-1 rounded bg-black text-white px-2 py-1 text-sm"
                >
                  {option?.label || val}
                  <div
                    className="cursor-pointer h-4 w-4"
                    onClick={(e) => {
                      e.stopPropagation();
                      setFormData((prev) => ({
                        ...prev,
                        categorySet: prev.categorySet.filter((v) => v !== val),
                      }));
                    }}
                  />
                </span>
              );
            })}
          </div>
        </div>


        <div>
          <label className="font-bold block">Images</label>
          <div className="flex gap-2">
          <ImageUploader
            multiple
            onChange={(imagesArray) => setFormData(prev => ({ ...prev, images: imagesArray }))}
          />
          </div>
        </div>

    </form>
  );
};

export default FoodForm;
