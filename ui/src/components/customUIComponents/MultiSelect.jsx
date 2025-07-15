import { Popover, PopoverTrigger, PopoverContent } from "../ui/popover";
import { Command, CommandInput, CommandList, CommandItem } from "../ui/command";
import { Check } from "lucide-react";
import { Button } from "../ui/button";
import { cn } from "@/lib/utils";

export default function MultiSelect({
  options,
  value,
  onChange,
  placeholder = "Select options",
  className = '',
  disabled
}) {
  const toggleOption = (optionValue) => {
    if (value.includes(optionValue)) {
      onChange(value.filter((val) => val !== optionValue));
    } else {
      onChange([...value, optionValue]);
    }
  };

  return (
    <Popover>
      <PopoverTrigger asChild disabled={disabled}>
        <Button
          variant="outline"
          className={cn(
            "w-full justify-start text-left",
            className
          )}
        >
          <span className={value.length ? "truncate" : "text-muted-foreground"}>
            {value.length
              ? options
                  .filter((opt) => value.includes(opt.value))
                  .map((opt) => opt.label)
                  .join(", ")
              : placeholder}
          </span>
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-full p-0">
        <Command>
          <CommandInput placeholder="Search options..." />
          <CommandList>
            {options.map((option) =>
              option.value ? (
                <CommandItem
                  key={option.value}
                  onSelect={() => toggleOption(option.value)}
                  className="cursor-pointer"
                >
                  <span className="flex-1">{option.label}</span>
                  {value.includes(option.value) && (
                    <Check className="ml-auto h-4 w-4" />
                  )}
                </CommandItem>
              ) : (
                <CommandItem key={option.label}>{option.label}</CommandItem>
              )
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
