# Use a Node.js base image
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the application code
COPY . .

# Build the application
RUN npm run build

# Use Node.js for production
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy the build artifacts and package files
COPY --from=builder /app/.next /app/.next
COPY --from=builder /app/public /app/public
COPY --from=builder /app/package*.json /app/

# Install production dependencies
RUN npm install --production

# Expose the port Next.js uses
EXPOSE 3000

# Start the Next.js server
CMD ["npm", "start"]
