FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --no-audit --no-fund
COPY . .
ARG REACT_APP_API_URL=
ENV REACT_APP_API_URL=$REACT_APP_API_URL
ARG REACT_APP_TMDB_API_KEY=
ENV REACT_APP_TMDB_API_KEY=$REACT_APP_TMDB_API_KEY
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
