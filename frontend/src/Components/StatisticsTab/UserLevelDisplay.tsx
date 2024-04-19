import { useEffect, useState } from "react";

export function LevelProgressDisplay(props: { value: number }) {
  const [width, setWidth] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => {
      setWidth(props.value * 100);
    }, 300);

    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="ProgressRoot">
      <div
        className="ProgressIndicator"
        role="progressbar"
        style={{ width: `${width}%` }}
      ></div>
    </div>
  );
}
