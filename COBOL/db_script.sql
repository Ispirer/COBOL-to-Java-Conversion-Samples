create table PROD 
(
   PROD_ID NUMBER(6),
   PROD_NM VARCHAR2(10),
   PROD_GR CHAR(3)
)
/
insert into PROD values (1001,'T544GT    ','DCX');
insert into PROD values (1002,'K554SS    ','YAX');
insert into PROD values (1003,'K201BL    ','YAX');
insert into PROD values (1004,'F261IF    ','DCX');
insert into PROD values (1005,'R102AM    ','IMX');
insert into PROD values (1006,'F642IT    ','DCX');
insert into PROD values (1007,'K786SY    ','YAX');
insert into PROD values (1008,'F578VA    ','DCX');
insert into PROD values (1009,'T554YA    ','DCX');
insert into PROD values (1010,'R300TB    ','IMX');
/
create table PROD_PREV_DAY_PR
(
   PROD_ID NUMBER(6),
   PREV_DAY_PRICE   NUMBER(8,3)
);
/
insert into PROD_PREV_DAY_PR values (1001, 231.505);
insert into PROD_PREV_DAY_PR values (1002, 7865.756);
insert into PROD_PREV_DAY_PR values (1003, 31082.577);
insert into PROD_PREV_DAY_PR values (1004, 430.78);
insert into PROD_PREV_DAY_PR values (1005, 33986.45);
insert into PROD_PREV_DAY_PR values (1006, 34856.376);
insert into PROD_PREV_DAY_PR values (1007, 174.775);
insert into PROD_PREV_DAY_PR values (1008, 25631.834);
insert into PROD_PREV_DAY_PR values (1009, 8741.846);
insert into PROD_PREV_DAY_PR values (1010, 597.675);
/
create table PROD_LATEST_PR
(
   PROD_ID NUMBER(6),
   LATEST_PRICE   NUMBER(8,3)
);
/
insert into PROD_LATEST_PR values (1001, 255.120);
insert into PROD_LATEST_PR values (1002, 7546.345);
insert into PROD_LATEST_PR values (1003, 31130.340);
insert into PROD_LATEST_PR values (1004, 418.9);
insert into PROD_LATEST_PR values (1005, 34865.789);
insert into PROD_LATEST_PR values (1006, 36547.877);
insert into PROD_LATEST_PR values (1007, 188.56);
insert into PROD_LATEST_PR values (1008, 25688.193);
insert into PROD_LATEST_PR values (1009, 8886.301);
insert into PROD_LATEST_PR values (1010, 643.698);
/
create table PROD_END_OF_MNTH_PR
(
   PROD_ID NUMBER(6),
   END_OF_MNTH_PRICE   NUMBER(8,3)
);
/
insert into PROD_END_OF_MNTH_PR values (1001, 217.014);
insert into PROD_END_OF_MNTH_PR values (1002, 77345.882);
insert into PROD_END_OF_MNTH_PR values (1003, 30567.456);
insert into PROD_END_OF_MNTH_PR values (1004, 450.867);
insert into PROD_END_OF_MNTH_PR values (1005, 35486.977);
insert into PROD_END_OF_MNTH_PR values (1006, 34542.984);
insert into PROD_END_OF_MNTH_PR values (1007, 207.345);
insert into PROD_END_OF_MNTH_PR values (1008, 26974.05);
insert into PROD_END_OF_MNTH_PR values (1009, 8345.773);
insert into PROD_END_OF_MNTH_PR values (1010, 623.864);
/