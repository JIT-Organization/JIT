

export default async function Restaurant({ params }) {
    // const router = useRouter();
    // const { restaurantId } = router.query;
    // const pathname = usePathname();
    // const restaurantId = pathname.split('/').pop();
    const { restaurantId } = await params;
  return (
    <div>
      <h2>
        Restaurant page for a particular restaurant identified by a restaurantId
      </h2>
      <p className="text-lg">You are viewing details for restaurant with ID: {restaurantId}</p>
    </div>
  )
}