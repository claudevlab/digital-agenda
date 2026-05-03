# frontend/Dockerfile

# Stage 1: Build Angular
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY . .
RUN npm run build -- --configuration=production

# Stage 2: Nginx serve i file statici
FROM nginx:1.27-alpine AS runtime
COPY --from=builder /app/dist/*/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80