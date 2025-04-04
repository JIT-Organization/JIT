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

export default function CustomPopup({ type, trigger, onConfirm, dialogDescription, data, onSubmit, selectOptions }) {
  return (
    <>
      {type === "delete" ? (
        <DeleteAlertDialog trigger={trigger} onConfirm={onConfirm} />
      ) : (
        <Dialog>
          <DialogTrigger asChild>{trigger}</DialogTrigger>
          <DialogContent className="w-full">
            <DialogTitle className="capitalize">{type}</DialogTitle>
            <DialogDescription>{dialogDescription}</DialogDescription>
            <DialogForm type={type} data={data} onSubmit={onSubmit} selectOptions={selectOptions} />
          </DialogContent>
        </Dialog>
      )}
    </>
  );
}


// import React, { forwardRef } from "react";
// import DeleteAlertDialog from "../DeleteAlertDialog";
// import DialogForm from "../DialogForm";
// import { Dialog, DialogContent, DialogDescription, DialogTitle } from "../ui/dialog";

// export default function CustomPopup({ type, onConfirm, dialogDescription, isDialogOpen, setIsDialogOpen}) {
//   return (
//     <>
//       {type === "delete" ? (
//         <DeleteAlertDialog onConfirm={onConfirm} />
//       ) : (
//         <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
//           <DialogContent className="w-full">
//             <DialogTitle className="capitalize">{type}</DialogTitle>
//             <DialogDescription>{dialogDescription}</DialogDescription>
//             <DialogForm type={type} />
//           </DialogContent>
//         </Dialog>
//       )}
//     </>
//   );
// }
