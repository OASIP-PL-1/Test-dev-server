# วิธี run dev server (แบบมี Reverse Proxy แล้ว)
*ช่วงแรก* : ที่เข้า vm ให้ `ssh-keygen` แล้ว copy `id_rsa.pub` ใน `~/.ssh` ไปใส่ใน link git ที่จะ pull project 
``` 
git clone git@github.com:OASIP-PL-1/Test-dev-server.git
cd ./Test-dev-server/
``` 
### 1. run docker-compose
``` 
sudo docker-compose up -d
``` 
> รอ ... 5-10 นาที

- ถ้าจะเข้าไป check database container --> user:`root` , password:`123`
``` 
sudo docker exec -it mysqldb mysql -u root -p
``` 

- ถ้า error ให้ดู `logs` 
```
sudo docker logs mysqldb
sudo docker logs backend-app
```
> แล้วไปแก้ code ใน vscode แล้ว git push และ git pull ลง VM อีกรอบ
> และต้อง `docker-compose down` + ลบไฟล์ด้วย


### 2. stop docker-compose
```
sudo docker-compose down
``` 
> *ทำทุกคร้้ง!!! หลัง `docker-compose down`*>>  2.1 , 2.2 
#### 2.1. ถ้ามีการแก้ไข `script.sql` -> ต้องลบข้อมูลใน `Test-dev-server/db/data` เพื่อลบข้อมูลเก่าของ database
```
cd db
sudo rm -r data
mkdir data
ls data                 # data ต้องเป็น floder ว่าง
cd ../
```
#### 2.2. ถ้ามีการแก้ไข `code backend` -> ลบ image `test-project_backend-app` , `<none>` เพื่อข้อมูลเก่าของ backend
```
sudo docker images
sudo docker rmi [contianer_id1] [contianer_id2]
```
> ถ้าไม่ลบ image container จะใช้ไฟล์เดิม

#### 2.3. ถ้ามีการแก้ไข `code frontend` -> ลบ image `test-dev-server_frontend-app` , `<none>` เพื่อข้อมูลเก่าของ frontend
```
sudo docker images
sudo docker rmi [contianer_id1] [contianer_id2]
```
> ถ้าไม่ลบ image container จะใช้ไฟล์เดิม
---

## วิธีสร้าง docker-compose.yml + Dockerfile
```yml
version: '3'
services:
  
  mysqldb:
    container_name: mysqldb
    environment:
      MYSQL_ROOT_PASSWORD: '123'
    image: mysql
    ports:
    - 13306:3306
    volumes:
    - ./db/my.cnf:/etc/my.cnf
    - ./db/data:/var/lib/mysql
    - ./db/:/docker-entrypoint-initdb.d
    restart: on-failure

  backend-app:
    depends_on:
      - mysqldb
    build: ./Back-end
    restart: on-failure
    container_name: backend-app
    # ports:
    #   - 8080:8080
    environment:
      SPRING_APPLICATION_JSON: '{
        "spring.datasource.driver-class-name" : "com.mysql.cj.jdbc.Driver",
        "spring.datasource.username" : "dev",
        "spring.datasource.password" : "int221",
        "spring.datasource.url" : "jdbc:mysql://mysqldb:3306/bookingmodels?allowPublicKeyRetrieval=true&useSSL=false",
        "spring.jpa.hibernate.ddl-auto" : "update",
        "spring.jpa.properties.hibernate.dialect" : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "spring.data.rest.default-media-type" : "application/json",
        "spring.hateoas.use-hal-as-default-json-media-type" : "false"
      }'
    volumes:
      - .m2:/root/.m2
    stdin_open: true
    tty: true

  frontend-app:
    depends_on:
      - backend-app
    build: ./Front-end
    # ports:
    #   - 80:80
    container_name: frontend-app
    volumes:
      - ./nginx-frontend.conf:/etc/nginx/conf.d/default.conf

  proxy:
    container_name: reverse-proxy
    image: nginx
    restart: always
    depends_on:
      - frontend-app
    ports:
      - 80:80
    volumes:
      - "./nginx.conf:/etc/nginx/conf.d/default.conf:ro"
```    
      
### 1. สร้าง database container ใน docker-compose.yml
```yml
mysqldb:
    container_name: mysqldb
    environment:
      MYSQL_ROOT_PASSWORD: '123'
    image: mysql
    ports:
    - 13306:3306                             # กำหนด port ไว้ต่อผ่าน MySQL Workbench
    volumes:
    - ./db/my.cnf:/etc/my.cnf                # path ที่มี my.cnf
    - ./db/data:/var/lib/mysql               # path ของ data directory (ที่เก็บข้อมูล)
    - ./db/:/docker-entrypoint-initdb.d      # path ที่มี .sql ไฟล์ไว้รัน script            
``` 
* ใน `my.cnf` เพิ่ม `default-time-zone` ด้วยเพื่อกำหนด timezone ของ mysql container
``` 
`default-time-zone` = "Asia/Bangkok"
``` 
* ในไฟล์ `create-script-bookingmodels-v5.sql` มีการสร้าง database bookingmodels และมีการสร้าง user 2 user คือ dev, admin
```sql
-- DROP USER 'dev'@'%';
CREATE USER 'dev'@'%' IDENTIFIED BY 'int221';
GRANT CREATE, DROP, DELETE, INSERT, SELECT, UPDATE, REFERENCES, ALTER ON bookingmodels.* TO 'dev'@'%';

-- DROP USER 'admin'@'%';
CREATE USER 'admin'@'%' IDENTIFIED BY 'admin';
GRANT SELECT, UPDATE ON bookingmodels.eventCategories TO 'admin'@'%';
GRANT DELETE, INSERT, SELECT, UPDATE ON bookingmodels.events TO 'admin'@'%';
``` 
#### 1.1 การเข้าไปใช้งาน database 
- Host : ip21pl1.sit.kmutt.ac.th
- port : 13306
- username : root / dev / admin
- password : 123 / int221 / admin
- database : bookingmodels

### 2. สร้าง backend container ใน docker-compose.yml
```yml
  backend-app:
    depends_on:
      - mysqldb
    build: ./Back-end
    restart: on-failure
    container_name: backend-app
    # ports:
    #   - 8080:8080       # ถ้ามี reverse proxy ให้เอา port ออกได้
    environment:
      SPRING_APPLICATION_JSON: '{
        "spring.datasource.driver-class-name" : "com.mysql.cj.jdbc.Driver",
        "spring.datasource.username" : "dev",
        "spring.datasource.password" : "int221",
        "spring.datasource.url" : "jdbc:mysql://mysqldb:3306/bookingmodels?allowPublicKeyRetrieval=true&useSSL=false",
        "spring.jpa.hibernate.ddl-auto" : "update",
        "spring.jpa.properties.hibernate.dialect" : "org.hibernate.dialect.MySQL5InnoDBDialect",
        "spring.data.rest.default-media-type" : "application/json",
        "spring.hateoas.use-hal-as-default-json-media-type" : "false"
      }'
    volumes:
      - .m2:/root/.m2
    stdin_open: true   
    tty: true
    # stdin_open, tty = ให้เปิด terminal แสดงผลลัพธ์การรันด้วย
```
> กำหนด url, username, password ให้ตรงกับ user ที่สร้างไว้ใน database container mysqldb


#### 2.1 สร้าง `Dockerfile` ใน `./Back-end` เพื่อใช้ bulid image ก่อนสร้าง container
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

# -Duser.timezone=Asia/Bangkok 
# กำหนด Timezone ให้ ฺBackend แสดงเวลาเป็น ICT

```
*ช่วงแรก* : ติดปัญหา bulid บน server แล้วรันไม่ได้ เลยต้องมา test การ build project Backend บน local ก่อนอัพขึ้น server
- เปิด Terminal ใน vscode บนเครื่องตัวเอง แล้วเข้าไปที่ `Test-dev-server/Back-end/` 
- แล้วลอง run build ใน project backend เพื่อทดสอบก่อนว่า project เราสามารถ bulid บนเครื่อง local server ได้หรือไม่ ?
```
cd Test-dev-server/Back-end/
./mvnw package
mvn -f pom.xml clean package
```
> เมื่อรันแล้ว จะขึ้น `BUILD SUCCESS` และมีโฟลเดอร์ `target` ขึ้นมา (ควร bulid project ใหม่ทุกครั้งที่มีการแก้ไข code ใน backend) ก่อนอัพขึ้น dev server

- ถ้าใช้ `mvn` ไม่ได้ > ต้องไปติดตั้ง env เพิ่ม
    - เพิ่ม `java` ใน env กรอบล่าง New> JAVA_HOME เลือก C:\Program Files\java\jdk... *ต้องใช้ java version เดียวกับ pom.xml*
    - เพิ่ม `mvn` ใน env ตรง path กรอบล่าง C:\Program Files\apache-maven-3.8.5-bin\apache-maven-3.8.5\bin (dowload > https://maven.apache.org/)

#### 2.2 ตั้งค่า application.properties
- กำหนด timezone ให้ backend เป็น `Asia/Bangkok`
```conf
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto= update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.data.rest.default-media-type=application/json
spring.hateoas.use-hal-as-default-json-media-type=false
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Bangkok
spring.jackson.time-zone=Asia/Bangkok
```

#### 2.3 เอา CrossOrigin ออกจาก Controller
- ถ้ามี Reverse Proxy แล้ว
```java
// @CrossOrigin(origins = "*")
```

### 3. สร้าง frontend container ใน docker-compose.yml
```yml
  frontend-app:
    depends_on:
      - backend-app  
    build: ./Front-end
    # ports:
    #   - 80:80            # ถ้ามี reverse proxy ให้เอา port ออกได้
    container_name: frontend-app
    volumes:
      - ./nginx-frontend.conf:/etc/nginx/conf.d/default.conf  #ใช้ไฟล์ nginx-frontend ที่กำหนดค่าไว้
```

#### 3.1 สร้าง `Dockerfile` ใน `.Front-end`
- อันนี้จะเป็นการรัน Vue.js บน nginx เพราะเสถียรกว่า
```Dockerfile
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

#### 3.2 config vite base ในไฟล์ `package.json`
- กำหนดให้มี `/pl1/` เป็น url base ของ project frontend
```json
    "build": "vite build --base=/pl1/",
```

#### 3.3 แก้ไฟล์ `.env` , `.env.production` ใน `Front-end`
- ต้องเปลี่ยนเลขเป็นเลข ip บนเครื่อง VM
```javascript
//ถ้ามี reverse proxy
VITE_BASE_URL=/pl1/api

//เดิมไม่มี reverse proxy
VITE_BASE_URL=http://202.44.9.103:8080/pl1/api
```

#### 3.4 แก้ไฟล์ `vite.config.js` ใน `.Front-end`
- ลบ proxy ใน frontend ออก เพราะ proxy นี้จะใช้ได้เฉพาะตอนรัน `npm run dev` แต่ตอนรันบน server เป็น `npm run build` แทน
- แล้วเปลี่ยนไปใช้ container Reverse Proxy ช่วย fetch ข้อมูลจาก Backend ให้แทน
```js
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
    base: '/pl1/'
    ,
    server: {
        // proxy: {
        //     "^/api": {
        //         target: "http://ip21pl1.sit.kmutt.ac.th/api",
        //         changeOrigin: true,
        //         secure: false,
        //         rewrite: (path) => path.replace(/^\/api/, ''),
        //         ws: true,
        //     }
        // },
        port : 80
    }
})
```

#### 3.5 แก้ปัญหา Error 404 Not Found Nginx 
- สร้างไฟล์ `nginx-forntend.conf` เพื่อใช้กำหนดค่า config ของ niginx ใน nignx ที่ใช้รัน frontend
- แล้วเขียน `try_files $uri $uri/ /index.html;` เพื่อให้ refresh แล้วกลับมาลิ้ง url เดิมได้
```conf
server {
    listen       80;
    listen  [::]:80;
    server_name  localhost;

    location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

### 4. reverse Proxy 
```yml
  proxy:
    container_name: reverse-proxy
    image: nginx
    restart: always
    depends_on:
      - frontend-app
    ports:
      - 80:80
    volumes:
      - "./nginx.conf:/etc/nginx/conf.d/default.conf:ro"
```

### 4.1 `nginx.conf` กำหนดการตั้งค่าของ reverse Proxy 
- กำหนด url ให้ตรงกับ port ภายใน container ของ frontend และ backend
```conf 
    upstream frontend-server {
        server frontend-app:80 ;
    }

    upstream backend-server {
        server backend-app:8080 ;
    }

    server {
        listen 80;
        listen [::]:80;
        server_name localhost;
        

        location / {
            proxy_pass http://frontend-server;
        }

        location /api {
            proxy_pass http://backend-server;
        }
        
    }
```

### Reference
- วิธีสร้าง container database-backend  ให้เชื่อมต่อกันได้ : https://www.bezkoder.com/docker-compose-spring-boot-mysql/
- วิธีสร้าง container frontend : https://v2.vuejs.org/v2/cookbook/dockerize-vuejs-app.html
- แก้ปัญหา refresh หน้าเว็บไม่ได้ nginx https://stackoverflow.com/questions/17798457/how-can-i-make-this-try-files-directive-work
