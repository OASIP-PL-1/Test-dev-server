# 1
# # build stage
# FROM node:lts-alpine as build-stage
# WORKDIR /app
# COPY package*.json ./
# RUN npm install
# COPY . .
# RUN npm run build

# # production stage
# FROM nginx:stable-alpine as production-stage
# COPY --from=build-stage /app/dist /usr/share/nginx/html
# EXPOSE 80
# CMD ["nginx", "-g", "daemon off;"]


#4
FROM node:17-alpine
WORKDIR /vue_app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD [ "npm", "run", "serve" ]
