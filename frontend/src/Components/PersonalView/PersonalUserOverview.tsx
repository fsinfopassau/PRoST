import { toast } from "react-toastify";
import { hideOwnUser, setOwnKiosk } from "../../Queries";
import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import ScrollDialog from "../Util/ScrollDialog";
import { InfoCircledIcon } from "@radix-ui/react-icons";
import { Link } from "react-router-dom";

export function PersonalUserOverview(props: { user: User }) {
  const { user } = props;

  function toggleHidden() {
    const newVal = !user.hidden;
    hideOwnUser(newVal).then((result) => {
      if (result) {
        toast.success(newVal ? "Dein Profil ist jetzt versteckt!" : "Dein Profil ist jetzt öffentlich!");
        user.hidden = newVal;
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function toggleKiosk() {
    const newVal = !user.kiosk;
    setOwnKiosk(newVal).then((result) => {
      if (result) {
        toast.success(
          newVal ? "Der Kiosk ist mit deinem Profil nutzbar!" : "Dein Profil kann den Kiosk nicht mehr nutzen!"
        );
        user.hidden = newVal;
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  return (
    <>
      <UserSummaryCard user={user} />
      <div className="GridContainer">
        <div className="DisplayCard" style={{ display: "flex", justifyContent: "space-evenly" }}>
          <Link to={`/stats/users/${user.id}`} className="bold Button">
            <h3>Deine Statistik</h3>
          </Link>
          <Link to={`/stats/users`} className="bold Button">
            <h3>Ranglisten</h3>
          </Link>
        </div>
        <div className="DisplayCard">
          <h3>Errungenschaften</h3>
          <p>
            Hilf mit! Bei der Entwicklung von <a href="https://github.com/fsinfopassau/PRoST">PRoST auf GitHub 💖</a>
          </p>
        </div>
        <div className="DisplayCard">
          <h3>Einstellungen</h3>
          <div style={{ display: "flex", gap: "1rem", padding: "0.25rem 0", alignItems: "center" }}>
            <Switch className="SwitchRoot" defaultChecked={!user.hidden} onCheckedChange={toggleHidden}>
              <SwitchThumb className="SwitchThumb" />
            </Switch>
            Öffentliches Profil
            <ScrollDialog title="Öffentliches Profil" onSubmit={() => {}} trigger={<InfoCircledIcon />}>
              <p>Nicht-Öffentliche Profile werden anderen Nutzern und im Kiosk-Menü ausgeblendet</p>
              <p>Des Weiteren bleiben Profil, Statistiken und Achievements anderen Nutzern verborgen.</p>
            </ScrollDialog>
          </div>
          <div style={{ display: "flex", gap: "1rem", padding: "0.25rem 0", alignItems: "center" }}>
            <Switch className="SwitchRoot" defaultChecked={user.kiosk} onCheckedChange={toggleKiosk}>
              <SwitchThumb className="SwitchThumb" />
            </Switch>
            Kiosk
            <ScrollDialog title="Kiosk" onSubmit={() => {}} trigger={<InfoCircledIcon />}>
              <p>Der Kiosk kann, wenn abgeschalten, keine Käufe in deinem Namen tätigen.</p>
              <p>Käufe können dann nur noch durch das Profil des Nutzers durchgeführt werden.</p>
            </ScrollDialog>
          </div>
        </div>
      </div>
    </>
  );
}
