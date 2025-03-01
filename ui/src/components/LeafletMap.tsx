"use client";
import React from "react";


import { useEffect, useRef, useState } from "react";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

interface LeafletMapProps {
  onSelectLocation: (address: string) => void;
}

const LeafletMap = ({ onSelectLocation }: LeafletMapProps) => {
  const mapRef = useRef<L.Map | null>(null);
  const mapContainerRef = useRef<HTMLDivElement | null>(null);
  const markerRef = useRef<L.Marker | null>(null);

  const [position, setPosition] = useState<[number, number]>([12.9716, 77.5946]); // Default: Bangalore

  useEffect(() => {
    if (mapContainerRef.current && !mapRef.current) {
      mapRef.current = L.map(mapContainerRef.current).setView(position, 13);

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(mapRef.current);

      markerRef.current = L.marker(position).addTo(mapRef.current).bindPopup("Default Location: Bangalore").openPopup();

      mapRef.current.on("click", async (e: L.LeafletMouseEvent) => {
        const { lat, lng } = e.latlng;
        setPosition([lat, lng]);

        if (markerRef.current) {
          markerRef.current.setLatLng(e.latlng);
        } else {
          markerRef.current = L.marker(e.latlng).addTo(mapRef.current);
        }

        const address = await reverseGeocode(lat, lng);
        onSelectLocation(address);
      });
    }

    return () => {
      if (mapRef.current) {
        mapRef.current.off();
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, [onSelectLocation]);

  // Function to get the user's location
  const getUserLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(async (position) => {
        const { latitude, longitude } = position.coords;
        setPosition([latitude, longitude]);

        if (mapRef.current) {
          mapRef.current.setView([latitude, longitude], 13);
          if (markerRef.current) {
            markerRef.current.setLatLng([latitude, longitude]);
          } else {
            markerRef.current = L.marker([latitude, longitude]).addTo(mapRef.current);
          }
        }

        const address = await reverseGeocode(latitude, longitude);
        onSelectLocation(address);
      });
    }
  };

  return (
    <div className="flex flex-col items-center">
      <button onClick={getUserLocation} className="mb-2 p-2 bg-green-500 text-white rounded">
        Use My Location
      </button>
      <div ref={mapContainerRef} style={{ height: "350px", width: "500px", borderRadius: "10px", overflow: "hidden" }} />
    </div>
  );
};

// Function to get address from lat/lng
const reverseGeocode = async (lat: number, lng: number) => {
  try {
    const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`);
    const data = await response.json();
    return data.display_name || "Unknown Location";
  } catch (error) {
    console.error("Error fetching address:", error);
    return "Unknown Location";
  }
};

export default LeafletMap;
