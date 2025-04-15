"use client";

import * as React from "react";
import { format } from "date-fns";
import { CalendarIcon, Clock } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Input } from "@/components/ui/input";

export function DateTimePicker({ value, onChange }) {
  const [open, setOpen] = React.useState(false);
  const [selectedDate, setSelectedDate] = React.useState(value || new Date());
  const [time, setTime] = React.useState(format(selectedDate, "HH:mm"));

  React.useEffect(() => {
    const [hours, minutes] = time.split(":").map(Number);
    const updatedDate = new Date(selectedDate);
    updatedDate.setHours(hours);
    updatedDate.setMinutes(minutes);
    onChange?.(updatedDate);
  }, [selectedDate, time]);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            "w-[280px] justify-start text-left font-normal",
            !selectedDate && "text-muted-foreground"
          )}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {selectedDate ? format(selectedDate, "PPP p") : <span>Pick date & time</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-4 space-y-3" align="start">
        <Calendar
          mode="single"
          selected={selectedDate}
          onSelect={(date) => {
            if (date) setSelectedDate(date);
          }}
          initialFocus
        />
        <div className="flex items-center gap-2">
          <Clock className="h-4 w-4 text-muted-foreground" />
          <Input
            type="time"
            value={time}
            onChange={(e) => setTime(e.target.value)}
            className="w-[140px]"
          />
        </div>
      </PopoverContent>
    </Popover>
  );
}
