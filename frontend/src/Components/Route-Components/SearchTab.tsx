import { useParams } from "react-router-dom";
import { UserSelection } from "../UserSelection/UserSelection";
import { ItemSelection } from "../ItemSelection/ItemSelection";

export function SearchTab(props: { switchTheme: () => void }) {
  const { user } = useParams();

  if (user !== undefined && user !== null) {
    return <ItemSelection />
  } else {
    return <UserSelection switchTheme={props.switchTheme} />
  }
}
