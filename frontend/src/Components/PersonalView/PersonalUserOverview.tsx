import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";

export function PersonalUserOverview(props: { user: User }) {
  const { user } = props;

  return (
    <>
      <UserSummaryCard user={user} />
      <div className="GridContainer">
        <div className="DisplayCard">
          <h3>Statistiken</h3>
          <p>...</p>
        </div>
        <div className="DisplayCard">
          <h3>Ranking</h3>
          <p>...</p>
        </div>
        <div className="DisplayCard">
          <h3>Achievements</h3>
          <p>...</p>
        </div>
      </div>
    </>
  );
}
