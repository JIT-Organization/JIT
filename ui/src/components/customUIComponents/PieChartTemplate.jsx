"use client";
import React from "react";
import { PieChart, Pie, Cell } from "recharts";

import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  ChartLegend,
  ChartLegendContent,
} from "@/components/ui/chart"; // adjust path as needed

const COLORS = [
  "#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#a77ee6", "#f26419"
];

const PieChartTemplate = ({ data }) => {
  const config = data.reduce((acc, item, idx) => {
    acc[item.name] = {
      label: item.name,
      color: COLORS[idx % COLORS.length],
    };
    return acc;
  }, {});

  return (
    <ChartContainer config={config} className="w-full h-88">
      <PieChart>
      <Pie
            data={data}
            dataKey="quantity"
            nameKey="name"
            cx="50%"
            cy="50%"
            innerRadius={25}
            outerRadius={80}
            fill="#8884d8"
            label={({
              cx,
              cy,
              midAngle,
              innerRadius,
              outerRadius,
              percent,
              index,
              name,
            }) => {
              const RADIAN = Math.PI / 180;
              const radius = 25 + innerRadius + (outerRadius - innerRadius);
              const x = cx + radius * Math.cos(-midAngle * RADIAN);
              const y = cy + radius * Math.sin(-midAngle * RADIAN);

              return (
                <text
                  x={x}
                  y={y}
                  fill="#333"
                  textAnchor={x > cx ? "start" : "end"}
                  dominantBaseline="central"
                  fontSize={12}
                  fontWeight="500"
                >
                  {data[index].name} ({(percent * 100).toFixed(0)}%)
                </text>
              );
            }}
            labelLine={{ strokeWidth: 1, stroke: "#ccc" }}
            stroke="#ffffff"
            strokeWidth={5}
            cornerRadius={10}
          >
            {data.map((entry, index) => (
              <Cell
                key={`cell-${index}`}
                fill={COLORS[index % COLORS.length]}
              />
            ))}
          </Pie>
        <ChartTooltip content={<ChartTooltipContent />} />
        <ChartLegend content={<ChartLegendContent />} />
      </PieChart>
    </ChartContainer>
  );
};

export default PieChartTemplate;
