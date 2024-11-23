import {toast} from "react-toastify";
import {hideOwnUser} from "../../Queries";
import {User} from "../../Types/User";
import {UserSummaryCard} from "../StatisticsTab/UserSummaryCard";
import {Switch, SwitchThumb} from "@radix-ui/react-switch";
import ScrollDialog from "../Util/ScrollDialog";
import {InfoCircledIcon} from "@radix-ui/react-icons";

export function PersonalUserOverview(props: { user: User }) {
  const {user} = props;

  function toggleHidden() {
    const newVal = !user.hidden;
    hideOwnUser(newVal).then((result) => {
      if (result) {
        toast.success((newVal ? "Dein Profil ist jetzt versteckt!" : "Dein Profil ist jetzt öffentlich!"));
        user.hidden = newVal;
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  return (
      <>
        <UserSummaryCard user={user}/>
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
          <div className="DisplayCard">
            <h3>Einstellungen</h3>
            <div style={{display: "flex", gap: "1rem", padding: "0.25rem 0", alignItems: "center"}}>
              <Switch className="SwitchRoot" defaultChecked={!user.hidden}
                      onCheckedChange={toggleHidden}>
                <SwitchThumb className="SwitchThumb"/>
              </Switch>
              Öffentliches Profil
              <ScrollDialog
                  title="Öffentliches Profil"
                  onSubmit={() => {
                  }}
                  trigger={
                    <InfoCircledIcon/>
                  }
              >
                <p>Nicht-Öffentliche Profile werden anderen Nutzern und im Kiosk-Menü
                  ausgeblendet</p>
                <p>Des Weiteren bleibt dein Profil, Statistiken und Achievements anderen Nutzern verborgen.</p>
              </ScrollDialog>
            </div>
          </div>
        </div>
      </>
  );
}
