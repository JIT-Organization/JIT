'use client';
import { Input } from "@/components/ui/input";
import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";

const CustomizeDialog = ({ isOpen, item, onSave, onClose }) => {
  if (!isOpen || !item) return null;

  const [noteGroups, setNoteGroups] = useState([]);

  useEffect(() => {
    if (Array.isArray(item.customNotes) && item.customNotes.length > 0) {
      setNoteGroups(item.customNotes);
    } else {
      setNoteGroups([{ note: "", qty: item.qty }]);
    }
  }, [item]);
  

  const handleChange = (index, field, value) => {
    setNoteGroups((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: field === 'qty' ? parseInt(value || 0) : value };
      return updated;
    });
  };

  const addNoteGroup = () => {
    setNoteGroups([...noteGroups, { note: "", qty: 1 }]);
  };

  const removeNoteGroup = (index) => {
    setNoteGroups(noteGroups.filter((_, i) => i !== index));
  };

  const totalQty = noteGroups.reduce((sum, g) => sum + (g.qty || 0), 0);
  const isQtyExceeded = totalQty > item.qty;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-background rounded-lg shadow-lg w-full max-w-md p-4 space-y-4">
        <div className="flex justify-between items-center border-b pb-2">
          <h3 className="text-lg font-semibold">Customize: {item.menuItemName}</h3>
          <Button variant="ghost" size="icon" onClick={onClose} className="text-xl">
            ×
          </Button>
        </div>

        <div>
          <h4 className="font-medium mb-2">Customize Notes (Total Qty: {totalQty}/{item.qty})</h4>
          {noteGroups.map((group, i) => (
            <div key={i} className="flex items-end gap-2 mb-2">
              <div className="flex-1">
                <Label className="text-sm mb-1">Note</Label>
                <Input
                  type="text"
                  value={group.note}
                  onChange={(e) => handleChange(i, 'note', e.target.value)}
                />
              </div>
              <div style={{ width: 80 }}>
                <Label className="text-sm mb-1">Qty</Label>
                <Input
                  type="number"
                  min={1}
                  max={item.qty}
                  value={group.qty}
                  onChange={(e) => handleChange(i, 'qty', e.target.value)}
                  className="text-center"
                />
              </div>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => removeNoteGroup(i)}
                className="text-red-500 text-xl pb-1"
                title="Remove"
              >
                ×
              </Button>
            </div>
          ))}
          {totalQty < item.qty && (
            <Button
              onClick={addNoteGroup}
              variant="secondary"
              className="mt-2 px-3 py-1 rounded-full"
            >
              + Add Note
            </Button>
          )}
          {isQtyExceeded && (
            <p className="text-destructive text-sm mt-1">
              Total quantity exceeds available ({item.qty}).
            </p>
          )}
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t">
          <Button
            variant="outline"
            onClick={onClose}
            className="text-sm"
          >
            Cancel
          </Button>
          <Button
            onClick={() => !isQtyExceeded && onSave(item.menuItemName, noteGroups)}
            disabled={isQtyExceeded}
            className="text-sm"
          >
            Save
          </Button>
        </div>
      </div>
    </div>
  );
};

export default CustomizeDialog;
