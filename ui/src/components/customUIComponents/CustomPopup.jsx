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
          <DialogContent className="w-full max-w-4xl p-6 sm:p-8 md:p-10 rounded-2xl shadow-lg">
            <DialogTitle className="capitalize text-2xl font-semibold mb-1">{type}</DialogTitle>
            <DialogDescription className="text-muted-foreground mb-6">
              {dialogDescription}
            </DialogDescription>
            <DialogForm
              type={type}
              data={data}
              onSubmit={onSubmit}
              selectOptions={selectOptions}
            />
          </DialogContent>
        </Dialog>
      )}
    </>
  );
}
