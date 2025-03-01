import React, { useState, useEffect, useRef } from "react";
import { cn } from "@/lib/utils";

// Helper function to format time in HH:mm:ss A format
const formatTime = (hours, minutes, seconds) => {
  const period = hours >= 12 ? "PM" : "AM";
  const formattedHours = hours % 12 || 12; // Convert to 12-hour format
  const formattedMinutes = minutes.toString().padStart(2, "0");
  const formattedSeconds = seconds.toString().padStart(2, "0");
  return `${formattedHours}:${formattedMinutes}:${formattedSeconds} ${period}`;
};

// Main TimePicker component as a Dropdown
const TimePicker = ({ className, value, onChange, showSeconds = true }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [selectedTime, setSelectedTime] = useState(value);
  const [hours, setHours] = useState(0);
  const [minutes, setMinutes] = useState(0);
  const [seconds, setSeconds] = useState(0)
  const [isInitialTime, setIsInitialTime] = useState(true)

  // Ref for the dropdown to detect clicks outside
  const dropdownRef = useRef(null);

  // Helper function to handle time selection
  const handleTimeSelect = () => {
    if(!isInitialTime) {
    const time = formatTime(hours, minutes, seconds);
    setSelectedTime(time);
    onChange(time);
    }setIsDropdownOpen(false);
  };
  const handleTimeChange = (type, value) => {
    // Validate and update the appropriate state based on the time type (hours, minutes, or seconds)
    if (type === "hours") {
      const newHours = Math.max(0, Math.min(23, value));
      setHours(newHours);
      if (isInitialTime) setIsInitialTime(false);
    } else if (type === "minutes") {
      const newMinutes = Math.max(0, Math.min(59, value));
      setMinutes(newMinutes);
      if (isInitialTime) setIsInitialTime(false);
    } else if (type === "seconds") {
      const newSeconds = Math.max(0, Math.min(59, value));
      setSeconds(newSeconds);
      if (isInitialTime) setIsInitialTime(false);
    }
    // Call onChange with the updated time (if needed)
    onChange && onChange(hours, minutes, seconds);
  };

  // Close the dropdown when clicking outside of it
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        handleTimeSelect(); // Save the time when clicking outside
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [hours, minutes, seconds]);

  return (
    <div className={cn("relative p-3", className)}>
      <div className="flex items-center space-x-2">
        <input
          type="text"
          value={selectedTime}
          readOnly
          className="w-full p-2 border rounded-md cursor-pointer"
          onClick={() => setIsDropdownOpen(!isDropdownOpen)} // Toggle dropdown
        />
        <button
          onClick={() => setIsDropdownOpen(!isDropdownOpen)}
          className="p-2 bg-gray-300 rounded-md"
        >
          🕒
        </button>
      </div>

      {/* Dropdown */}
      {isDropdownOpen && (
        <div
          ref={dropdownRef}
          className="absolute top-13 left-0  bg-white border rounded-md shadow-lg z-10"
        >
          <div className="p-4 flex">
      <div className="flex justify-between">
        <select
          value={hours}
          onChange={(e) => handleTimeChange("hours", parseInt(e.target.value))}
          className="w-16 p-2 border rounded"
        >
          {[...Array(24).keys()].map((h) => (
            <option key={h} value={h}>
              {h < 10 ? `0${h}` : h}
            </option>
          ))}
        </select>
      </div>
      <div className="flex justify-between">
        <select
          value={minutes}
          onChange={(e) => handleTimeChange("minutes", parseInt(e.target.value))}
          className="w-16 p-2 border rounded"
        >
          {[...Array(60).keys()].map((m) => (
            <option key={m} value={m}>
              {m < 10 ? `0${m}` : m}
            </option>
          ))}
        </select>
      </div>
      {showSeconds && (
        <div className="flex justify-between">
          <select
            value={seconds}
            onChange={(e) => handleTimeChange("seconds", parseInt(e.target.value))}
            className="w-16 p-2 border rounded"
          >
            {[...Array(60).keys()].map((s) => (
              <option key={s} value={s}>
                {s < 10 ? `0${s}` : s}
              </option>
            ))}
          </select>
        </div>
      )}
      <div className="mt-2">
        {/* Displaying selected time */}
        <span>{formatTime(hours, minutes, seconds)}</span>
      </div>
    </div>

        </div>
      )}
    </div>
  );
};

TimePicker.displayName = "TimePicker";

export { TimePicker };
