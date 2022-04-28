use bookingmodels;
insert  into eventcategories(eventCategoryId,eventCategoryName,eventCategoryDescription,eventCategoryDuration) 
values 
(1,'Project Management Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย project management clinic ในวิชา INT221 integrated project I ให้นักศึกษาเตรียมเอกสารที่เกี่ยวข้อง เพื่อแสดงระหว่างขอคำปรึกษา',30),
(2,'DevOps/Infra Clinic','Use this event category for DevOps/Infra clinic.',30),
(3,'Database Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย database clinic ในวิชา INT221 integrated project I',15),
(4,'Client-side Clinic','ตารางนัดหมายนี้ใช้สำหรับนัดหมาย client-side clinic ในวิชา INT221 integrated project I',30),
(5,'Server-side Clinic',null,30);


insert  into events(eventId,bookingName,bookingEmail,eventStartTime,eventDuration,eventNotes,eventCategoryId) 
values 
(1,'Somchai Jaidee (OR-7)','somchai.jai@mail.kmutt.ac.th','2022-05-23 13:30:00',(select eventCategoryDuration from eventcategories where eventCategoryId=2),null,2),
(2,'Somsri Rakdee (SJ-3)','somsri.rak@mail.kmutt.ac.th','2022-04-27 09:30:00',(select eventCategoryDuration from eventcategories where eventCategoryId=1),'ขอปรึกษาปัญหาเพื่อนไม่ช่วยงาน',1),
(3,'สมเกียรติ ขยันเรียน กลุ่ม TT-4','somkiat.kay@kmutt.ac.th','2022-05-23 16:30:00',(select eventCategoryDuration from eventcategories where eventCategoryId=3),null,3);


update eventcategories set eventCategoryDuration = 20 where eventCategoryId=2;
select * from eventcategories;
select * from events;
