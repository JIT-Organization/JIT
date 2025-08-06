"use client";
import Cookies from "js-cookie";

export const checkPermission = (permissionCode) => {
  try {
    const encoded = Cookies.get("permissions");
    if (!encoded) return false;

    const decoded = atob(encoded);
    const parsed = JSON.parse(decoded);

    if (!Array.isArray(parsed)) return false;

    const permissions = new Set(parsed);
    return permissions.has(permissionCode);
  } catch (err) {
    console.error("Permission check failed:", err);
    return false;
  }
};
