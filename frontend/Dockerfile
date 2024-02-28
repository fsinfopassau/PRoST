FROM node:16 AS frontend
ARG VITE_API_URL
ENV VITE_API_URL $VITE_API_URL
WORKDIR /app/frontend
COPY package*.json ./
RUN npm install
COPY ./ ./
EXPOSE 8080
CMD ["npm","run","dev"]