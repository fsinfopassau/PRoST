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
import { useState } from "react";

export function ButtonDialogChanger(props: {
  name: string;
  dialogName: string;
  dialogDesc: string;
  onSubmit: (value: string) => void;
}) {
  const { name, dialogName, dialogDesc, onSubmit } = props;
  const [inputValue, setInputValue] = useState<string>("");
  const [open, setOpen] = useState(false);

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  function submit() {
    onSubmit(inputValue);
  }

  function handleKeyDown(event: React.KeyboardEvent) {
    if (event.key === "Enter") {
      submit();
      setOpen(false);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <button>{name}</button>
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay className="DialogOverlay" />
        <DialogContent className="DialogContent">
          <div>
            <DialogClose asChild>
              <button className="IconButton" aria-label="Close">
                <Cross2Icon />
              </button>
            </DialogClose>
          </div>
          <DialogTitle className="DialogTitle">{dialogName}</DialogTitle>
          <DialogDescription className="DialogDescription">
            {dialogDesc}
          </DialogDescription>
          <fieldset className="Fieldset">
            <input
              className="Input"
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
            />
          </fieldset>
          <div
            style={{
              display: "flex",
              marginTop: 25,
              justifyContent: "flex-end",
            }}
          >
            <DialogClose asChild onClick={submit}>
              <button className="Button">
                <CheckIcon />
              </button>
            </DialogClose>
          </div>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
}
