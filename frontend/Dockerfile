FROM docker.io/library/node:16 AS frontend
ARG VITE_API_URL
ENV VITE_API_URL $VITE_API_URL
ARG VITE_BASE_PATH
ENV VITE_BASE_PATH $VITE_BASE_PATH
WORKDIR /app/frontend
COPY package*.json ./
RUN npm install
COPY ./ ./
RUN npm run build
EXPOSE 8080
CMD ["npm", "run", "preview", "--", "--mode", "production", "--port", "8080"]