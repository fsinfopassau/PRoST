import { AspectRatio } from "@radix-ui/react-aspect-ratio";
import { useEffect, useState } from "react";

export function LevelProgressDisplay(props: { value: number }) {
  const [width, setWidth] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => {
      setWidth(props.value * 100);
    }, 300);

    return () => clearTimeout(timer);
  }, [props.value]);

  return (
    <>
      <AspectRatio ratio={546 / 15}>
        <div className="progress-bar">
          <div className="progress-bar-background">
            <div className="progress-bar-foreground"
              style={{ width: `${width}%` }}
            ></div>
          </div>
        </div>
      </AspectRatio>
    </>
  );
}
