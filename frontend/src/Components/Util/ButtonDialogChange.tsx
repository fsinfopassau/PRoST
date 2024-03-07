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

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  function submit() {
    onSubmit(inputValue);
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <button>{name}</button>
      </DialogTrigger>
      <DialogPortal>
        <DialogOverlay className="DialogOverlay" />
        <DialogContent className="DialogContent">
          <div>
            <DialogClose asChild className="DialogClose">
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
            <input className="Input" onChange={handleInputChange} />
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
