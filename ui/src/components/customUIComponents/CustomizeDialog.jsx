'use client';
import React, { useState, useEffect } from "react";

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
      <div className="bg-white rounded-lg shadow-lg w-full max-w-md p-4 space-y-4">
        <div className="flex justify-between items-center border-b pb-2">
          <h3 className="text-lg font-semibold">Customize: {item.name}</h3>
          <button onClick={onClose} className="text-gray-500 text-xl">×</button>
        </div>

        <div>
          <h4 className="font-medium mb-2">Customize Notes (Total Qty: {totalQty}/{item.qty})</h4>
          {noteGroups.map((group, i) => (
            <div key={i} className="flex items-end gap-2 mb-2">
              <div className="flex-1">
                <label className="text-sm block mb-1">Note</label>
                <input
                  type="text"
                  value={group.note}
                  onChange={(e) => handleChange(i, 'note', e.target.value)}
                  className="w-full border p-2 rounded"
                />
              </div>
              <div style={{ width: 80 }}>
                <label className="text-sm block mb-1">Qty</label>
                <input
                  type="number"
                  min={1}
                  max={item.qty}
                  value={group.qty}
                  onChange={(e) => handleChange(i, 'qty', e.target.value)}
                  className="w-full border p-2 rounded text-center"
                />
              </div>
              <button
                onClick={() => removeNoteGroup(i)}
                className="text-red-500 text-xl pb-1"
                title="Remove"
              >
                ×
              </button>
            </div>
          ))}
          {totalQty < item.qty && (
            <button
              onClick={addNoteGroup}
              className="mt-2 px-3 py-1 bg-orange-500 text-white rounded-full"
            >
              + Add Note
            </button>
          )}
          {isQtyExceeded && (
            <p className="text-red-500 text-sm mt-1">
              Total quantity exceeds available ({item.qty}).
            </p>
          )}
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t">
          <button
            onClick={onClose}
            className="px-3 py-1 border rounded text-sm text-gray-600"
          >
            Cancel
          </button>
          <button
            onClick={() => !isQtyExceeded && onSave(item.id, noteGroups)}
            disabled={isQtyExceeded}
            className="px-3 py-1 bg-blue-600 text-white rounded text-sm disabled:opacity-50"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
};

export default CustomizeDialog;
