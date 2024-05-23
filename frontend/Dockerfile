# Build frontend stage
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

# Copy files into nginx image
FROM docker.io/library/nginx:alpine
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
ARG VITE_BASE_PATH
COPY --from=frontend /app/frontend/dist/ /usr/share/nginx/html${VITE_BASE_PATH}