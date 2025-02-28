import React from "react";
import DeleteAlertDialog from "../DeleteAlertDialog";
import DialogForm from "../DialogForm";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";

export default function CustomPopup({ type, trigger, onConfirm, dialogDescription }) {
  return (
    <>
      {type === "delete" ? (
        <DeleteAlertDialog trigger={trigger} onConfirm={onConfirm} />
      ) : (
        <Dialog>
          <DialogTrigger>{trigger}</DialogTrigger>
          <DialogContent className="w-full">
            <DialogTitle className="capitalize">{type}</DialogTitle>
            <DialogDescription>{dialogDescription}</DialogDescription>
            <DialogForm type={type} />
          </DialogContent>
        </Dialog>
      )}
    </>
  );
}
