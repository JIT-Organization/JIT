import { AlertCircle } from "lucide-react";

export default function ErrorState({ title, message, action }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
      <AlertCircle className="h-8 w-8 text-destructive" />
      <div className="text-center space-y-2">
        <p className="text-lg font-medium text-destructive">{title}</p>
        <p className="text-sm text-muted-foreground">{message}</p>
      </div>
      {action && <div className="mt-4">{action}</div>}
    </div>
  );
} 