import React from "react";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

function AddOnInput({ value = [], onChange }) {
  const [addOns, setAddOns] = useState(value);

  // Helper to update parent
  const updateAddOns = (newAddOns) => {
    setAddOns(newAddOns);
    onChange && onChange(newAddOns);
  };

  // Add new add-on
  const handleAddAddOn = () => {
    updateAddOns([
      ...addOns,
      { label: "", options: [], price: null },
    ]);
  };

  // Remove add-on
  const handleRemoveAddOn = (idx) => {
    const newAddOns = addOns.filter((_, i) => i !== idx);
    updateAddOns(newAddOns);
  };

  // Update add-on label
  const handleLabelChange = (idx, label) => {
    const newAddOns = [...addOns];
    newAddOns[idx].label = label;
    updateAddOns(newAddOns);
  };

  const handleToggleLayer = (idx) => {
    const newAddOns = [...addOns];
      newAddOns[idx].options = [{ name: "", price: "" }];
      newAddOns[idx].price = null;
    updateAddOns(newAddOns);
  };

  // Update price for single-layer
  const handlePriceChange = (idx, price) => {
    const newAddOns = [...addOns];
    newAddOns[idx].price = price;
    updateAddOns(newAddOns);
  };

  // Add sub-option
  const handleAddOption = (addOnIdx) => {
    const newAddOns = [...addOns];
    newAddOns[addOnIdx].options.push({ name: "", price: "" });
    updateAddOns(newAddOns);
  };

  // Remove sub-option
  const handleRemoveOption = (addOnIdx, optIdx) => {
    const newAddOns = [...addOns];
    newAddOns[addOnIdx].options = newAddOns[addOnIdx].options.filter((_, i) => i !== optIdx);
    updateAddOns(newAddOns);
  };

  // Update sub-option name/price
  const handleOptionChange = (addOnIdx, optIdx, field, val) => {
    const newAddOns = [...addOns];
    newAddOns[addOnIdx].options[optIdx][field] = val;
    updateAddOns(newAddOns);
  };

  return (
    <div className="grid grid-cols-12 gap-4">
      {addOns.map((addOn, idx) => (
        <div key={idx} className="col-span-12 md:col-span-6 border rounded p-4 mb-2 bg-yellow-50">
          <div className="flex gap-2 items-center mb-2">
            <Input
              className="w-1/3"
              placeholder="Label"
              value={addOn.label}
              onChange={e => handleLabelChange(idx, e.target.value)}
            />
            {(!addOn.options || addOn.options.length === 0) && (
              <Input
                className="w-1/3"
                placeholder="Price"
                type="number"
                value={addOn.price || ""}
                onChange={e => handlePriceChange(idx, e.target.value)}
              />
            )}
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => handleRemoveAddOn(idx)}
            >
              Remove
            </Button>
          </div>
          {(!addOn.options || addOn.options.length === 0) && (
            <Button
              type="button"
              variant={addOn.options && addOn.options.length > 0 ? "default" : "outline"}
              size="sm"
              onClick={() => handleToggleLayer(idx)}
            >
              Add Options
            </Button>
          )}
          {/* Two layer: options */}
          {addOn.options && addOn.options.length > 0 && (
            <div className="space-y-2">
              {addOn.options.map((opt, optIdx) => (
                <div key={optIdx} className="flex gap-2 items-center">
                  <Input
                    className="w-1/3"
                    placeholder="Option Name"
                    value={opt.name}
                    onChange={e => handleOptionChange(idx, optIdx, "name", e.target.value)}
                  />
                  <Input
                    className="w-1/3"
                    placeholder="Price"
                    type="number"
                    value={opt.price}
                    onChange={e => handleOptionChange(idx, optIdx, "price", e.target.value)}
                  />
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    onClick={() => handleRemoveOption(idx, optIdx)}
                  >
                    Remove
                  </Button>
                </div>
              ))}
              <Button
                type="button"
                variant="secondary"
                size="sm"
                onClick={() => handleAddOption(idx)}
              >
                Add Option
              </Button>
            </div>
          )}
        </div>
      ))}
      <div className="col-span-12 md:col-span-6">
        <Button type="button" onClick={handleAddAddOn} variant="default">
          Add Add-On
        </Button>
      </div>
    </div>
  );
}

export default AddOnInput; 