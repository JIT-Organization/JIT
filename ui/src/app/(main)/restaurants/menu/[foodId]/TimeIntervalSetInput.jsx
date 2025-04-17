'use client';

import { cn } from '@/lib/utils';
import React from 'react';

const TimeIntervalSetInput = ({ value = [], onChange, className }) => {
  const handleTimingChange = (index, field, val) => {
    const updated = [...value];
    updated[index][field] = val;
    onChange(updated);
  };

  const addTiming = () => {
    onChange([...value, { startTime: '', endTime: '' }]);
  };

  const removeTiming = (index) => {
    const updated = value.filter((_, i) => i !== index);
    onChange(updated);
  };

  return (
    <div className={cn("bg-gray-100 p-4 rounded", className)}>
      {value.map((slot, index) => (
        <div key={index} className="flex gap-4 mb-2 items-end">
          <div className="flex-1">
            <label htmlFor={`start-${index}`} className="text-sm block">Available From</label>
            <input
              id={`start-${index}`}
              type="time"
              value={slot.startTime}
              onChange={(e) => handleTimingChange(index, 'startTime', e.target.value)}
              className="border p-2 w-full rounded bg-yellow-50"
            />
          </div>
          <div className="flex-1">
            <label htmlFor={`end-${index}`} className="text-sm block">Available To</label>
            <input
              id={`end-${index}`}
              type="time"
              value={slot.endTime}
              onChange={(e) => handleTimingChange(index, 'endTime', e.target.value)}
              className="border p-2 w-full rounded bg-yellow-50"
            />
          </div>
          {value.length > 1 && (
            <button
              type="button"
              onClick={() => removeTiming(index)}
              className="text-red-500 text-xl pb-1"
              title="Remove this time slot"
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
  );
};

export default TimeIntervalSetInput;
