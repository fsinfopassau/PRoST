import { useParams } from "react-router-dom";
import { UserSummaryCard } from "./UserSummaryCard";
import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getUser } from "../Util/Queries";
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
      <div className="CardContainer">
        <UserSummaryCard user={user} />
      </div>
    );
  } else {
    return <ErrorComponent />;
  }
}
