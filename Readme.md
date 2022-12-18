# infrastructure 

|   | git branch |
| ---------- | --------- |
| Frontend  | ui  |
| Backend  | main |
| Database   | main  |
| Test-dev-server  | new |

- [x] Reverse Proxy
- [x] Implement HTTPS

1. ## วิธี run dev server
   1.1 กรณีติดตั้งโปรเจค บน VM เครื่องใหม่
      - เมื่อ login เข้า vm ให้สร้าง `ssh-keygen` แล้ว copy `id_rsa.pub` ใน `~/.ssh` ไปใส่ใน link git hub ที่จะ pull project 
    ``` 
    git clone git@github.com:OASIP-PL-1/Test-dev-server.git
    cd ./Test-dev-server/
    ``` 
    1.2 กรณีที่ทำ project ต่อ
      - check ดูว่า project ที่ pull มาเป็น version ล่าสุดหรือยัง?
    ``` 
    git pull origin new
    git log
    ``` 
      - run docker-compose
    ```conf 
    ##run container
    sudo docker-compose up -d

    ##stop run container
    sudo docker-compose down

    ##restart container
    sudo docker-compose restart
    ``` 

2. ## วิธี check container
    ```
    ##view container status and name 
    sudo docker ps -a

    ##login database in VM (user: root, password: 123)
    sudo docker exec -it mysqldb mysql -u root -p

    ##view log
    sudo docker logs backend-app
    sudo docker logs frontend-app
    sudo docker logs reverse-proxy
    ``` 
3. ## วิธีเคลียไฟล์ใน VM
   3.1 ลบ docker images ทุกครั้งที่แก้ source code 
   ``` 
    sudo docker images
    sudo docker rmi [container_id1] [container_id2] ...
   ``` 
   3.2 ลบ event attachment file ทุกครั้งที่ reset ข้อมูลใหม่
   ``` 
    ls /public/fileStorage/eventAttachment/
    cd /public/fileStorage/
    sudo rm r- eventAttachment
    sudo mkdir eventAttachment
   ``` 
   3.3 ลบข้อมูลที่เก็บไว้ใน VM ทุกครั้งที่ reset ข้อมูลใหม่ใน database  
   ``` 
    ls db/data
    cd db
    sudo rm r- data
    mkdir data
   ``` 
4. ## Dockerfile และ ไฟล์เกี่ยวข้องกับ docker-compose
* docker file backend
```dockerfile
FROM maven:3.8.5-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -Dmaven.test.skip package

FROM openjdk:11-jdk-slim
ARG JAR_FILE=/app/target/*.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Duser.timezone=Asia/Bangkok","-jar","app.jar"]
 
# -Dmaven.test.skip
# build package ของ maven ให้เป็นไฟล์เล็กๆ และข้ามการ test

# -Duser.timezone=Asia/Bangkok 
# กำหนด Timezone ให้ ฺBackend แสดงเวลาเป็น ICT

```
* docker file frontend
```dockerfile
# build stage
FROM node:lts-alpine as build-stage
WORKDIR /vue_app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /vue_app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

```
* `nginx-frontend.conf`
```conf
server {
    client_max_body_size 15m;
    listen       80;
    listen  [::]:80;
    server_name  localhost;

    location / {
        client_max_body_size 15m;
        root   /usr/share/nginx/html;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;  #ใส่เพื่อให้ refresh แล้วกลับมาลิ้ง url เดิมได้ แก้ปัญหา Error 404 Not Found Nginx 
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        client_max_body_size 15m;
        root   /usr/share/nginx/html;
    }
}
```

5. ## การทำ Reverse Prory และ HTTPS server
* docker-compose
```yml
version: '3'
services:
  
  mysqldb:
    container_name: mysqldb
    environment:
      MYSQL_ROOT_PASSWORD: '123'
    image: mysql
    ports:
    - 13306:3306      # กำหนด port ไว้ต่อผ่าน MySQL Workbench
    volumes:
    - ./db/my.cnf:/etc/my.cnf    # path ที่มี my.cnf
    - ./db/data:/var/lib/mysql    # path ของ data directory (ที่เก็บข้อมูล)
    - ./db/:/docker-entrypoint-initdb.d   # path ที่มี .sql ไฟล์ไว้รัน script import data 
    restart: on-failure

  backend-app:
    depends_on:
      - mysqldb
    build: ./Back-end
    restart: on-failure
    container_name: backend-app
    # ports:
    #   - 8080:8080     # ถ้ามี reverse proxy ให้เอา port ออกได้
    environment:
      SPRING_APPLICATION_JSON: '{
        "spring.datasource.driver-class-name" : "com.mysql.cj.jdbc.Driver",
        "spring.datasource.username" : "dev",
        "spring.datasource.password" : "int221",
        "spring.datasource.url" : "jdbc:mysql://mysqldb:3306/bookingmodels?allowPublicKeyRetrieval=true&useSSL=false",
        "spring.jpa.hibernate.ddl-auto" : "update",
        "spring.jpa.properties.hibernate.dialect" : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "spring.jpa.database-platform" : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "spring.jpa.generate-ddl":"true",
        "spring.data.rest.default-media-type" : "application/json",
        "spring.hateoas.use-hal-as-default-json-media-type" : "false",
        "spring.security.user.password" : "1234",
        "spring.security.filter.order" : "10",

        "jwt.secret" : "secret",

        "spring.mail.host" : "smtp.gmail.com",
        "spring.mail.port" : "587",
        "spring.mail.username":"piraya.cartoony@mail.kmutt.ac.th",
        "spring.mail.password" : "aiubqiprshldbqej",
        "spring.mail.properties.mail.smtp.auth" : "true",
        "spring.mail.properties.mail.smtp.starttls.enable" : "true",

        "spring.servlet.multipart.enabled" : "true",
        "spring.servlet.multipart.file-size-threshold" : "4KB",
        "spring.servlet.multipart.max-file-size" : "10MB",
        "spring.servlet.multipart.max-request-size" : "15MB",
        "file.uploaddir" : "/public/fileStorage"
      }'
    volumes:
      - .m2:/root/.m2
      - /public/fileStorage:/public/fileStorage
    stdin_open: true
    tty: true

  frontend-app:
    depends_on:
      - backend-app
    build: ./Front-end
    # ports:
    #   - 80:80     # ถ้ามี reverse proxy ให้เอา port ออกได้
    container_name: frontend-app
    volumes:
      - ./nginx-frontend.conf:/etc/nginx/conf.d/default.conf

  proxy:
    container_name: reverse-proxy
    image: nginx:stable-alpine
    # restart: always
    restart: unless-stopped
    depends_on:
      - frontend-app
    ports:
      - 443:443
      - 80:80
    volumes:
      - ./dohttps/nginx/config/conf.d/default.conf:/etc/nginx/conf.d/default.conf:rw
      - ./dohttps/nginx/ssl:/etc/ssl/:rw

```
- เพิ่ม `ip21pl1.crt` , `ip21pl1.key` ใน ./dohttps/nginx/ssl
* `default.conf` (Reverse Proxy + Implement HTTPS)
```conf
server{ #port 80
    client_max_body_size 15m;
        listen 80;
        rewrite ^/(.*)$ https://$host/pl1/$1 permanent;
}
server{ #port 443
    client_max_body_size 15m;
        listen 443 ssl;
        server_name localhost;
        ssl_certificate /etc/ssl/ip21pl1.crt;   #The crt file storage path of nginx for ssl certificate of domain name application
        ssl_certificate_key /etc/ssl/ip21pl1.key;      #Storage path of nginx key file of ssl certificate for domain name application

        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;

     # Specify the password as a format supported by openssl
        ssl_protocols TLSv1.2;

        ssl_ciphers  HIGH:!aNULL:!MD5;  # Password encryption method
        ssl_prefer_server_ciphers  on;
        location / {
            proxy_pass http://frontend-server;
        }

        location /api {
            proxy_pass http://backend-server;
        }
}
upstream frontend-server {
        server frontend-app:80 ;
    }

upstream backend-server {
        server backend-app:8080 ;
    }

```
### Reference
* INT221 Implement HTTPS by Sumate Maneesart
* nginx - client_max_body_size - https://stackoverflow.com/questions/2056124/nginx-client-max-body-size-has-no-effect

