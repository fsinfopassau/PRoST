import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import { CompositeMetricType, ItemMetricType, TimeSpan } from "../../Types/Statistics";
import { useEffect, useState } from "react";
import { getCompositeMetric, getItemMetric } from "../../Queries";
import { ChartData } from "../../Types/Chart";

const COLORS = [
  "#6B9080", // Muted Teal
  "#F6BD60", // Warm Gold
  "#F28482", // Peach Pink
  "#84A59D", // Sage Green
  "#F5CAC3", // Blush
  "#96C1B1", // Mint Green
  "#A997DF", // Lavender
  "#FFD166", // Sunflower Yellow
  "#6A4C93", // Deep Purple
  "#99C1DE", // Sky Blue
  "#E5989B", // Soft Rose
  "#55A630", // Leaf Green
  "#EF476F", // Watermelon Red
  "#118AB2", // Ocean Blue
  "#FFD670", // Pale Amber
  "#8D99AE", // Dusty Gray
  "#073B4C", // Dark Cyan
  "#FFC300", // Rich Gold
  "#457B9D", // Muted Denim
  "#FF6F61", // Soft Coral
];

export function MetricPieChart(props: { entries: ChartData[] | undefined; title: string; desc: string }) {
  const { entries, title, desc } = props;
  const [data, setData] = useState<ChartData[]>([]);
  const [shift, setShift] = useState<number>(0);

  useEffect(() => {
    if (entries) {
      setData(entries);
      //setShift(Math.floor(Math.random() * entries.length));
      setShift(0);
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
              {data.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[(index + shift) % COLORS.length]} />
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

export function ItemMetricPieChart(props: { type: ItemMetricType; time: TimeSpan; title: string; desc: string }) {
  const { type, time, title, desc } = props;

  const [data, setData] = useState<ChartData[]>([]);

  useEffect(() => {
    getItemMetric(type, time).then((l) => {
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
  type: CompositeMetricType;
  filterFirst: boolean;
  dataKey: string;
  time: TimeSpan;
  title: string;
  desc: string;
}) {
  const { type, filterFirst, dataKey, time, title, desc } = props;

  const [data, setData] = useState<ChartData[]>([]);

  useEffect(() => {
    getCompositeMetric(type, time).then((l) => {
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
