import React from "react";
import Image from "next/image";
import Link from "next/link";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card";

const FoodCard = ({ id, image, name, rating, cost, timeTaken, tags = [] }) => {
  return (
    <Card className="w-72 overflow-hidden"> {/* Consistent Card Structure */}
      
      {/* Header Section with Image */}
      <CardHeader className="relative w-full h-36">
        <Image src={image} alt={name} layout="fill" objectFit="cover" className="rounded-t-xl" />
      </CardHeader>

      {/* Food Details Section */}
      <CardContent>
        <CardTitle className="text-lg">{name}</CardTitle>
        <CardDescription className="text-gray-500">⭐ {rating}/5 • ⏱️ {timeTaken}</CardDescription>
        
        <div className="mt-2 flex justify-between items-center">
          <span className="text-lg font-bold">${cost}</span>
        </div>

        {/* Tags (e.g., Chef Recommended, Spicy) */}
        {tags.length > 0 && (
          <div className="mt-2 flex flex-wrap gap-2">
            {tags.map((tag, index) => (
              <span key={index} className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded-lg">
                {tag}
              </span>
            ))}
          </div>
        )}
      </CardContent>

      {/* Footer Section */}
      <CardFooter className="justify-center">
        <Link href={`/menu-details/${id}`} className="text-blue-500 hover:underline text-sm">
          View Details
        </Link>
      </CardFooter>
    </Card>
  );
};

export default FoodCard;
