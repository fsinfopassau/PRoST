import { useState } from "react";
import ScrollDialog from "./ScrollDialog";
import { isValidString } from "../../Format";

export function ButtonDialogChanger(props: {
  trigger: React.ReactNode;
  dialogName: string;
  dialogDesc: string;
  onSubmit: (value: string) => void;
}) {
  const { trigger, dialogName, dialogDesc, onSubmit } = props;
  const [inputValue, setInputValue] = useState<string>("");

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  function submit() {
    onSubmit(inputValue);
  }

  return (
    <ScrollDialog onSubmit={submit} title={dialogName} trigger={trigger}>
      <div
        style={{
          padding: "1rem",
          display: "flex",
          flexDirection: "column",
          gap: ".5rem",
        }}
      >
        <div className="DialogDescription">{dialogDesc}</div>
        <fieldset className="Fieldset">
          <input className={isValidString(inputValue) ? "Input good-color" : "Input danger-color"} onChange={handleInputChange} />
        </fieldset>
      </div>
    </ScrollDialog>
  );
}
