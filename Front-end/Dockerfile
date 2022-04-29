# ### STAGE 1: Build ###
# FROM node:12.7-alpine AS build
# WORKDIR /usr/src/app
# COPY package.json ./
# RUN npm install
# COPY . .
# RUN npm run build

# ### STAGE 2: Run ###
# FROM nginx:1.17.1-alpine
# COPY default.conf /etc/nginx/conf.d/default.conf
# COPY --from=build /usr/src/app/dist/kanban-ui /usr/share/nginx/html
# EXPOSE 80

# FROM node:18-alpine3.14 as build-stage
# WORKDIR /app
# COPY package*.json ./
# RUN npm install
# COPY . .
# RUN npm run build