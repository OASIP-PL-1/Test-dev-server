# วิธี run dev server

### run docker-compose
``` 
git clone https://github.com/OASIP-PL-1/Test-dev-server
cd ./Test-dev-server/

sudo docker-compose up -d
``` 
//รอ ... 5-10 นาที
- ถ้า error ให้ดู `logs` 
```
sudo docker logs mysqldb
sudo docker logs backend-app
```

### stop docker-compose
```
sudo docker-compose down
``` 
** ถ้า down แล้ว **
1. ต้องลบข้อมูลใน Test-dev-server/db/data
```
cd /db
sudo rm -r data
mkdir data
ls data 
//data ต้องเป็น floder ว่าง
cd ../
```
2. และลบ image `test-project_backend-app` , `none` 
```
sudo docker rmi [contianer_id1] [contianer_id2]
sudo docker images
```
//ถ้าไม่ลบ container จะใช้ไฟล์เดิม
ให้แก้ code ใน vscode แล้ว git push และ git pull ลง VM อีกรอบ
---

## docker-compose.yml
1. สร้าง database container
- 
```yml
mysqldb:
    container_name: mysqldb
    environment:
      MYSQL_ROOT_PASSWORD: '123'
    image: mysql
    ports:
    - 13306:3306
    volumes:
    - /home/student209/test-project/db/my.cnf:/etc/my.cnf             # path ที่มี my.cnf
    - /home/student209/test-project/db/data:/var/lib/mysql            # path ของ data directory (ที่เก็บข้อมูล)
    - /home/student209/test-project/db/:/docker-entrypoint-initdb.d   # path ที่มี .sql ไฟล์ไว้รัน script
``` 
- /home/student209 ต้องเปลี่ยนเป็น path ~ ของ VM

* เข้าไป check database container --> user:`root` , password:`123`
``` 
sudo docker exec -it mysqldb mysql -u root -p
``` 

2. สร้าง backend container 

*ช่วงแรก ลอง build maven ก่อน แล้วค่อย up*
- run build ใน project backend บนเครื่องตัวเอง
- เพิ่ม java ใน env กรอบล่าง New> JAVA_HOME เลือก  C:\Program Files\java\jdk... *ต้องใช้ java version เดียวกับ pom.xml*
- เพิ่ม mvn ใน env ตรง path กรอบล่าง C:\Program Files\apache-maven-3.8.5-bin\apache-maven-3.8.5\bin
//รันบน Terminal ใน vscode บนเครื่องตัวเอง 
```
cd /test 
.\mvnw package
mvn -f pom.xml clean package 
```
- เมื่อ bulid แล้ว จะมี target เพิ่มมา
เปลี่ยน Dockerfile ใน ./Backend --> RUN mvn -Dmaven.test.skip package

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
ENTRYPOINT ["java","-jar","app.jar"]
```

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
- ไม่ต้องแก้ application.properties