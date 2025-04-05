import PageProvider from "@/components/providers/PageProvider";
import { getMenuItemListOptions } from "@/lib/api/api";

export default async function MenuLayout({ children }) {
  return (
    <PageProvider queryOptions={getMenuItemListOptions()}>
      {children}
    </PageProvider>
  );
}
