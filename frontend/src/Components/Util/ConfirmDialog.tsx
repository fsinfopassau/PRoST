import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "@radix-ui/react-dialog";
import { CheckIcon, Cross2Icon } from "@radix-ui/react-icons";
import { PropsWithChildren, useState } from "react";

interface CustomComponentProps {
  dialogTitle: string;
  dialogDesc: string;
  onSubmit: () => void;
}

const ConfirmDialog: React.FC<PropsWithChildren<CustomComponentProps>> = ({
  children,
  dialogTitle,
  dialogDesc,
  onSubmit,
}) => {
  const [open, setOpen] = useState(false);

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      onSubmit();
      setOpen(false);
    }
    if (event.key === "Escape") {
      setOpen(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <div>{children}</div>
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay className="DialogOverlay" />
        <DialogContent className="DialogContent" onKeyDown={handleKeyDown}>
          <div>
            <DialogClose asChild className="DialogClose">
              <button className="IconButton" aria-label="Close">
                <Cross2Icon />
              </button>
            </DialogClose>
          </div>
          <DialogTitle className="DialogTitle">{dialogTitle}</DialogTitle>
          <DialogDescription className="DialogDescription">
            {dialogDesc}
          </DialogDescription>
          <div
            style={{
              display: "flex",
              marginTop: 25,
              justifyContent: "flex-end",
            }}
          >
            <DialogClose asChild onClick={onSubmit}>
              <button className="Button">
                <CheckIcon />
              </button>
            </DialogClose>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default ConfirmDialog;
