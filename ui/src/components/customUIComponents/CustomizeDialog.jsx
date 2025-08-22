'use client';
import { Input } from "@/components/ui/input";
import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogHeader, DialogFooter, DialogTitle } from "@/components/ui/dialog";
import { Card, CardContent } from "@/components/ui/card";

const CustomizeDialog = ({ isOpen, items = [], onSave, onClose }) => {
  const [notes, setNotes] = useState({});

  useEffect(() => {
    const initialNotes = {};
    items.forEach(item => {
      if (item.customNotes && Array.isArray(item.customNotes) && item.customNotes.length > 0) {
        initialNotes[item.itemName] = item.customNotes[0]?.note || "";
      } else if (typeof item.customNotes === 'string') {
        initialNotes[item.itemName] = item.customNotes;
      } else {
        initialNotes[item.itemName] = "";
      }
    });
    setNotes(initialNotes);
  }, [items]);

  const handleNoteChange = (itemName, value) => {
    setNotes(prev => ({ ...prev, [itemName]: value }));
  };

  if (!isOpen || !items.length) return null;

  return (
    <Dialog open={isOpen} onOpenChange={v => { if (!v) onClose(); }}>
      <DialogContent className="max-w-md max-h-[90vh] flex flex-col">
        <DialogHeader>
          <DialogTitle>Customize Items</DialogTitle>
        </DialogHeader>
        <Card className="shadow-none border-none flex-1 flex flex-col min-h-0">
          <CardContent className="p-0 flex-1 min-h-0 overflow-y-auto">
            {items.map(item => (
              <div className="mb-2" key={item.itemName}>
                <Label className="text-sm mb-1">Note for {item.itemName}</Label>
                <Input
                  type="text"
                  value={notes[item.itemName] || ""}
                  onChange={e => handleNoteChange(item.itemName, e.target.value)}
                />
              </div>
            ))}
          </CardContent>
        </Card>
        <DialogFooter>
          <Button variant="outline" onClick={onClose} className="text-sm">Cancel</Button>
          <Button
            onClick={() => onSave(
              items.map(item => ({
                ...item,
                customNotes: notes[item.itemName] || ''
              }))
            )}
            className="text-sm"
          >Save</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default CustomizeDialog;
