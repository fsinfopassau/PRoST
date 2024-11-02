import { useEffect } from "react";

export const useEffectOnce = (effectCalllback: () => void) => {
  useEffect(effectCalllback, []);
};
