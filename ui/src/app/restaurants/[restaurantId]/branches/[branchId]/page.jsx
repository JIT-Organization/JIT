import Components from "@/components/Components";
//import { useRouter } from 'next/router';
//import { usePathname } from 'next/navigation';

export default async function Branch({ params }) {
    // const router = useRouter();
    // const { restaurantId } = router.query;
    // const pathname = usePathname();
    // const restaurantId = pathname.split('/').pop();
    const { branchId } = await params;
  return (
    <div>
      <h2>
        Brnach page for a particular restaurant identified by a branchid
      </h2>
      <p className="text-lg">You are viewing details for branch with ID: {branchId}</p>
    </div>
  )
}