import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import MultiSelect from "@/components/customUIComponents/MultiSelect";
import { data as defaultAddOnList } from "@/app/(main)/restaurants/add-on/data";

// Single Add-On input (label, price, options)
function AddOnSingleInput({ value, onChange, onRemove, showRemove = true, readOnly = false, highlight = true }) {
  // Update label
  const handleLabelChange = (label) => {
    if (!readOnly) onChange({ ...value, label });
  };

  // Toggle to options mode
  const handleToggleLayer = () => {
    if (!readOnly) onChange({ ...value, options: [{ name: "", price: "" }], price: null });
  };

  // Update price for single-layer
  const handlePriceChange = (price) => {
    if (!readOnly) onChange({ ...value, price });
  };

  // Add sub-option
  const handleAddOption = () => {
    if (!readOnly) onChange({
      ...value,
      options: [...(value.options || []), { name: "", price: "" }],
      price: null,
    });
  };

  // Remove sub-option
  const handleRemoveOption = (optIdx) => {
    if (!readOnly) onChange({
      ...value,
      options: value.options.filter((_, i) => i !== optIdx),
    });
  };

  // Update sub-option name/price
  const handleOptionChange = (optIdx, field, val) => {
    if (!readOnly) {
      const newOptions = [...value.options];
      newOptions[optIdx][field] = val;
      onChange({ ...value, options: newOptions });
    }
  };

  return (
    <div className={`border rounded p-4 mb-2 ${highlight ? "bg-yellow-50" : ""}`}> 
      <div className="flex gap-2 items-center mb-2">
        <Input
          className="w-1/3"
          placeholder="Label"
          value={value.label}
          onChange={e => handleLabelChange(e.target.value)}
          disabled={readOnly}
        />
        {(!value.options || value.options.length === 0) && (
          <Input
            className="w-1/3"
            placeholder="Price"
            type="number"
            value={value.price || ""}
            onChange={e => handlePriceChange(e.target.value)}
            disabled={readOnly}
          />
        )}
        {showRemove && !readOnly && (
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={onRemove}
          >
            Remove
          </Button>
        )}
      </div>
      {(!value.options || value.options.length === 0) && !readOnly && (
        <Button
          type="button"
          variant={value.options && value.options.length > 0 ? "default" : "outline"}
          size="sm"
          onClick={handleToggleLayer}
        >
          Add Options
        </Button>
      )}
      {/* Two layer: options */}
      {value.options && value.options.length > 0 && (
        <div className="space-y-2">
          {value.options.map((opt, optIdx) => (
            <div key={optIdx} className="flex gap-2 items-center">
              <Input
                className="w-1/3"
                placeholder="Option Name"
                value={opt.name}
                onChange={e => handleOptionChange(optIdx, "name", e.target.value)}
                disabled={readOnly}
              />
              <Input
                className="w-1/3"
                placeholder="Price"
                type="number"
                value={opt.price}
                onChange={e => handleOptionChange(optIdx, "price", e.target.value)}
                disabled={readOnly}
              />
              {!readOnly && (
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => handleRemoveOption(optIdx)}
                >
                  Remove
                </Button>
              )}
            </div>
          ))}
          {!readOnly && (
            <Button
              type="button"
              variant="secondary"
              size="sm"
              onClick={handleAddOption}
            >
              Add Option
            </Button>
          )}
        </div>
      )}
    </div>
  );
}

// Array manager for add-ons, now supports selection from a list and adding new
function AddOnInput({ value = [], onChange, availableAddOns }) {
  // Use default data if availableAddOns is not provided
  const addOnList = availableAddOns || defaultAddOnList;
  // Derive initial state from value prop
  const [selectedAddOnLabels, setSelectedAddOnLabels] = useState([]);
  const [newAddOns, setNewAddOns] = useState([]);

  // Sync internal state with value prop when value changes from outside
  useEffect(() => {
    const labels = [];
    const customs = [];
    value.forEach(addon => {
      if (addon.label && addOnList.some(a => a.label === addon.label)) {
        labels.push(addon.label);
      } else {
        customs.push(addon);
      }
    });
    setSelectedAddOnLabels(labels);
    setNewAddOns(customs);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(value), JSON.stringify(addOnList)]);

  // When user changes selection or new add-ons, call onChange
  useEffect(() => {
    const selectedAddOns = addOnList.filter(addon =>
      selectedAddOnLabels.includes(addon.label)
    );
    const combined = [...selectedAddOns, ...newAddOns];
    if (JSON.stringify(combined) !== JSON.stringify(value)) {
      onChange(combined);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedAddOnLabels, newAddOns]);

  return (
    <div className="space-y-4">
      <MultiSelect
        options={addOnList.map(addon => ({
          value: addon.label,
          label: addon.label,
        }))}
        value={selectedAddOnLabels}
        onChange={setSelectedAddOnLabels}
        placeholder="Select add-ons"
      />
      <div className="grid grid-cols-12 gap-4">
        {/* Show selected add-ons as read-only */}
        {addOnList.filter(addon => selectedAddOnLabels.includes(addon.label)).map((addOn, idx) => (
          <div key={"selected-" + addOn.label} className="col-span-12 md:col-span-6">
            <AddOnSingleInput
              value={addOn}
              readOnly={true}
              showRemove={false}
              highlight={true}
            />
          </div>
        ))}
        {/* Show new add-ons as editable */}
        {newAddOns.map((addOn, idx) => (
          <div key={"new-" + idx} className="col-span-12 md:col-span-6">
            <AddOnSingleInput
              value={addOn}
              onChange={newAddOn => {
                const updated = [...newAddOns];
                updated[idx] = newAddOn;
                setNewAddOns(updated);
              }}
              onRemove={() => setNewAddOns(newAddOns.filter((_, i) => i !== idx))}
              showRemove={true}
              readOnly={false}
              highlight={true}
            />
          </div>
        ))}
        <div className="col-span-12 md:col-span-6">
          <Button type="button" onClick={() => setNewAddOns([...newAddOns, { label: "", options: [], price: null }])} variant="default">
            Add New Add-On
          </Button>
        </div>
      </div>
    </div>
  );
}

export { AddOnSingleInput };
export default AddOnInput; 