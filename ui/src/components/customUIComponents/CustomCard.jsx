"use client";

import * as React from "react";

const CustomCard = ({ title, value, data, icon, color = '', showBreakdown = false, className = '', onMouseEnter, onMouseLeave, onClick }) => {
  return (
    <div
      className={`group flex items-center min-w-[220px] h-28 bg-[#F8F5ED] border border-gray-300 rounded-lg shadow-sm px-6 py-4 gap-4 relative overflow-visible ${className}`}
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onClick={onClick}
    >
      {/* Icon */}
      <div className="flex items-center justify-center w-12 h-12 
      rounded-full bg-white border border-gray-200 mr-2">
        {icon}
      </div>
      {/* Content */}
      <div className="flex flex-col flex-1 justify-center">
        <p className="text-base font-medium text-gray-700 mb-1">{title}</p>
        <div className="flex items-center gap-1 mb-1">
          <span className={`text-2xl font-bold ${color}`}>{value}</span>
        </div>
      </div>
      {/* Hovered Breakdown to the right */}
      <div
        className={`
          h-auto w-0 overflow-hidden
          opacity-0 scale-x-0
          ${showBreakdown ? 'w-fit opacity-100 scale-x-100' : ''}
          origin-left transition-all duration-500 ease-in-out
          flex flex-col justify-center gap-1 px-3 py-3
        `}
      >
        {data.map((item, idx) => (
          <div
            key={idx}
            className="flex justify-between text-sm whitespace-nowrap w-full"
          >
            <p className="text-gray-700">{item.label}</p>
            <p className="text-gray-900 font-medium ml-4">{item.value}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CustomCard;



