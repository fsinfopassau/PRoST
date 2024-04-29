import { useEffect, useState } from "react";
import { isKiosk, isUser } from "../SessionInfo";
import { User } from "../Types/User";
import { getOwnUser } from "../Queries";
import { ErrorComponent } from "./Util/ErrorTab";
import { UserContainer } from "./SearchTab/UserContainer";
import { PersonalUserOverview } from "./PersonalView/PersonalUserOverview";

export function RootTab(props: { switchTheme: () => void }) {
  const [user, setUser] = useState<User | undefined>(undefined);

  useEffect(() => {
    if (!isKiosk() && isUser()) {
      getOwnUser()
        .then((user) => {
          setUser(user);
        })
        .catch(() => {
          setUser(undefined);
        });
    }
  }, []);

  return (
    <>
      <div>
        <h1>
          <img
            onClick={props.switchTheme}
            src="icons/happy-manje/happy beer.svg"
            id="SearchTitle"
          />
          PRoST
        </h1>
        {!isUser() ? (
          <></>
        ) : isKiosk() ? (
          <UserContainer />
        ) : user === undefined ? (
          <ErrorComponent />
        ) : (
          <PersonalUserOverview user={user} />
        )}
      </div>
    </>
  );
}
