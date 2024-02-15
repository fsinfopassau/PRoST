import {
  BarChartIcon,
  GearIcon,
  MagnifyingGlassIcon,
} from "@radix-ui/react-icons";
import { Tabs, TabsList, TabsTrigger } from "@radix-ui/react-tabs";
import { useLocation, useNavigate } from "react-router-dom";

export function TabChanger() {
  const navigate = useNavigate();

  function tabUpdate(newValue: string) {
    if (newValue === "search") {
      navigate("/");
    } else {
      navigate("/" + newValue);
    }
  }

  function getTabName(): string {
    var s = useLocation().pathname.split("/")[1];
    if (s.length > 0) return s;
    return "search";
  }

  return (
    <Tabs
      defaultValue={getTabName()}
      className="TabsRoot"
      onValueChange={tabUpdate}
    >
      <TabsList className="TabsList">
        <TabsTrigger value="search" className="TabsTrigger">
          <MagnifyingGlassIcon />
        </TabsTrigger>
        <TabsTrigger value="stats" className="TabsTrigger">
          <BarChartIcon />
        </TabsTrigger>
        <TabsTrigger value="settings" className="TabsTrigger">
          <GearIcon />
        </TabsTrigger>
      </TabsList>
    </Tabs>
  );
}
