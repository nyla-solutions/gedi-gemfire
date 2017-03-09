alter user sys identified by Security#1

grant connect to sys

grant execute on utl_http to gemDb;
grant execute on utl_file to gemDb;





CREATE TABLE employer (
         employerId      VARCHAR2(50) PRIMARY KEY,
         orgId         VARCHAR2(15) NOT NULL);



CREATE TABLE employee (
         employeeId       VARCHAR2(50) PRIMARY KEY,
         firstName      VARCHAR2(50) NOT NULL,
         lastName      VARCHAR2(50) NOT NULL,   
         middleName      VARCHAR2(40),      
         ssn        VARCHAR2(20),
         job        VARCHAR2(10),
         mgrId      VARCHAR2(50),
         hiredate   DATE,
         photo      BLOB,
         sal        NUMBER(10,2),
         hourlyRate  NUMBER(7,2),
         commission       NUMBER(7,2),
         employerId    VARCHAR2(50)  NOT NULL
                     CONSTRAINT employee_fkey REFERENCES employer
                     (employerId));

                     
create or replace TRIGGER EMPLOYEE_TRIGGER 
AFTER INSERT OR UPDATE
  ON employee
  FOR EACH ROW
DECLARE
   out varchar(32000);  
BEGIN
select  utl_http.request('http://192.168.0.11:9090/'||'?'||'key'||'='
   ||:new.EMPLOYEEID
   ||'&'
   ||'cmd=empJpa2Gf') 
   into out
   from dual;
   
END;
 

create or replace TRIGGER EMPLOYEE_DEL_TRIGGER 
AFTER DELETE
  ON employee
  FOR EACH ROW
DECLARE
   out varchar(32000);  
BEGIN
select  utl_http.request('http://192.168.0.11:9090/'||'?'||'key'||'='
   ||:old.EMPLOYEEID
   ||'&'
   ||'cmd=empJpa2Gf') 
   into out
   from dual;
   
END;