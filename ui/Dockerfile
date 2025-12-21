# Build stage
FROM node:18-alpine AS builder
WORKDIR /app

# Declare build args
ARG NEXT_PUBLIC_BASE_URL
ARG BASE_URL

# Export them as env vars for Next.js build
ENV NEXT_PUBLIC_BASE_URL=${NEXT_PUBLIC_BASE_URL}
ENV BASE_URL=${BASE_URL}

RUN echo "NEXT_PUBLIC_BASE_URL=$NEXT_PUBLIC_BASE_URL"
RUN echo "BASE_URL=$BASE_URL"

COPY package*.json ./
RUN npm install --frozen-lockfile
COPY . .

# Build Next.js (uses env vars)
RUN npm run build

# Runtime stage
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV PORT=8080

COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 8080
CMD ["npm", "start"]