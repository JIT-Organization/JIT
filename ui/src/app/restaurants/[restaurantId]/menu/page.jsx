import FoodCard from "../../../../components/food/FoodCard";
import { menuItems } from "@/data/menuitems";

const MenuList = () => {
    return (
      <div
        className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 px-4 overflow-x-auto"
        style={{
          scrollSnapType: "x mandatory", // Smooth scrolling for horizontal overflow
        }}
      >
        {menuItems.map((item) => (
          <div
            key={item.id}
            className="scroll-snap-center"
            style={{
              width: "18rem", // Fixed width for consistent layout
              flexShrink: 0,
            }}
          >
            <FoodCard {...item} />
          </div>
        ))}
      </div>
    );
  };
  
  export default MenuList;
  