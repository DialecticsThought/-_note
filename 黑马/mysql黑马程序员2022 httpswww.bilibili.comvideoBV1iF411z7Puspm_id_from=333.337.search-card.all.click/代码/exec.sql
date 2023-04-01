
CREATE TABLE emp(
	empno int primary key, -- 员工编号
	ename varchar(10), -- 员工姓名
    job varchar(9), -- 员工工作
    mgr int, -- 员工直属领导编号
    hiredate date, -- 入职时间
    sal double, -- 工资
    comm double, -- 奖金
    deptno int -- 对应dept表的外键
)
-- 添加部门和员工之间的主外键关系
ALTER TABLE emp add constraint foreign key emp(deptno) references dept(deptno)

insert into emp values(7369,'SMITH','CLERK',7902,'1980-12-17',800,NULL,20),
											(7499,'ALEN','SALESMAN',7698,'1981-02-20',1600,300,30),
											(7521,'WARD','SALESMAN',7698,'1981-02-22',1250,500,30),
											(7566,'JONES','MANAGER',7839,'1981-04-02',2975,NULL,20),
											(7654,'MARTIN','SALESMAN',7698,'1981-09-28',1250,1400,30),
											(7698,'BLAKE','MANAGER',7839,'1981-05-01',2850,NULL,30),
											(7782,'CLARK','MANAGER',7839,'1981-06-09',2450,NULL,10),
											(7788,'SCOTT','ANALYST',7566,'1987-04-19',3000,NULL,20),
(7839,'KING','PRESIDENT',NULL,'1981-11-17',5000,NULL,10),
(7844,'TURNER','SALESMAN',7698,'1981-09-08',1500,0,30),
(7876,'ADAMS','CLERK',7788,'1987-05-23',1100,NULL,20),
(7900,'JAMES','CLERK',7698,'1981-12-03',950,NULL,30),
(7902,'FORD','ANALYST',7566,'1981-12-03',3000,NULL,20),
(7934,'MILLER','CLERK',7782,'1982-01-23',1300,NULL,10);


create table student(
 id int,
 name varchar(20),
 gender varchar(20),
 chinese int,
 english int,
 math int
);
 
insert into student (id,name,gender,chinese,english,math) values (1,'张明','男',89,78,90);
insert into student (id,name,gender,chinese,english,math) values (2,'李进','男',67,53,95);
insert into student (id,name,gender,chinese,english,math) values (3,'王五','女',87,78,77);
insert into student (id,name,gender,chinese,english,math) values (4,'李一','女',88,98,92);
insert into student (id,name,gender,chinese,english,math) values (5,'李财','男',82,84,67);
insert into student (id,name,gender,chinese,english,math) values (6,'张宝','男',55,85,45);
insert into student (id,name,gender,chinese,english,math) values (7,'黄蓉','女',75,65,30);
insert into student (id,name,gender,chinese,english,math) values (7,'黄蓉','女',75,65,30);


create table employee (
dname varchar(20) ,-- 部门名
eid varchar(20),
ename varchar(20),
hiredate date, -- 入职日期
salary double -- 薪资
);

insert into employee values('研发部','1001','刘备','2021-11-01',3000);
insert into employee values('研发部','1002','关羽','2021-11-02' ,5000);
insert into employee values( '研发部','1003','张飞','2021-11-03',7000);
insert into employee values ('研发部','1004','赵云','2021-11-04',7000);
insert into employee values( '研发部','1005','马超','2021-11-05',4000);
insert into employee values( '研发部','1006','黄忠','2021-11-06',4900);
insert into employee values( '销售部','1007','曹操','2021-11-01',2000);
insert into employee values( '销售部','1008','许褚','2021-11-02',3000);
insert into employee values( '销售部','10091','典韦','2021-11-03',5000);
insert into employee values('销售部','1010','张辽','2021-11-04',6000);
insert into employee values( '销售部','1011','徐晃','2021-11-05',9000);
insert into employee values('销售部','1012','曹洪','2021-11-06',6000);







 create table if not exists dept3(
	deptno varchar(20)primary key,
	name varchar(20));
create table if not exists emp3(
	eid varchar(20) primary key,
	ename varchar(20),
	age int,
	dept_id varchar(20));

insert into dept3 values ('1001','研发部'),
                       ('1002','销售部'),
				      ('1003','财务部'),
				      ('1004','人事部');
insert into emp3 values ('1','乔峰',20,'1001'),
                        ('2','段誉',21,'1001'),
				      ('3','虚竹',23,'1001'),
				      ('4','阿紫',18,'1001'),
				      ('5','扫地僧',85,'1002'),
					('6','李秋水',33,'1002'),
					('7','鸠摩智',50,'1002'),
					('8','天山童姥',60,'1003'),
					('9','慕容博',58,'1003'),
					('10','丁春秋',71,'1005');