import { getAuthorizedUser, isKiosk, isUser } from "../SessionInfo";
import { UserSelection } from "./SearchTab/UserSelection";
import { Login } from "./Util/Login";

export function MainTab(props: { switchTheme: () => void }) {
  return (
    <>
      {!isUser() ? (
        <>
          <Login></Login>
        </>
      ) : isKiosk() ? (
        <UserSelection switchTheme={props.switchTheme} />
      ) : (
        <>
          <h1>{getAuthorizedUser()?.displayName}</h1>
          <p>TODO</p>
        </>
      )}
    </>
  );
}
