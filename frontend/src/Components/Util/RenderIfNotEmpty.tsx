import { isNotBlank } from "./utils";

export const RenderIfNotEmpty = <T,>(props: { dependency: T; render: () => React.ReactNode }) => {
  const { dependency, render } = props;

  const satisfiesString = typeof dependency === "string" ? isNotBlank(dependency) : true;

  if (dependency !== undefined && dependency !== null && satisfiesString) {
    return render();
  } else {
    return <></>;
  }
};
