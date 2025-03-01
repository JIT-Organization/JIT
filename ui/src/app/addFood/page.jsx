"use client";
import React, { useState } from "react";
import { TimePicker } from "@/components/ui/timePicker"; // Assuming TimePicker is in the same directory
import { FaArrowLeft, FaEdit, FaTrash } from "react-icons/fa";
import {
    DropdownMenu,
    DropdownMenuTrigger,
    DropdownMenuContent,
    DropdownMenuItem,
} from "@/components/ui/dropdown-menu";



export default function AddFood() {
    const [foodName, setFoodName] = useState("");
    const [time, setTime] = useState("");
    const [count, setCount] = useState("");
    const [selectedCook, setSelectedCook] = useState("Select Cook");


    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle form submission, like sending the data to the server
        console.log("Form submitted with:", { foodName, time });
    };


    return (

        <div>
            {/* Header with back arrow, title, and edit/delete buttons */}
            <div className="flex justify-between items-center p-4 border-b">
                <button className="text-xl text-gray-500">
                    <FaArrowLeft /> {/* Back arrow icon */}
                </button>
                <h1 className="text-center text-xl font-semibold">Add Food</h1>
                <div className="flex space-x-4">
                    <button className="text-gray-500">
                        Delete {/* Edit button */}
                    </button>
                    <button className="text-gray-500">
                        Submit {/* Delete button */}
                    </button>
                </div>
            </div>
            <form onSubmit={handleSubmit} className="space-y-4 margin-style">
                <div className="form-group">
                    <label htmlFor="foodDescription" className="block text-sm font-medium">
                        Description
                    </label>
                    <textarea
                        id="foodDescription"
                        name="foodDescription"
                        value={foodName}
                        onChange={(e) => setFoodName(e.target.value)}
                        className="mt-1 block w-full p-2 border border-gray-300 rounded-md resize-none form-label-width1"
                        placeholder="Enter Description"
                        rows="4" // Adjust height (4 lines by default)
                    />
                </div>
                <div className="form-group">
                    {/* Responsible Cooks Dropdown */}
                    <label className="block text-sm font-medium">Responsible Cooks</label>
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <input
                                type="text"
                                value={selectedCook}
                                readOnly
                                className="mt-1 p-2 border rounded-md cursor-pointer"
                            />
                        </DropdownMenuTrigger>
                        <DropdownMenuContent className="w-48">
                            <DropdownMenuItem onSelect={() => setSelectedCook("Cook 1")}>
                                Cook 1
                            </DropdownMenuItem>
                            <DropdownMenuItem onSelect={() => setSelectedCook("Cook 2")}>
                                Cook 2
                            </DropdownMenuItem>
                            <DropdownMenuItem onSelect={() => setSelectedCook("Cook 3")}>
                                Cook 3
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>

                    {/* Count Input */}
                    <label className="block text-sm font-medium mt-4">Count</label>
                    <input
                        type="number"
                        value={count}
                        onChange={(e) => setCount(e.target.value)}
                        className="mt-1 p-2 border rounded-md"
                    />
                </div>

                <div className="display-flex">
                    <div className="form-group">
                        <label htmlFor="foodTime" className="block text-sm font-medium form-label-width2">
                            Available From  <TimePicker value={time} onChange={setTime} />
                        </label>
                    </div>
                    <div className="form-group">
                        <label htmlFor="foodTime" className="block text-sm font-medium form-label-width2">
                            Available To  <TimePicker value={time} onChange={setTime} />
                        </label>
                    </div>
                </div>
            </form>
        </div>
    );
}
