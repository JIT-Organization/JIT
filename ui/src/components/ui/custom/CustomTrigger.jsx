import { useSidebar } from "@/components/ui/sidebar"; // Assuming you have a custom hook to toggle the sidebar

export function CustomTrigger() {
  const { toggleSidebar } = useSidebar(); // This hook gives access to the toggle function

  return (
    <button onClick={toggleSidebar} className="text-xl" aria-label="Toggle sidebar">
      {"☰"}
    </button>
  );
}
