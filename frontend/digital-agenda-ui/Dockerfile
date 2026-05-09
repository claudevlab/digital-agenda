# Stage 1: Build Angular
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./

# Rimosso --omit=dev! Ci servono le devDependencies per buildare Angular!
RUN npm ci 

COPY . .
RUN npm run build -- --configuration=production

# Stage 2: Nginx serve i file statici
FROM nginx:1.27-alpine AS runtime
# Il jolly /*/browser è un ottimo trucco per le nuove versioni di Angular 17+
COPY --from=builder /app/dist/*/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80