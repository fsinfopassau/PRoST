import { toast } from "react-toastify";
import { hideOwnUser, setOwnKiosk } from "../../Queries";
import { User } from "../../Types/User";
import { UserSummaryCard } from "../StatisticsTab/UserSummaryCard";
import { Switch, SwitchThumb } from "@radix-ui/react-switch";
import ScrollDialog from "../Util/ScrollDialog";
import { InfoCircledIcon } from "@radix-ui/react-icons";
import { Link } from "react-router-dom";
import { useState } from "react";

export function PersonalUserOverview(props: { user: User }) {
  const { user } = props;
  const [hidden, setHidden] = useState(user.hidden);
  const [kiosk, setKiosk] = useState(user.kiosk);

  function toggleHidden() {
    const newVal = !hidden;
    hideOwnUser(newVal).then((result) => {
      if (result) {
        setHidden(newVal);
        toast.success(newVal ? "Dein Profil ist jetzt versteckt!" : "Dein Profil ist jetzt öffentlich!");
      } else {
        toast.error("Änderung fehlgeschlagen!");
      }
    });
  }

  function toggleKiosk() {
    const newVal = !kiosk;
    setOwnKiosk(newVal).then((result) => {
      if (result) {
        setKiosk(newVal);
        toast.success(
          newVal ? "Der Kiosk ist mit deinem Profil nutzbar!" : "Der Kiosk kann dein Profil nicht mehr nutzen!"
        );
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
            <ScrollDialog title="Öffentliches Profil" onSubmit={undefined} trigger={<InfoCircledIcon />}>
              <p>Private Profile werden anderen Nutzern und im Kiosk-Menü ausgeblendet.</p>
              <p>Des Weiteren sind Statistiken und Achievements von privaten Profilen anonym.</p>
            </ScrollDialog>
          </div>
          <div style={{ display: "flex", gap: "1rem", padding: "0.25rem 0", alignItems: "center" }}>
            <Switch className="SwitchRoot" defaultChecked={user.kiosk} onCheckedChange={toggleKiosk}>
              <SwitchThumb className="SwitchThumb" />
            </Switch>
            Kiosk
            <ScrollDialog title="Kiosk" onSubmit={undefined} trigger={<InfoCircledIcon />}>
              <p>Der Kiosk kann, wenn abgeschalten, keine Käufe in deinem Namen tätigen.</p>
              <p>Käufe können dann nur noch durch das Profil des Nutzers durchgeführt werden.</p>
            </ScrollDialog>
          </div>
        </div>
      </div>
    </>
  );
}
