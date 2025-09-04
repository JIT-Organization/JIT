'use client'

import React from 'react'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  LabelList,
  ResponsiveContainer,
  Tooltip,
  Legend,
} from 'recharts'

import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  ChartLegend,
  ChartLegendContent,
} from '@/components/ui/chart'

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#a77ee6', '#f26419']

const BarGraphTemplate = ({ data, xKey, barKeys, config = {} }) => {
  return (
    <ChartContainer config={config} className="w-full h-96">
      <BarChart
        data={data}
        barCategoryGap={16}
        margin={{ top: 16, right: 16, bottom: 32, left: 16 }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey={xKey} />
        <YAxis />
        <Tooltip content={<ChartTooltipContent />} />
        <Legend content={<ChartLegendContent />} />
        {barKeys.map((key, index) => (
          <Bar
            key={key}
            dataKey={key}
            fill={config[key]?.color || COLORS[index % COLORS.length]}
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
  )
}

export default BarGraphTemplate
