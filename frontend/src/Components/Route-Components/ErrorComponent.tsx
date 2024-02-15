export function ErrorComponent({ switchTheme }: any) {
  return (
    <h1 onClick={switchTheme}>
      <img src="/icons/happy-manje/happy beer.svg" />
      Beer not found!
    </h1>
  );
}
