"use client";
import PieChartTemplate from "@/components/customUIComponents/PieChartTemplate";
import BarGraphTemplate from "@/components/customUIComponents/BarGraphTemplate";
import LineGraphTemplate from "@/components/customUIComponents/LineGraphTemplate";
import CustomCard from "@/components/customUIComponents/CustomCard";
const sampleData = [
  { name: "Pizza", quantity: 4 },
  { name: "Burger", quantity: 2 },
  { name: "Sushi", quantity: 5 },
  { name: "Pasta", quantity: 3 },
  { name: "Biriyani", quantity: 20 },
];

const weeklyData = [
  { day: "Monday", apples: 12, bananas: 18, cherries: 25 },
  { day: "Tuesday", apples: 10, bananas: 20, cherries: 15 },
  { day: "Wednesday", apples: 15, bananas: 22, cherries: 19 },
  { day: "Thurdsay", apples: 12, bananas: 18, cherries: 25 },
  { day: "Friday", apples: 10, bananas: 20, cherries: 15 },
  { day: "Saturday", apples: 15, bananas: 22, cherries: 19 },
];

const incomeData = [
  { day: "Monday", cash: 300, online: 500, wallet: 100 },
  { day: "Tuesday", cash: 200, online: 450, wallet: 150 },
  { day: "Wednesday", cash: 350, online: 400, wallet: 200 },
  { day: "Thursday", cash: 300, online: 600, wallet: 180 },
  { day: "Friday", cash: 400, online: 700, wallet: 120 },
];

const sample =[
  { label: "Cash", value: "1,233" },
          { label: "Online", value: "12,222" },
          { label: "Wallet", value: "333,333" },
]

const sample2 =[
  { label: "Available", value: "15" },
          { label: "Used", value: "12" },
]

export default function DashboardPage() {
  return (
    <div className="p-4">
      <CustomCard title="Total Revenue" value="1233" data={sample} />
      <CustomCard title="Total Tables" value="27" data={sample2} />
      <h1 className="text-xl font-semibold mb-4">Food Quantity Distribution</h1>
      {/* 2x2 grid for charts */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-4">
        {/* Pie Chart */}
        <div className="w-full h-64">
          <PieChartTemplate data={sampleData} />
        </div>

        {/* Bar Graph */}
        <div className="flex w-full h-96">
          <BarGraphTemplate
            data={weeklyData}
            xKey="day"
            barKeys={["apples", "bananas", "cherries"]}
            config={{
              apples: { label: "Apples", color: "#FF6384" },
              bananas: { label: "Bananas", color: "#FFCE56" },
              cherries: { label: "Cherries", color: "#36A2EB" },
            }}
          />
        </div>

        {/* Line Graph 1 */}
        <div className="flex w-full h-96">
          <LineGraphTemplate
            data={incomeData}
            xKey="day"
            lineKeys={["cash", "online", "wallet"]}
          />
        </div>

        {/* Line Graph 2 */}
        <div className="flex w-full h-96">
          <LineGraphTemplate
            data={incomeData}
            xKey="day"
            lineKeys={["cash", "online", "wallet"]}
          />
        </div>
      </div>
    </div>
  );
}
