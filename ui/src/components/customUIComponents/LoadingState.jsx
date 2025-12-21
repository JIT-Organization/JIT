import { Loader2 } from "lucide-react";

export default function LoadingState({ message = "Loading..." }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
      <Loader2 className="h-8 w-8 animate-spin text-primary" aria-label="Loading spinner" />
      <p className="text-lg font-medium text-center">{message}</p>
    </div>
  );
}