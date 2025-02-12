import React from "react";
import Image from "next/image";
import Link from "next/link";

const FoodCard = ({
  id,
  image,
  name,
  rating,
  cost,
  timeTaken,
  tags = [], // Optional array for additional details
  onClick,
}) => {
  return (
    <div
      className="border rounded-lg shadow-md p-3 bg-white hover:shadow-lg transition-shadow cursor-pointer"
      onClick={onClick}
    >
      {/* Image Section */}
      <div className="relative w-full h-36">
        <Image
          src={image}
          alt={name}
          layout="fill"
          objectFit="cover"
          className="rounded-t-md"
          priority
        />
      </div>

      {/* Content Section */}
      <div className="p-3">
        <h3 className="text-lg font-semibold truncate">{name}</h3>
        <div className="flex justify-between items-center mt-2">
          <span className="text-sm text-gray-600">⭐ {rating}/5</span>
          <span className="text-sm font-bold">${cost}</span>
        </div>
        <div className="text-xs text-gray-500 mt-1">⏱️ {timeTaken}</div>
        {tags.length > 0 && (
          <div className="mt-2 flex flex-wrap gap-1">
            {tags.map((tag, index) => (
              <span
                key={index}
                className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded"
              >
                {tag}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Details Button */}
      <div className="mt-4 text-center">
        <Link href={`/menu-details/${id}`}>
          <span className="text-blue-500 hover:underline text-sm">View Details</span>
        </Link>
      </div>
    </div>
  );
};

export default FoodCard;
