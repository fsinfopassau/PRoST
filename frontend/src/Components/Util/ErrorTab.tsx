import { BASE_PATH } from "../App";

export function ErrorComponent() {
  return (
    <h1>
      <img src={`${BASE_PATH}/icons/happy-manje/happy beer.svg`} />
      Beer not found!
    </h1>
  );
}
