import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
} from "@radix-ui/react-dialog";
import { CheckIcon, Cross2Icon } from "@radix-ui/react-icons";
import {
  ScrollArea,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from "@radix-ui/react-scroll-area";
import { PropsWithChildren, useState } from "react";

interface CustomComponentProps {
  title: string;
  trigger: React.ReactNode;
  onSubmit: () => void;
}

const ScrollDialog: React.FC<PropsWithChildren<CustomComponentProps>> = ({
  children,
  title,
  trigger,
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
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogPortal>
        <DialogOverlay className="DialogOverlay">
          <DialogContent className="DialogContent" onKeyDown={handleKeyDown}>
            <DialogTitle className="DialogTitle">{title}</DialogTitle>
            <div className="DialogDescription">
              <ScrollArea
                style={{
                  height: "100%",
                  maxWidth: "80rem",
                }}
              >
                <ScrollAreaViewport
                  style={{
                    maxHeight: "15rem",
                    minHeight: "50vh",
                    height: "50%",
                    width: "100%",
                  }}
                >
                  {children}
                </ScrollAreaViewport>
                <ScrollAreaScrollbar
                  className="Scrollbar"
                  orientation="vertical"
                >
                  <ScrollAreaThumb className="ScrollbarThumb" />
                </ScrollAreaScrollbar>
                <ScrollAreaScrollbar
                  className="Scrollbar"
                  orientation="horizontal"
                >
                  <ScrollAreaThumb className="ScrollbarThumb" />
                </ScrollAreaScrollbar>
              </ScrollArea>
            </div>
            <div
              style={{
                display: "flex",
                marginTop: "1rem",
                justifyContent: "space-evenly",
              }}
            >
              <DialogClose asChild>
                <div
                  className="Button red"
                  style={{ width: "40%" }}
                  aria-label="Close"
                >
                  <Cross2Icon />
                </div>
              </DialogClose>
              <DialogClose asChild onClick={onSubmit}>
                <div className="Button green" style={{ width: "40%" }}>
                  <CheckIcon />
                </div>
              </DialogClose>
            </div>
          </DialogContent>
        </DialogOverlay>
      </DialogPortal>
    </Dialog>
  );
};

export default ScrollDialog;
