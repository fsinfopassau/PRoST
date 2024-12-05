import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import { CompositeLeaderboardType, ItemLeaderboardType, TimeSpan } from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { getCompositeLeaderboard, getItemLeaderboard } from "../../Queries";

type PieChartData = {
  name: string;
  value: number;
};

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];

export function MetricPieChart(props: { entries: PieChartData[] | undefined; title: string; desc: string }) {
  const { entries, title, desc } = props;
  const [data, setData] = useState<PieChartData[]>([]);

  useEffect(() => {
    if (entries) {
      setData(entries);
    }
  }, [entries]);

  return (
    <div className="DisplayCard">
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "0rem", flexDirection: "column" }}>
          <div className="bold">{title}</div>
          <PieChart width={400} height={400}>
            <Pie data={data} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={125} fill="#8884d8" label>
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
          <div>{desc}</div>
        </div>
      </div>
    </div>
  );
}

export function ItemMetricPieChart(props: { type: ItemLeaderboardType; time: TimeSpan; title: string; desc: string }) {
  const { type, time, title, desc } = props;

  const [data, setData] = useState<PieChartData[]>([]);

  useEffect(() => {
    getItemLeaderboard(type, time).then((l) => {
      if (l) {
        const newData = l.map((entry) => ({
          name: entry.entity.displayName, // Use the ShopItem's name for the chart label
          value: entry.value, // Use the value as the chart data point
        }));
        setData(newData);
      }
    });
  }, [time]);

  return <MetricPieChart entries={data} title={title} desc={desc} />;
}

export function CompositeMetricPieChart(props: {
  type: CompositeLeaderboardType;
  filterFirst: boolean;
  dataKey: string;
  time: TimeSpan;
  title: string;
  desc: string;
}) {
  const { type, filterFirst, dataKey, time, title, desc } = props;

  const [data, setData] = useState<PieChartData[]>([]);

  useEffect(() => {
    getCompositeLeaderboard(type, time).then((l) => {
      if (l) {
        if (filterFirst) {
          l = l.filter((i) => {
            return i.key1 == dataKey;
          });
        } else {
          l = l.filter((i) => {
            return i.key2 == dataKey;
          });
        }

        const uniqueKeys = Array.from(new Set(l.map((entry) => JSON.stringify([entry.key1DisplayName, entry.value]))))
          .map((key) => JSON.parse(key))
          .map(([name, value]) => ({ name, value })); // Ensure correct shape
        setData(uniqueKeys);
      }
    });
  }, [time]);

  return <MetricPieChart entries={data} title={title} desc={desc} />;
}
