# Use a Node.js base image
FROM node:18-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all files
COPY . .

# Build the Next.js app
RUN npm run build

# Use Node.js for serving the Next.js app
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy only the necessary files
COPY --from=builder /app/.next /app/.next
COPY --from=builder /app/package*.json /app/

# Install production dependencies only
RUN npm install --production

# Expose the port
EXPOSE 3000

# Start the Next.js app
CMD ["npm", "start"]
