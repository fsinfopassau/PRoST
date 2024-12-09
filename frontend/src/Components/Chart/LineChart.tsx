import { useEffect, useState } from "react";
import { ChartData } from "../../Types/Chart";
import { CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis } from "recharts";
import { getCompositeMetric } from "../../Queries";
import { CompositeMetricType, CompositeMetricEntry, TimeSpan } from "../../Types/Statistics";

export function MetricLineChart(props: { entries: ChartData[] | undefined; title: string; desc: string }) {
  const { entries, title, desc } = props;
  const [data, setData] = useState<ChartData[]>([]);

  useEffect(() => {
    if (entries) {
      const sortedData = [...entries].sort((a, b) => parseInt(a.name) - parseInt(b.name));
      setData(sortedData);
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
          <LineChart width={800} height={400} data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="value" stroke="#8884d8" activeDot={{ r: 8 }} />
          </LineChart>
          <div>{desc}</div>
        </div>
      </div>
    </div>
  );
}

export function CompositeMetricLineChart(props: {
  type: CompositeMetricType;
  filterFirst: boolean;
  dataKey: string | undefined;
  time: TimeSpan;
  title: string;
  desc: string;
}) {
  const { type, filterFirst, dataKey, time, title, desc } = props;

  const [data, setData] = useState<ChartData[]>([]);

  useEffect(() => {
    getCompositeMetric(type, time).then((l) => {
      if (l) {
        if (dataKey == undefined) {
          l = compress(l);
        } else {
          if (filterFirst) {
            l = l.filter((i) => {
              return i.key1 == dataKey;
            });
          } else {
            l = l.filter((i) => {
              return i.key2 == dataKey;
            });
          }
        }

        const uniqueKeys = Array.from(new Set(l.map((entry) => JSON.stringify([entry.key1DisplayName, entry.value]))))
          .map((key) => JSON.parse(key))
          .map(([name, value]) => ({ name, value })); // Ensure correct shape
        setData(uniqueKeys);
      }
    });
  }, [time]);

  return <MetricLineChart entries={data} title={title} desc={desc} />;
}

function compress(data: CompositeMetricEntry[]): CompositeMetricEntry[] {
  const groupedByKey1 = new Map<string, CompositeMetricEntry>();

  for (const entry of data) {
    if (groupedByKey1.has(entry.key1)) {
      const existingEntry = groupedByKey1.get(entry.key1)!;
      existingEntry.value += entry.value;
    } else {
      groupedByKey1.set(entry.key1, { ...entry });
    }
  }

  return Array.from(groupedByKey1.values());
}
