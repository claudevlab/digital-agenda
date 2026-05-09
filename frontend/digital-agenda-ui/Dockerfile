# Stage 1: Build Angular
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci 
COPY . .
RUN npm run build -- --configuration=production

# Stage 2: Nginx serve i file statici
FROM nginx:1.27-alpine AS runtime

# Svuota i file di Nginx preesistenti
RUN rm -rf /usr/share/nginx/html/*

# Copia i file compilati. 
COPY --from=builder /app/dist/digital-agenda-ui/browser /usr/share/nginx/html/

COPY nginx/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80