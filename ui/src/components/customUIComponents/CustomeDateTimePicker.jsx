'use client';

import * as React from 'react';
import { format } from 'date-fns';
import { CalendarIcon, Clock } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

export function DateTimePicker({ value, onChange, className }) {
  const [open, setOpen] = React.useState(false);
  const initialDate = value ? new Date(value) : new Date();

  const [selectedDate, setSelectedDate] = React.useState(() => {
    const date = new Date(initialDate);
    return isNaN(date.getTime()) ? null : date;
  });

  const [hour, setHour] = React.useState(() =>
    selectedDate ? (selectedDate.getHours() % 12 || 12) : 12
  );
  const [minute, setMinute] = React.useState(() =>
    selectedDate ? selectedDate.getMinutes() : 0
  );
  const [ampm, setAmPm] = React.useState(() =>
    selectedDate ? (selectedDate.getHours() >= 12 ? 'PM' : 'AM') : 'AM'
  );

  const convertTo24Hour = (h, ampmVal) => {
    if (ampmVal === 'AM') return h === 12 ? 0 : h;
    return h === 12 ? 12 : h + 12;
  };

  React.useEffect(() => {
    if (!value) return;

    const newVal = typeof value === 'string' ? new Date(value) : value;
    if (isNaN(newVal.getTime())) return;

    setSelectedDate(new Date(newVal));
    const h = newVal.getHours();
    setHour(h % 12 || 12);
    setMinute(newVal.getMinutes());
    setAmPm(h >= 12 ? 'PM' : 'AM');
  }, [value]);

  React.useEffect(() => {
    if (!selectedDate) return;

    const updated = new Date(selectedDate);
    updated.setHours(convertTo24Hour(hour, ampm));
    updated.setMinutes(minute);
    updated.setSeconds(0);
    updated.setMilliseconds(0);

    if (!value || updated.getTime() !== new Date(value).getTime()) {
      onChange?.(updated);
    }
  }, [selectedDate, hour, minute, ampm]);

  const handleDateSelect = (date) => {
    if (date) {
      const newDate = new Date(date);
      newDate.setHours(convertTo24Hour(hour, ampm));
      newDate.setMinutes(minute);
      newDate.setSeconds(0);
      setSelectedDate(newDate);
    }
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            'justify-start text-left font-normal',
            !selectedDate && 'text-muted-foreground',
            className
          )}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          <span className="truncate overflow-hidden whitespace-nowrap block">
            {selectedDate
              ? format(selectedDate, 'PPP hh:mm a')
              : 'Pick date & time'}
          </span>
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-4 space-y-3 bg-white" align="start">
        <Calendar
          mode="single"
          selected={selectedDate}
          onSelect={handleDateSelect}
          initialFocus
        />

        <div className="flex items-center gap-2">
          <Clock className="h-4 w-4 text-muted-foreground" />

          <Select value={hour.toString()} onValueChange={(val) => setHour(Number(val))}>
            <SelectTrigger className="w-14">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {[...Array(12)].map((_, i) => {
                const h = (i + 1).toString().padStart(2, '0');
                return (
                  <SelectItem key={h} value={parseInt(h).toString()}>
                    {h}
                  </SelectItem>
                );
              })}
            </SelectContent>
          </Select>

          <span>:</span>

          <Select value={minute.toString()} onValueChange={(val) => setMinute(Number(val))}>
            <SelectTrigger className="w-14">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {[...Array(60)].map((_, i) => {
                const m = i.toString().padStart(2, '0');
                return (
                  <SelectItem key={m} value={i.toString()}>
                    {m}
                  </SelectItem>
                );
              })}
            </SelectContent>
          </Select>

          <Select value={ampm} onValueChange={(val) => setAmPm(val)}>
            <SelectTrigger className="w-16">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-white">
              <SelectItem value="AM">AM</SelectItem>
              <SelectItem value="PM">PM</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </PopoverContent>
    </Popover>
  );
}
