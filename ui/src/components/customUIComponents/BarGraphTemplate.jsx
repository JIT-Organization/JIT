'use client';

import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  LabelList,
} from 'recharts';

import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  ChartLegend,
  ChartLegendContent,
} from '@/components/ui/chart';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#a77ee6', '#f26419'];

const BarGraphTemplate = ({ data, xKey, barKeys, config = {} }) => {
  return (
    <ChartContainer config={config} className="w-full h-96">
      <BarChart data={data} barCategoryGap={16}>
        <XAxis dataKey={xKey} />
        <YAxis />
        <ChartTooltip content={<ChartTooltipContent />} />
        <ChartLegend content={<ChartLegendContent />} />
        {barKeys.map((key, index) => (
          <Bar
            key={key}
            dataKey={key}
            fill={COLORS[index % COLORS.length]}
            radius={[4, 4, 0, 0]}
            barSize={20}
          >
            <LabelList
              dataKey={key}
              position="center"
              fill="#fff"
              fontSize={14}
              fontWeight="bold"
            />
          </Bar>
        ))}
      </BarChart>
    </ChartContainer>
  );
};

export default BarGraphTemplate;
