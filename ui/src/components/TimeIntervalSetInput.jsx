'use client';

import { cn } from '@/lib/utils';
import React from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

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
            <label htmlFor={`start-${index}`} className="text-sm block mb-1">
              Available From
            </label>
            <Input
              id={`start-${index}`}
              type="time"
              value={slot.startTime}
              onChange={(e) => handleTimingChange(index, 'startTime', e.target.value)}
            />
          </div>
          <div className="flex-1">
            <label htmlFor={`end-${index}`} className="text-sm block mb-1">
              Available To
            </label>
            <Input
              id={`end-${index}`}
              type="time"
              value={slot.endTime}
              onChange={(e) => handleTimingChange(index, 'endTime', e.target.value)}
            />
          </div>
          {value.length > 1 && (
            <Button
              type="button"
              onClick={() => removeTiming(index)}
              variant="ghost"
              size="icon"
              className="button-remove text-xl pb-1"
              title="Remove this time slot"
            >
              &times;
            </Button>
          )}
        </div>
      ))}

      <Button
        type="button"
        onClick={addTiming}
        className="mt-2 px-3 py-1 bg-orange-500 hover:bg-orange-600 text-white rounded-full"
      >
        +
      </Button>
    </div>
  );
};

export default TimeIntervalSetInput;
