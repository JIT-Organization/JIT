import React, { useMemo } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import { useQuery } from "@tanstack/react-query";
import { getMenuItemListOptions } from "@/lib/api/api";
import { getSelectOptions } from "@/lib/utils/helper";

const ComboItemsInput = ({ value = [], onChange, className }) => {
  const { data: menuItemsList } = useQuery(getMenuItemListOptions());
  const selectOptions = useMemo(() => getSelectOptions(menuItemsList || []), [menuItemsList]);

  const handleChange = (index, field, val) => {
    const updated = [...value];
    updated[index] = { ...updated[index], [field]: val };
    onChange(updated);
  };

  const addRow = () => {
    onChange([...value, { item: "", qty: 1 }]);
  };

  const removeRow = (index) => {
    const updated = value.filter((_, i) => i !== index);
    onChange(updated);
  };

  return (
    <div className={"bg-gray-100 p-4 rounded " + (className || "") }>
      <div className="flex gap-4 mb-2 font-semibold">
        <div className="flex-1 text-sm">Item</div>
        <div className="w-24 text-sm">Qty</div>
        <div className="w-8" />
      </div>
      {value.map((row, index) => (
        <div key={index} className="flex gap-4 mb-2 items-end">
          <div className="flex-1">
            <MultiSelect
              options={selectOptions}
              value={row.item ? [row.item] : []}
              onChange={arr => handleChange(index, "item", arr[0] || "")}
              placeholder="Select item"
              className="input"
              isSingleSelect={true}
            />
          </div>
          <div className="w-24">
            <Input
              type="number"
              min={1}
              value={row.qty}
              onChange={e => handleChange(index, "qty", e.target.value.replace(/[^\d]/g, ""))}
              className="bg-yellow-50"
            />
          </div>
          {value.length > 1 && (
            <Button
              type="button"
              onClick={() => removeRow(index)}
              variant="ghost"
              size="icon"
              className="button-remove text-xl pb-1"
              title="Remove this item"
            >
              &times;
            </Button>
          )}
        </div>
      ))}
      <Button
        type="button"
        onClick={addRow}
        className="mt-2 px-3 py-1 bg-orange-500 hover:bg-orange-600 text-white rounded-full"
      >
        +
      </Button>
    </div>
  );
};

export default ComboItemsInput; 