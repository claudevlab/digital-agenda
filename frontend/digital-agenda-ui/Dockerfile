# Stage 1: Build Angular
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./

# Rimosso --omit=dev! Ci servono le devDependencies per buildare Angular!
RUN npm ci 

COPY . .
RUN npm run build -- --configuration=production

# Rimuovi eventuali file di default di nginx
RUN rm -rf /usr/share/nginx/html/*


# Stage 2: Nginx serve i file statici
FROM nginx:1.27-alpine AS runtime
# Trucco Bash: Troviamo l'index.html ovunque esso sia (in browser o direttamente nella root)
# E lo copiamo, insieme a tutto il resto, nella cartella di nginx
COPY --from=builder /app/dist/ /dist_temp/
RUN find /dist_temp -type d -name "browser" -exec cp -r {}/* /usr/share/nginx/html/ \; || cp -r /dist_temp/*/* /usr/share/nginx/html/

COPY nginx/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80