// "use client";

// import * as React from "react";
// import {
//   HoverCard,
//   HoverCardTrigger,
//   HoverCardContent,
// } from "../ui/hover-card";
// import { FaIndianRupeeSign } from "react-icons/fa6";

// const CustomCard = () => {
//   return (
//     <div className="inline-block relative group h-32 overflow-hidden">
//       <HoverCard openDelay={0}>
//         <div className="flex h-full items-stretch">
//           {/* Trigger */}
//           <HoverCardTrigger asChild>
//             <div
//               className={`
//                 h-full inline-flex flex-col justify-center items-center
//                 p-4 border rounded-md rounded-r-none bg-white shadow-sm
//                 cursor-pointer 
//                 group-hover:border-r-0 
//                 transition-all duration-200 ease-in-out
//               `}
//             >
//               <p className="text-lg font-medium whitespace-nowrap text-center">
//                 Total Revenue
//               </p>
//               <div className="flex items-center justify-center gap-1 mt-3">
//                 <FaIndianRupeeSign className="text-2xl" />
//                 <p className="text-xl font-semibold whitespace-nowrap text-center">
//                   12,55,300
//                 </p>
//               </div>
//             </div>
//           </HoverCardTrigger>

//           {/* Separator (also grows in) */}
//           <div
//             className={`
//               bg-gray-300 self-center 
//               transition-all duration-200 ease-in-out
//               w-0 group-hover:w-px
//             `}
//           />

//           {/* Content (scaled from left) */}
//           <HoverCardContent
//             side="right"
//             className={`
//               h-32 p-4 border border-l border-l-transparent
//               rounded-md rounded-l-none bg-white shadow-sm
              
//               /* start collapsed */
//               transform origin-left scale-x-0 opacity-0
              
//               /* expand on hover */
//               group-hover:border-l
//               group-hover:scale-x-100 group-hover:opacity-100
              
//               transition-all duration-200 ease-in-out
//               flex flex-col justify-center gap-1
//             `}
//           >
//             <div className="grid grid-cols-2 text-lg w-full gap-x-2">
//               <p>Cash</p>
//               <p className="text-right">1,233</p>
//             </div>
//             <div className="grid grid-cols-2 text-lg w-full gap-x-2">
//               <p>Online</p>
//               <p className="text-right">12,222</p>
//             </div>
//             <div className="grid grid-cols-2 text-lg w-full gap-x-2">
//               <p>Wallet</p>
//               <p className="text-right">3,333,333,333</p>
//             </div>
//           </HoverCardContent>
//         </div>
//       </HoverCard>
//     </div>
//   );
// };

  

// export default CustomCard;

"use client";

import * as React from "react";
import { FaIndianRupeeSign } from "react-icons/fa6";

const CustomCard = ({title, value, data}) => {
  return (
    <div
className="inline-flex group w-fit h-28 border border-gray-500 rounded-md bg-white shadow-lg overflow-hidden transition-all"
    >
      {/* HOVERABLE AREA */}
      <div
        className="
          flex flex-col justify-center
          px-4 py-3
          border-r-0 group-hover:border-r-0
          transition-all duration-500 ease-in-out
          ml-8
        "
      >
        <p className="justify-center text-sm font-medium whitespace-nowrap">{title}</p>
        <div className="flex items-center gap-1 mt-2">
          <FaIndianRupeeSign
        className="text-2xl group-hover:text-xl transition-all duration-200 ease-in-out"
      />
      <p
        className="text-xl font-semibold whitespace-nowrap text-center
        group-hover:text-lg transition-all duration-200 ease-in-out"
      >
        {value}
      </p>

        </div>
      </div>

      {/* HOVERED CONTENT */}
      <div
        className={`
          h-auto w-0 overflow-hidden
          opacity-0 scale-x-0
          group-hover:w-fit group-hover:opacity-100 group-hover:scale-x-100
          origin-left transition-all duration-500 ease-in-out
          flex flex-col justify-center gap-1 px-3 py-3
        `}
      >
        {data.map((item, idx) => (
          <div
            key={idx}
            className="flex justify-between text-sm whitespace-nowrap w-full"
          >
            <p className="text-gray-700">{item.label}</p>
            <p className="text-gray-900 font-medium ml-4">{item.value}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CustomCard;



