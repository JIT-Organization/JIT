import Components from "@/components/Components";

export default async function Branches({ params }) {
    const { restaurantId } = await params;
  return (
    <div>
      <h2>
        ALL branches page for any restaurant {restaurantId}
      </h2>

    </div>
  )
}