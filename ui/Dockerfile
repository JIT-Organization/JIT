# Use a Node.js base image
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all files
COPY . .

# Build the Next.js app
RUN npm run dev

# Use a lightweight web server for serving the build
FROM nginx:stable-alpine

# Copy the built app to the web server
COPY --from=builder /app/.next /usr/share/nginx/html

# Expose port
EXPOSE 80

# Start the web server
CMD ["nginx", "-g", "daemon off;"]
