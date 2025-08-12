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

export default function CustomPopup({ type, trigger, onConfirm, dialogDescription, data, onSubmit, selectOptions, isLoading, onSuccess }) {
  const [open, setOpen] = React.useState(false);

  const handleSubmit = async (fields) => {
    await onSubmit(fields, () => setOpen(false));
  };

  return (
    <>
      {type === "delete" ? (
        <DeleteAlertDialog trigger={trigger} onConfirm={onConfirm} />
      ) : (
        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>{trigger}</DialogTrigger>
          <DialogContent className="w-full">
            <DialogTitle className="capitalize">{type}</DialogTitle>
            <DialogDescription>{dialogDescription}</DialogDescription>
            <DialogForm type={type} data={data} onSubmit={handleSubmit} selectOptions={selectOptions} isLoading={isLoading} />
          </DialogContent>
        </Dialog>
      )}
    </>
  );
}
