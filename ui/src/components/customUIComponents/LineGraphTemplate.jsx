"use client"

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid
} from "recharts";
import {
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"

const incomeData = [
  { day: 'Monday', cash: 300, online: 500, wallet: 100 },
  { day: 'Tuesday', cash: 200, online: 450, wallet: 150 },
  { day: 'Wednesday', cash: 350, online: 400, wallet: 200 },
  { day: 'Thursday', cash: 300, online: 600, wallet: 180 },
  { day: 'Friday', cash: 400, online: 700, wallet: 120 },
  { day: 'Saturday', cash: 250, online: 650, wallet: 140 },
  { day: 'Sunday', cash: 300, online: 550, wallet: 160 },
]

const chartConfig = {
  cash: { color: "#ef4444", label: "Cash" },
  online: { color: "#3b82f6", label: "Online" },
  wallet: { color: "#10b981", label: "Wallet" },
}

const minMaxPoints = {};
const valueKeys = Object.keys(incomeData[0]).filter(key => key !== 'day');

valueKeys.forEach((key) => {
  const values = incomeData.map((d) => d[key]);
  const min = Math.min(...values);
  const max = Math.max(...values);
  minMaxPoints[key] = new Set();

  incomeData.forEach((d, index) => {
    if (d[key] === min || d[key] === max) {
      minMaxPoints[key].add(index);
    }
  });
});

const CustomDot = ({ cx, cy, index, keyName, payload }) => {
    const isMinOrMax = minMaxPoints[keyName]?.has(index);
    const value = payload?.[keyName];
  
    return (
      <>
        {isMinOrMax ? (
          <rect
            x={cx - 5}
            y={cy - 5}
            width={10}
            height={10}
            fill={chartConfig[keyName]?.color || "#000"}
          />
        ) : (
          <circle
            cx={cx}
            cy={cy}
            r={4}
            fill={chartConfig[keyName]?.color || "#000"}
          />
        )}
        {/* Value label */}
        {/* //{isMinOrMax && ( */}
          <text
            x={cx}
            y={cy - 10}
            textAnchor="middle"
            fill={chartConfig[keyName]?.color || "#000"}
            fontSize={10}
          >
            {value}
          </text>
        {/* // )} */}
      </>
    );
  };
  


//const valueKeys = Object.keys(incomeData[0]).filter(key => key !== 'day');


export default function LineGraphTemplate() {
  return (
    <ChartContainer config={chartConfig} className="w-full">
      <LineChart
        data={incomeData}
        margin={{ top: 10, right: 10, bottom: 10, left: 0 }}
      >
        {/* <CartesianGrid strokeDasharray="3 3" /> */}
        <XAxis dataKey="day" scale="point" padding={{ left: 20, right: 20 }} />
        <YAxis />
        <ChartTooltip content={<ChartTooltipContent />} />
        <ChartLegend content={<ChartLegendContent />} />
        {valueKeys.map((key) => (
  <Line
    key={key}
    type="linear"
    dataKey={key}
    stroke={chartConfig[key]?.color || '#000'}  // fallback if color is missing
    strokeWidth={2}
    dot={(props) => <CustomDot {...props} keyName={key} />}
    activeDot={{ r: 6 }}
  />
))}
      </LineChart>
    </ChartContainer>
  )
}
