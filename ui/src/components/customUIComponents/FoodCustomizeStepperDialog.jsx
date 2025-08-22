import React, { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import FoodCard from "./FoodCard";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const FoodCustomizeStepperDialog = ({ isOpen, onSave, items = [], onClose }) => {
  const [current, setCurrent] = useState(0);
  const [customizedItems, setCustomizedItems] = useState(items);

  useEffect(() => {
    setCustomizedItems(items);
    setCurrent(0);
  }, [items, isOpen]);

  if (!isOpen || !items.length) return null;

  const food = customizedItems[current];
  if (!food) return null;
  const noteValue = food.customNotes || "";

  const handleNoteChange = (value) => {
    setCustomizedItems((prev) =>
      prev.map((item, idx) =>
        idx === current ? { ...item, customNotes: value } : item
      )
    );
  };

  const handleAddOnChange = (addOnLabel, value) => {
    setCustomizedItems((prev) =>
      prev.map((item, idx) => {
        if (idx !== current) return item;
        let selectedAddOns = item.selectedAddOns || [];
        // Remove any previous selection for this addOn
        selectedAddOns = selectedAddOns.filter((a) => a.label !== addOnLabel);
        if (value !== false && value !== "") {
          selectedAddOns.push({ label: addOnLabel, selected: value });
        }
        return { ...item, selectedAddOns };
      })
    );
  };

  const handleNext = () => setCurrent((c) => Math.min(c + 1, items.length - 1));
  const handlePrev = () => setCurrent((c) => Math.max(c - 1, 0));
  const handleSave = () => onSave(customizedItems);

  // Calculate total price for the current food item
  function getTotalPrice(food) {
    let total = Number(food.offerPrice ||food.price) || 0;
    if (Array.isArray(food.selectedAddOns)) {
      for (const addOn of food.selectedAddOns) {
        const addOnData = (food.addOns || []).find(a => a.label === addOn.label);
        if (addOnData) {
          if (!addOnData.options || addOnData.options.length === 0) {
            total += Number(addOnData.price) || 0;
          } else {
            const opt = addOnData.options.find(o => o.name === addOn.selected);
            if (opt) total += Number(opt.price) || 0;
          }
        }
      }
    }
    return total;
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl flex flex-col" style={{ maxHeight: '90vh' }}>
        <div className="flex flex-row flex-1">
          <div className="w-1/2 pr-4 flex flex-col items-center justify-start gap-4">
            <div className="w-full flex flex-row items-center justify-between mb-2">
              <span className="text-lg font-bold">{food.itemName}</span>
              <span className="flex items-center">
                <span className="text-base font-semibold text-primary">₹{getTotalPrice(food)}</span>
                <span className="bg-gray-100 text-xs rounded ml-2">per qty</span>
              </span>
            </div>
            <Card className="w-full mb-2">
              <CardContent className="p-4 pt-2">
                <FoodCard food={food} mode="create" />
                {food.foodType === "Combo" && Array.isArray(food.comboItems) && food.comboItems.length > 0 && (
                  <Card className="w-full my-2">
                    <CardHeader className="p-2 pb-0">
                      <CardTitle className="text-base font-semibold">Combo Items</CardTitle>
                    </CardHeader>
                    <CardContent className="p-2 pt-0">
                      <div className="flex flex-col gap-1">
                        <div className="flex font-semibold text-sm mb-1">
                          <div className="flex-1">Item</div>
                          <div className="w-16 text-right">Qty</div>
                        </div>
                        {food.comboItems.map((ci, idx) => (
                          <div key={idx} className="flex text-sm items-center border-b last:border-b-0 py-1">
                            <div className="flex-1">{ci.item}</div>
                            <div className="w-16 text-right">{ci.qty}</div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                )}
              </CardContent>
            </Card>
          </div>
          <div className="w-px bg-gray-200 h-auto" style={{ minHeight: '300px' }} />
          <div className="w-1/2 pl-4 flex flex-col justify-between max-h-[90vh]">
            <div className="overflow-y-auto" style={{ maxHeight: 'calc(90vh - 80px)' }}>
              <DialogTitle>Customize</DialogTitle>
              <form className="flex flex-col gap-4 mt-4">
                <div className="flex flex-col gap-1">
                  <Label>Notes</Label>
                  <Input
                    type="text"
                    placeholder="Add any special instructions..."
                    value={noteValue}
                    onChange={(e) => handleNoteChange(e.target.value)}
                  />
                </div>
                {/* Add-On Selection */}
                {Array.isArray(food.addOns) && food.addOns.length > 0 && (
                  <div className="mb-6">
                    <h3 className="text-lg font-semibold mb-4">Add-Ons</h3>
                    <div className="flex flex-col gap-4">
                      {food.addOns.map((addOn, idx) => {
                        const hasOptions = addOn.options && addOn.options.length > 0;
                        return (
                          <Card key={addOn.label} className="w-full">
                            {hasOptions && (
                              <CardHeader className="p-2">
                                <CardTitle className="text-base font-bold">{addOn.label}</CardTitle>
                              </CardHeader>
                            )}
                            <CardContent className="pt-0 p-2">
                              {(!hasOptions) ? (
                                <div className="flex items-center gap-2 pl-2">
                                  <Checkbox
                                    id={`addon-${addOn.label}`}
                                    checked={!!((food.selectedAddOns || []).find(a => a.label === addOn.label)?.selected)}
                                    onCheckedChange={checked => handleAddOnChange(addOn.label, checked ? true : false)}
                                  />
                                  <span className="text-sm">{addOn.label} {addOn.price ? `(+₹${addOn.price})` : null}</span>
                                </div>
                              ) : (
                                <RadioGroup
                                  value={((food.selectedAddOns || []).find(a => a.label === addOn.label)?.selected) || ""}
                                  onValueChange={val => handleAddOnChange(addOn.label, val)}
                                  className="flex flex-col gap-2 pl-2"
                                >
                                  {addOn.options.map(opt => (
                                    <div key={opt.name} className="flex items-center gap-2">
                                      <RadioGroupItem
                                        value={opt.name}
                                        id={`addon-${addOn.label}-${opt.name}`}
                                      />
                                      <label htmlFor={`addon-${addOn.label}-${opt.name}`} className="text-sm cursor-pointer">{opt.name} {opt.price ? `(+₹${opt.price})` : null}</label>
                                    </div>
                                  ))}
                                </RadioGroup>
                              )}
                            </CardContent>
                          </Card>
                        );
                      })}
                    </div>
                  </div>
                )}
              </form>
            </div>
          </div>
        </div>
        <DialogFooter className="flex justify-between items-center pt-2 border-t border-gray-200 mt-2">
          <Button onClick={handlePrev} disabled={current === 0} variant="outline">
            Previous
          </Button>
          <div className="flex-1 text-center text-sm pt-2">{current + 1} / {items.length}</div>
          <Button onClick={handleNext} disabled={current === items.length - 1} variant="outline">
            Next
          </Button>
          <Button onClick={handleSave} className="ml-2">Save</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default FoodCustomizeStepperDialog; 