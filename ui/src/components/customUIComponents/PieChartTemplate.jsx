"use client";
import React from "react";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = [
  "#0088FE",
  "#00C49F",
  "#FFBB28",
  "#FF8042",
  "#a77ee6",
  "#f26419",
];

const PieChartTemplate = ({ data }) => {
  return (
    <div className="w-full h-64">
      <ResponsiveContainer width="100%" height="100%">
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
          <Tooltip />
          <Legend
            layout="horizontal"
            align="center"
            verticalAlign="bottom"
            iconType="square"
            wrapperStyle={{ fontSize: 12, color: "#444" }}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

export default PieChartTemplate;
