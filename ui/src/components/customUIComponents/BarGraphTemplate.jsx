'use client';

import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  CartesianGrid,
  LabelList,
} from 'recharts';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#a77ee6', '#f26419'];

const BarGraphTemplate = ({ data, xKey, barKeys }) => {
  return (
    <div className="w-full h-96">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
          {/* <CartesianGrid strokeDasharray="3 3" /> */}
          <XAxis dataKey={xKey} />
          <YAxis />
          <Tooltip />
          <Legend />
          {barKeys.map((key, index) => (
            <Bar
              key={key}
              dataKey={key}
              fill={COLORS[index % COLORS.length]}
              radius={[4, 4, 0, 0]} // top corners rounded
              barSize={20}
              >
                <LabelList 
                dataKey={key} 
                position="center"  // Position label at the center of the bar
                fill="#fff"        // Text color
                fontSize={14}      // Font size
                fontWeight="bold"  // Font weight
              />
            
            </Bar>
            
          ))}
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default BarGraphTemplate;
