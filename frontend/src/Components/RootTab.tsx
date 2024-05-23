import { useEffect, useState } from "react";
import { isOnlyKiosk, isUser } from "../SessionInfo";
import { User } from "../Types/User";
import { getOwnUser } from "../Queries";
import { ErrorComponent } from "./Util/ErrorTab";
import { UserContainer } from "./SearchTab/UserContainer";
import { PersonalUserOverview } from "./PersonalView/PersonalUserOverview";

export function RootTab(props: { switchTheme: () => void }) {
  const [user, setUser] = useState<User | undefined>(undefined);

  useEffect(() => {
    getOwnUser()
      .then((user) => {
        setUser(user);
      })
      .catch(() => {
        setUser(undefined);
      });
  }, []);

  return (
    <>
      <div>
        <h1>
          <img
            onClick={props.switchTheme}
            src="icons/happy-manje/happy beer.svg"
            id="MainIcon"
          />
          PRoST
        </h1>
        {user === undefined ? (
          <></>
        ) : isOnlyKiosk() ? (
          <UserContainer />
        ) : isUser() ? (
          <PersonalUserOverview user={user} />
        ) : (
          <ErrorComponent />
        )}
      </div>
    </>
  );
}
