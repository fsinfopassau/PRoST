import { useParams } from "react-router-dom";
import { UserSummaryCard } from "./UserSummaryCard";
import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getUser } from "../../Queries";
import { ErrorComponent } from "../Util/ErrorTab";

export function UserStatistics() {
  const [user, setUser] = useState<User>();
  const { userId } = useParams();

  useEffect(() => {
    if (userId)
      getUser(userId).then((user) => {
        setUser(user);
      });
  }, []);

  if (user) {
    return (
      <>
        <div className="SingleCardContainer">
          <div>
            <div className="GridContainer" style={{ flexGrow: "0" }}>
              <UserSummaryCard user={user} />
            </div>
          </div>
        </div>
      </>
    );
  } else {
    return <ErrorComponent />;
  }
}
