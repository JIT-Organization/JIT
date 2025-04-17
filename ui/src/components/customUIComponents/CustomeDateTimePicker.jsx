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

export function DateTimePicker({ value, onChange, className }) {
  const [open, setOpen] = React.useState(false);
  const [selectedDate, setSelectedDate] = React.useState(value || new Date());
  const [hour, setHour] = React.useState(12);
  const [minute, setMinute] = React.useState(0);
  const [ampm, setAmPm] = React.useState("AM");

  const convertTo24Hour = (h, ampmVal) => {
    if (ampmVal === "AM") return h === 12 ? 0 : h;
    return h === 12 ? 12 : h + 12;
  };

   React.useEffect(() => {
    if (!value) return;

    const incomingTime = value.getTime();
    const currentTime = new Date(selectedDate);
    currentTime.setHours(convertTo24Hour(hour, ampm));
    currentTime.setMinutes(minute);

    if (incomingTime !== currentTime.getTime()) {
      setSelectedDate(value);
      const h = value.getHours();
      setHour(h % 12 || 12);
      setMinute(value.getMinutes());
      setAmPm(h >= 12 ? "PM" : "AM");
    }
  }, [value, hour, minute, ampm]);

   React.useEffect(() => {
    const newDate = new Date(selectedDate);
    newDate.setHours(convertTo24Hour(hour, ampm));
    newDate.setMinutes(minute);
    newDate.setSeconds(0);

    if (newDate.getTime() !== selectedDate.getTime()) {
      onChange?.(newDate);
    }
  }, [selectedDate, hour, minute, ampm, onChange]);

  const handleDateSelect = (date) => {
    if (date) {
      const newDate = new Date(date);
      newDate.setHours(convertTo24Hour(hour, ampm));
      newDate.setMinutes(minute);
      setSelectedDate(newDate);
    }
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            "justify-start text-left font-normal",
            !selectedDate && "text-muted-foreground",
            className 
          )}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          <span className="truncate overflow-hidden whitespace-nowrap block">
          {selectedDate ? format(selectedDate, "PPP hh:mm a") : <span>Pick date & time</span>}
          </span>
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-4 space-y-3" align="start">
        <Calendar
          mode="single"
          selected={selectedDate}
          onSelect={handleDateSelect}
          initialFocus
        />

        <div className="flex items-center gap-2">
          <Clock className="h-4 w-4 text-muted-foreground" />

          <select
            value={hour}
            onChange={(e) => setHour(parseInt(e.target.value))}
            className="border rounded px-2 py-1"
          >
            {[...Array(12)].map((_, i) => {
              const h = i + 1;
              return (
                <option key={h} value={h}>
                  {h.toString().padStart(2, "0")}
                </option>
              );
            })}
          </select>

          <span>:</span>

          <select
            value={minute}
            onChange={(e) => setMinute(parseInt(e.target.value))}
            className="border rounded px-2 py-1"
          >
            {[...Array(60)].map((_, i) => (
              <option key={i} value={i}>
                {i.toString().padStart(2, "0")}
              </option>
            ))}
          </select>

          <select
            value={ampm}
            onChange={(e) => setAmPm(e.target.value)}
            className="border rounded px-2 py-1"
          >
            <option value="AM">AM</option>
            <option value="PM">PM</option>
          </select>
        </div>
      </PopoverContent>
    </Popover>
  );
}
