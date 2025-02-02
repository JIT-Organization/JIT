import "./globals.css";
import TSQueryClientProvider from "../components/providers/TSQueryClientProvider";

export const metadata = {
  title: "Just In Time",
  description: "Your Food Your Way Right on Time",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className="antialiased">
        <TSQueryClientProvider>{children}</TSQueryClientProvider>
      </body>
    </html>
  );
}
