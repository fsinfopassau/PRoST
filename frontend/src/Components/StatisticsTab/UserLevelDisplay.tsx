import { Progress, ProgressIndicator } from "@radix-ui/react-progress";
import { useEffect, useState } from "react";

export function LevelProgressDisplay(props: { value: number }) {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const timer = setTimeout(
      () => setProgress(Math.min(1, Math.max(0, props.value))),
      200
    );
    return () => clearTimeout(timer);
  }, []);

  return (
    <Progress className="ProgressRoot" value={progress}>
      <ProgressIndicator
        className="ProgressIndicator"
        style={{ transform: `translateX(-${100 - progress * 100}%)` }}
      />
    </Progress>
  );
}
