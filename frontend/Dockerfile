FROM docker.io/library/node:16 AS frontend
WORKDIR /app/frontend
COPY package*.json ./
RUN npm install
COPY ./ ./
RUN npm run build
EXPOSE 8080
CMD ["npm", "run", "preview", "--", "--mode", "production", "--port", "8080"]