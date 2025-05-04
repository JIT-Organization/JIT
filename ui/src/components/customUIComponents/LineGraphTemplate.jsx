import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    Legend,
    ResponsiveContainer,
    CartesianGrid,
    Customized,
  } from 'recharts';
  
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#a77ee6', '#f26419'];
  
  const CustomSquareDot = ({ cx, cy, stroke }) => (
    <rect x={cx - 4} y={cy - 4} width={8} height={8} fill={stroke} />
  );
  
  const CustomDotLabel = ({ x, y, value }) => {
    return (
      <text
        x={x}
        y={y - 10} // position above the dot
        fill="#333"
        fontSize={12}
        textAnchor="middle"
      >
        {value}
      </text>
    );
  };
  
  
  const LineGraphTemplate = ({ data, xKey, lineKeys }) => {
    // Compute highest and lowest indices for each line
    const minMaxIndices = lineKeys.map((key) => {
      let minIndex = 0;
      let maxIndex = 0;
      data.forEach((item, index) => {
        if (item[key] < data[minIndex][key]) minIndex = index;
        if (item[key] > data[maxIndex][key]) maxIndex = index;
      });
      return { key, minIndex, maxIndex };
    });
  
    return (
      <div className="w-full h-96">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ left: 20, right: 20 }}>
            {/* <CartesianGrid strokeDasharray="3 3" /> */}
            <XAxis
              dataKey={xKey}
              interval={0}
              padding={{ left: 80, right: 80 }} // Proper spacing before "Monday"
            />
            <YAxis />
            <Tooltip />
            <Legend />
  
            {lineKeys.map((key, i) => {
              const { minIndex, maxIndex } = minMaxIndices[i];
              return (
                <Line
                  key={key}
                  dataKey={key}
                  stroke={COLORS[i % COLORS.length]}
                  label={<CustomDotLabel />}
                  strokeWidth={2}
                  type="linear"
                  dot={(props) => {
                    const isExtreme = props.index === minIndex || props.index === maxIndex;
                    return isExtreme ? (
                      <CustomSquareDot {...props} stroke={COLORS[i % COLORS.length]} />
                    ) : (
                        <circle cx={props.cx} cy={props.cy} r={4} fill={COLORS[i % COLORS.length]} />
                      );
                  }}
                  activeDot={{ r: 6 }}
                />
              );
            })}
          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  };
  
  export default LineGraphTemplate;
  