# วิธี run dev server
*ช่วงแรก* : ที่เข้า vm ให้ `ssh-keygen` แล้ว copy `id_rsa.pub` ใน `~/.ssh` ไปใส่ใน link git ที่จะ pull project 
``` 
git clone https://github.com/OASIP-PL-1/Test-dev-server
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
### 1. สร้าง database container ใน docker-compose.yml
```yml
mysqldb:
    container_name: mysqldb
    environment:
      MYSQL_ROOT_PASSWORD: '123'
    image: mysql
    ports:
    - 13306:3306
    volumes:
    - ./db/my.cnf:/etc/my.cnf                # path ที่มี my.cnf
    - ./db/data:/var/lib/mysql               # path ของ data directory (ที่เก็บข้อมูล)
    - ./db/:/docker-entrypoint-initdb.d      # path ที่มี .sql ไฟล์ไว้รัน script            
``` 

### 2. สร้าง `Dockerfile` ใน `./Back-end` เพื่อใช้ bulid image ก่อนสร้าง container
```dockerfile
FROM maven:3.8.5-jdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -Dmaven.test.skip package    
# ต้อง run หลังจากใช้ mvn package แล้ว 

FROM openjdk:11-jdk-slim
ARG JAR_FILE=/app/target/*.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```
*ช่วงแรก* : ถ้าไม่มี `target` floder ต้อง build maven ก่อน แล้วค่อย up ขึ้น server (เพราะลอง bulid บน server แล้วรันไม่ได้)
- เปิด Terminal ใน vscode บนเครื่องตัวเอง แล้วเข้าไปที่ `Test-dev-server/Back-end/` แล้ว run build ใน project backend
```
cd Test-dev-server/Back-end/
./mvnw package
mvn -Dmaven.test.skip package
```
> เมื่อรันแล้ว จะขึ้น `BUILD SUCCESS` และมีโฟลเดอร์ `target` ขึ้นมา (ควร bulid project ใหม่ทุกครั้งที่มีการแก้ไข code ใน backend)

- ถ้าใช้ `mvn` ไม่ได้ > ต้องไปติดตั้ง env เพิ่ม
    - เพิ่ม `java` ใน env กรอบล่าง New> JAVA_HOME เลือก C:\Program Files\java\jdk... *ต้องใช้ java version เดียวกับ pom.xml*
    - เพิ่ม `mvn` ใน env ตรง path กรอบล่าง C:\Program Files\apache-maven-3.8.5-bin\apache-maven-3.8.5\bin (dowload > https://maven.apache.org/)

### 3. สร้าง backend container ใน docker-compose.yml
```yml
backend-app:
    depends_on:
      - mysqldb
    build: ./Back-end
    restart: on-failure
    container_name: backend-app
    ports:
      - 8080:8080
    environment:
      SPRING_APPLICATION_JSON: '{
        "spring.datasource.driver-class-name" : "com.mysql.cj.jdbc.Driver",
        "spring.datasource.username" : "root",
        "spring.datasource.password" : "123",
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
volumes:
  db:
```
> ไม่ต้องแก้อะไร `application.properties` ใน project backend เพราะ `docker-compose.yml` จะ set ให้ใหม่ 


### 4. สร้าง `Dockerfile` ใน `.Front-end`
- อันนี้จะเป็นการรัน Vue.js บน nginx
```Dockerfile
# build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```
### 5. สร้าง frontend container ใน docker-compose.yml
```yml
frontend-app:
    depends_on:
      - backend-app
    build: ./Front-end
    restart: on-failure
    ports:
      - 3000:80
    container_name: frontend-app
```
### 6. แก้ไฟล์ `.env` ในโฟลเดอร์ frontend
- ต้องเปลี่ยนเลขเป็นเลข ip บนเครื่อง VM
```
VITE_BASE_URL=http://10.4.84.106:8080/api
```
