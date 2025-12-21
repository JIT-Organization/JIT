/** @type {import('next').NextConfig} */
const nextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "images.pexels.com",
      },
      {
        protocol: "https",
        hostname: "example.com",
      },
    ],
  },
  async rewrites() {
    return [
      {
        source: '/jit-api/:path*',
        destination: `${process.env.BASE_URL}/jit-api/:path*`,
      },
    ];
  },
};

export default nextConfig;
