"use client";

import React, { createContext, useContext, useState } from "react";
import { adjustBrightness, blackOrWhiteTextColor, getLuminance } from "@/lib/utils/colorUtils";

const ColorContext = createContext();

export const ColorProvider = ({ children }) => {
  // Default: blue
  const [primaryColor, setPrimaryColor] = useState("#233a48"); // blue

  React.useEffect(() => {
    // Primary color variables
    document.documentElement.style.setProperty("--primary-color", primaryColor);
    document.documentElement.style.setProperty("--primary-text-color", blackOrWhiteTextColor(primaryColor));
    document.documentElement.style.setProperty("--sidebar-color", primaryColor);
    document.documentElement.style.setProperty("--sidebar-text-color", blackOrWhiteTextColor(primaryColor));
    // Sidebar active tab: color variant of primary
    const lum = getLuminance(primaryColor);
    let sidebarActiveBg;
    if (lum > 0.8) {
      sidebarActiveBg = adjustBrightness(primaryColor, -0.2); // Brighter primary, darken a bit
    } else {
      sidebarActiveBg = adjustBrightness(primaryColor, 0.4); // Lighter version
    }
    document.documentElement.style.setProperty("--sidebar-active-bg", sidebarActiveBg);
    document.documentElement.style.setProperty("--sidebar-active-text", blackOrWhiteTextColor(sidebarActiveBg));
    // Button and input use primary color
    const buttonLum = getLuminance(primaryColor);
    let buttonColor;
    if (buttonLum > 0.95) {
      buttonColor = 'black'; 
    } else if (buttonLum > 0.8) {
      buttonColor = adjustBrightness(primaryColor, -1); 
    } else if (buttonLum > 0.3) {
      buttonColor = adjustBrightness(primaryColor, -0.3); 
    } else {
      buttonColor = primaryColor;
    }

    document.documentElement.style.setProperty("--button-bg", buttonColor);
    document.documentElement.style.setProperty("--button-text", blackOrWhiteTextColor(buttonColor));
    // Input background: very light version of primary or white if luminance > 0.8
    let inputBg;
    if (lum > 0.8) {
      inputBg = "#ffffff";
    } else {
      inputBg = adjustBrightness(primaryColor, 0.9); // Lighter version of primary
    }
    document.documentElement.style.setProperty("--input-bg", inputBg);
    document.documentElement.style.setProperty("--input-text-color", blackOrWhiteTextColor(inputBg));
  }, [primaryColor]);

  return (
    <ColorContext.Provider value={{ primaryColor, setPrimaryColor }}>
      {children}
    </ColorContext.Provider>
  );
};

export const useColor = () => useContext(ColorContext); 