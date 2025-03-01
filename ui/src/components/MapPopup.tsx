"use client";


import React from "react";
import { useState, useRef, useEffect } from "react";
import dynamic from "next/dynamic";
import Modal from "react-modal";
import { FaMapMarkerAlt } from "react-icons/fa";

Modal.setAppElement(typeof document !== "undefined" ? document.body : "");

interface MapPopupProps {
  value: string;
  onChange: (address: string) => void;
}

const MapPopup = ({ value, onChange }: MapPopupProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const modalRef = useRef<HTMLDivElement | null>(null);

  const [manualAddress, setManualAddress] = useState(value);

  
  const LeafletMap = dynamic(() => import("./LeafletMap"), { ssr: false });

  
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (modalRef.current && !modalRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [isOpen]);

  return (
    <div className="relative w-full">
      
      <input
        type="text"
        value={manualAddress}
        onChange={(e) => {
          setManualAddress(e.target.value);
          onChange(e.target.value); 
        }}
        placeholder="Enter your address or select from the map"
        className="w-full pl-3 pr-10 rounded-lg border-2 border-solid border-black p-3 text-md"
      />


      <FaMapMarkerAlt
        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-red-500 text-xl cursor-pointer"
        onClick={() => setIsOpen(true)}
      />

      <Modal
        isOpen={isOpen}
        onRequestClose={() => setIsOpen(false)}
        className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
        style={{ overlay: { zIndex: 1000 } }}
      >
        <div ref={modalRef} className="bg-white p-4 rounded-lg shadow-lg relative w-auto">
          <button onClick={() => setIsOpen(false)} className="absolute top-2 right-2 bg-red-500 text-white p-1 rounded">
            Close
          </button>

          
          <LeafletMap onSelectLocation={(address) => {
            setManualAddress(address); 
            onChange(address);
            setIsOpen(false); 
          }} />
        </div>
      </Modal>
    </div>
  );
};

export default MapPopup;

