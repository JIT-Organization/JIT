import { Inter } from "next/font/google";
import "./globals.css";
import { Toaster } from "@/components/ui/toaster";
import TSQueryClientProvider from "@/components/providers/TSQueryClientProvider";
import { ColorProvider } from "@/components/providers/ColorProvider";

const inter = Inter({ subsets: ["latin"] });

export const metadata = {
  title: "Just In Time",
  description: "Your Food Your Way Right on Time",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ColorProvider>
          <TSQueryClientProvider>
            {children}
            <Toaster />
          </TSQueryClientProvider>
        </ColorProvider>
      </body>
    </html>
  );
}
