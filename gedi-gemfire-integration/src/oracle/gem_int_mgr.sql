create or replace
PACKAGE BODY DB_MON_MGR 
is 
	--
	--
	-- Purpose: Monitor Oracle jobs, user sessions and SQLs
	--
	-- MODIFICATION HISTORY
	-- Person      Date    Comments
	-- ---------   ------  ------------------------------------------       
	-- G. Green   5/27/10  Creation
  -- G. Green   6/23/10  Added regular expression support 
  --                     and ability to ignore SQL/sessions separately
  -- G. Green   8/1/6/10 fixed issue where check running looked at dba_SCHEDULER_JOBS.START_DATE
  --                     instead of dba_SCHEDULER_JOBS.LAST_START_DATE

   PROCEDURE check_all 
   IS 
   BEGIN 
    check_jobs; 
    check_sessions; 
    check_sqls;
   END; 
-- ============================================================ 
-- Get configuration a single value parameter
-- ============================================================ 
FUNCTION GET_PARAM_VALUE(in_param_nm monitoring_params.param_nm%TYPE, in_default_param_value monitoring_params.param_value%TYPE default NULL) 
RETURN monitoring_params.param_value%TYPE
IS
   v_param_value monitoring_params.param_value%TYPE := null;
BEGIN

   BEGIN
    select param_value into v_param_value from monitoring_params where param_nm = in_param_nm and rownum = 1;
   EXCEPTION
   WHEN NO_DATA_FOUND THEN
        
      IF in_default_param_value is null THEN
        RAISE  NO_DATA_FOUND;
      END IF;
      
      v_param_value := in_default_param_value; -- use default
   END; -- end exception
  
   return v_param_value;
END;

-- =====================================
--  Send Notification
-- ============================================================ 
PROCEDURE send_notification(                                  
                 in_topic_param        IN VARCHAR2 CHARACTER SET ANY_CS DEFAULT NULL, 
                 in_message        IN VARCHAR2 CHARACTER SET ANY_CS DEFAULT NULL,
                 v_username_regexp_like IN  VARCHAR2 DEFAULT '.*')
IS
  v_destination_email clob := '';   
  v_subject clob := '';
  v_email_sender monitoring_params.param_value%TYPE;
  
   err_msg VARCHAR2(100); 
   mail_str VARCHAR2(4000); 
BEGIN

  BEGIN
     DBMS_OUTPUT.PUT_LINE('selecting param='||EMAIL_SENDER_PARAM);  
    -- Select email sending email
    select param_value into v_email_sender from monitoring_params where param_nm = EMAIL_SENDER_PARAM and rownum =1;
    
    DBMS_OUTPUT.PUT_LINE('results='||v_email_sender);  
    
    --- Select destination email addresses
    For an_email_add in 
    ( select distinct email_address 
      from monitoring_reg 
      where topic in (in_topic_param, ALL_TOPIC_NM)
      and REGEXP_LIKE(username,v_username_regexp_like)
    )
    LOOP
        v_destination_email := v_destination_email||rtrim(an_email_add.email_address)||';';
    END LOOP;
    
    -- Select email subject
    DBMS_OUTPUT.PUT_LINE('looking for subject param='||in_topic_param||'_SUBJECT');  
    
    v_subject := get_param_value(in_topic_param||'_SUBJECT', DEFAULT_SUBJECT_MSG);
    
    -- Send email
    UTL_MAIL.SEND(v_email_sender,v_destination_email,subject=>v_subject,message=>in_message, mime_type=>'text/html; charset=us-ascii');
    
    DBMS_OUTPUT.PUT_LINE('Sending to emails:'||v_destination_email); 
    
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    err_msg := SUBSTR(SQLERRM, 1, 100); 
    mail_str := 'Error with auto email - ' || err_msg;
    
    DBMS_OUTPUT.PUT_LINE('ERROR:'||mail_str); 
    UTL_MAIL.SEND(error_email_sender,error_destion_emails,subject=>'DB monitoring setup error',message=>mail_str, mime_type=>'text/html; charset=us-ascii');
  
  WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  mail_str := 'Error with auto email - ' || err_msg;
  DBMS_OUTPUT.PUT_LINE('ERROR:'||mail_str); 
    UTL_MAIL.SEND(error_email_sender,error_destion_emails,subject=>'DB monitoring error',message=>mail_str, mime_type=>'text/html; charset=us-ascii');
    
  END;
  
  
END; -- Procedure send_notification

-- ============================================================ 
-- Check for database job failures or long running jobs 
-- ============================================================ 
PROCEDURE check_jobs
   IS 
	mail_str 		CLOB; 
	v_schema_name 		VARCHAR2(32); 
	v_instance 		VARCHAR2(50); 
	err_msg 		VARCHAR2(100); 
  v_max_job_duration_intv INTERVAL DAY TO SECOND;
  v_job_memory_hr NUMBER;
BEGIN                 

      -- ============================================== 
      --  Check long running jobs
      -- ============================================== 
      check_running_jobs;

      -- ============================================== 
      -- = TODAY's failed jobs 
      -- ============================================== 
      check_failed_jobs;
		 
		-- ==================================================== 
		-- START DISABLE JOBS 
		 check_disabled_jobs;

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  --msg_subj := 'Error with auto email';
  mail_str := mail_str || 'Error with DB_MON_MGR.check_jobs - ' || err_msg;
  --UTL_MAIL.SEND('eln_monitoring2@helnhap',email_errors,subject=>msg_subj,message=>mail_str, mime_type=>'text/html; charset=us-ascii'); 
  
  -- Send Error
  send_notification(ERRORS_TOPIC_NM,mail_str);
 
END; -- Procedure 
-- ============================================================ 
-- Check for database job long running jobs 
-- ============================================================ 
PROCEDURE check_running_jobs(v_username_regexp_like IN  VARCHAR2 DEFAULT '.*') 
   IS 
	--mess_str_tble p_types.mail_str_tbl_type; 
	mail_str 		VARCHAR(4000); 
	
	v_instance 		VARCHAR2(50); 
	
	has_long_jobs 		CHAR := 'N'; 
	n_disenabled_cnt 	NUMBER :=0; 
	err_msg 		VARCHAR2(100); 
  --v_max_job_duration_intv INTERVAL DAY TO SECOND;
  --v_job_memory_hr NUMBER;
BEGIN                 
		    
  --Get job memory_hr
  --v_job_memory_hr := TO_NUMBER(get_param_value(JOB_MEMORY_HRS_PARAM,TO_CHAR(DEFAULT_MAX_SESSION_HR)));
  
  -- DBMS_OUTPUT.PUT_LINE('max_job_duration='||max_job_duration);  
    
	--recepient_list:=rtrim(recepient_list,';'); 
				    
-- 	Select database name 
	SELECT GLOBAL_NAME INTO v_instance from global_name;    
   
	 -- Add long running jobs 
  		 
   FOR cur_hung_jobs IN (select  distinct jobs.OWNER owner, jobs.JOB_NAME job_name, jobs.JOB_ACTION job_action, jobs.last_start_date last_start_date, 
                                run.ELAPSED_TIME as run_duration, jobs.comments comments, run.cpu_used cpu_used, 
                                rules.max_time_interval max_time_interval
                        from   dba_SCHEDULER_RUNNING_JOBS run, dba_scheduler_jobs jobs, monitoring_job_rules rules
                        where  run.JOB_NAME = jobs.JOB_NAME 
                                and REGEXP_LIKE(jobs.JOB_NAME, rules.JOB_NM_REGEXP_LIKE, 'i')
								and not REGEXP_LIKE(jobs.JOB_NAME, NVL(rules.JOB_NM_NOT_REGEXP_LIKE, ' '), 'i')
                                and jobs.job_name not in (select param_value from monitoring_params where param_nm = IGNORE_JOB_NM_PARAM)									  
                                and jobs.OWNER not in (select param_value from monitoring_params where param_nm = IGNORE_JOB_OWNER_PARM)
                                and TO_NUMBER(TO_CHAR(jobs.last_start_date, 'D')) >= rules.start_DT_D
                                and TO_NUMBER(TO_CHAR(jobs.last_start_date, 'D')) <= rules.end_DT_D
                                and TO_NUMBER(TO_CHAR(jobs.last_start_date, 'HH24')) >= rules.start_HH24
                                and TO_NUMBER(TO_CHAR(jobs.last_start_date, 'HH24')) <= rules.end_HH24
                                and run.ELAPSED_TIME > rules.max_time_interval
                        order by jobs.last_start_date
                        )                  
               LOOP 
			   
			   -- Initially found a long running job
			   IF has_long_jobs = 'N' THEN
					has_long_jobs := 'Y';
					
					-- Add header
					mail_str:='The following is the report of the long running jobs on '||v_instance||' instance'||'.'; 
					mail_str:=mail_str||'<p><table border="1">'; 
					mail_str:= mail_str||'<tr style="background-color:red;font-weight=bold">'|| '<td>JOB_NAME</td>'||'<td>OWNER</td>'||'<td>START TIME</td>' 
                            ||'<td>Max Time<br/>(DDD HH:MM:SS)</td>'||'<td>Elapse Time<br/>(DDD HH:MM:SS)</td>'
                            ||'<td>CPU Used<br/>(DD HH:MM:SS)</td>'||'<td>Description</td></tr>';  
			   END IF;
 
			   DBMS_OUTPUT.PUT_LINE('Long running JOB_NAME='||cur_hung_jobs.JOB_NAME);  			      
 
				 mail_str:=   mail_str||'<tr><td>'||cur_hung_jobs.JOB_NAME||'</td>' 
					  ||'<td>'||cur_hung_jobs.OWNER||'</td>' 
					  ||'<td>'||TO_CHAR(cur_hung_jobs.last_start_date,'MM/dd/YYYY HH:mi:ss AM')||'</td>' 
            ||'<td>'||cur_hung_jobs.max_time_interval||'</td>' 
					  ||'<td>'||cur_hung_jobs.run_duration||'</td>' 
            ||'<td>'||cur_hung_jobs.cpu_used||'</td>' 
					  ||'<td>'||cur_hung_jobs.comments||'</td></tr>'; 
				END LOOP; 
                     
                
				
		-- ==================================================== 
		-- Send Email if needed 
		IF  has_long_jobs = 'Y' THEN 
		
			-- end HTML table
			mail_str:=mail_str||'</table></p>'; 
			
			mail_str:= mail_str||' These jobs may require further investigation.'; 

		   -- Sending notification for the jobs
		   send_notification(JOBS_RUNNING_TOPIC_NM,mail_str,v_username_regexp_like);      
		END IF;         

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 

  mail_str := mail_str || 'Error with DB_MON_MGR.check_running_jobs - ' || err_msg;
  
  -- Send Error
  send_notification(ERRORS_TOPIC_NM,mail_str,v_username_regexp_like);
 
END; -- Procedure check_running_jobs
-- ============================================================ 
-- Check for failed jobs 
-- ============================================================ 
PROCEDURE check_failed_jobs(v_username_regexp_like IN  VARCHAR2 DEFAULT '.*')
   IS 	
	mail_str 		VARCHAR(4000);  
	v_instance 		VARCHAR2(50); 
	err_msg 		VARCHAR2(100); 
  has_failed_jobs CHAR := 'N';
  v_job_memory_hr number;
  
BEGIN                 
		    
-- DBMS_OUTPUT.PUT_LINE('max_job_duration='||max_job_duration);  
  
  -- Determine how far back to look for failed jobs
  v_job_memory_hr := TO_NUMBER(get_param_value(JOB_MEMORY_HRS_PARAM, DEFAULT_JOB_MEMORY_HRS));
  
	--recepient_list:=rtrim(recepient_list,';'); 
				    
-- 	Select database name 
	SELECT GLOBAL_NAME INTO v_instance from global_name;    
               
-- ============================================== 
-- =  failed jobs 
-- ============================================== 

       -- Select last error for each jobs           			 
       FOR cur_hung_jobs IN (select log.job_name, jobs.OWNER, log.status,jobs.COMMENTS, log.log_date, max(details.additional_info) error_msg, jobs.last_start_date
                      from dba_scheduler_job_log log, DBA_SCHEDULER_JOB_RUN_DETAILS details, dba_scheduler_jobs jobs
                      where   log.status = 'FAILED' 
                      and log.log_date > sysdate - (v_job_memory_hr/24)
                      and log.log_id = details.log_id
                      and log.job_name = details.job_name
                      and log.job_name =  jobs.job_name
                      and jobs.job_name = details.job_name                      
                    and jobs.OWNER not in (select param_value from monitoring_params where param_nm = IGNORE_JOB_OWNER_PARM)
                    and jobs.job_name not in (select param_value from monitoring_params where param_nm = IGNORE_JOB_NM_PARAM)									  
                    group by log.job_name, jobs.OWNER, log.status,jobs.COMMENTS, log.log_date,jobs.last_start_date
									)                  
         LOOP 
			   
          IF has_failed_jobs = 'N' THEN
              -- failed job found
              has_failed_jobs := 'Y';
						
              -- Set initial Mail content
              mail_str:= mail_str||'The following is the report of jobs failure in the last '||v_job_memory_hr||' hours on '||v_instance||' instance'||'.'; 
              mail_str:= mail_str||'<p><table border="1">'; 
              mail_str:= mail_str||'<tr style="background-color:red;font-weight=bold">'|| '<td>JOB_NAME</td>'||'<td>OWNER</td>'||'<td>Log Date</td>' ||'<td>LAST START TIME</td>' 
                     ||'<td>Comments</td>'||'<td>Error</td>'||'</tr>';   
					END IF;
					          
					-- DBMS_OUTPUT.PUT_LINE('Broken jobs JOB_NAME='||cur_hung_jobs.JOB_NAME);  
						mail_str:=   SUBSTR(mail_str||'<tr style="vertical-align:text-top"><td>'||cur_hung_jobs.JOB_NAME||'</td>' 
							  ||'<td>'||cur_hung_jobs.OWNER||'</td>' 
                ||'<td>'||TO_CHAR(cur_hung_jobs.log_date,'MM/dd/YYY HH:mi:ss AM')||'</td>' 
							  ||'<td>'||NVL(TO_CHAR(cur_hung_jobs.last_start_date,'MM/dd/YYY HH:mi:ss AM'),chr(38)||'nbsp;')||'</td>' 
							  ||'<td>'||cur_hung_jobs.comments||'</td>' 
                ||'<td>'||dbms_lob.substr(cur_hung_jobs.error_msg,4000,1)||'</td>' 
							  ||'</tr>',0,4000);           
            
            EXIT WHEN LENGTH(mail_str) >  (4000 - 500);
                
			END LOOP;

		-- ==================================================== 
		-- Send Email if needed 
		IF  has_failed_jobs = 'Y' THEN 
		
			-- end HTML table
			mail_str:= mail_str||'</table></p>';
			
			
			mail_str:= mail_str||' These jobs may require further investigation.'; 
           
		   -- Sending notification for the jobs
		   send_notification(JOBS_FAILED_TOPIC_NM,mail_str,v_username_regexp_like);      
		END IF;         

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  
  mail_str := SUBSTR('Error with DB_MON_MGR.check_failed_jobs - ' || err_msg,1,4000);
  
  -- Send Error
  send_notification(ERRORS_TOPIC_NM,mail_str,v_username_regexp_like);
 
END; -- Procedure check_failed_jobs
-- ============================================================ 
-- Check for failed jobs 
-- ============================================================ 
PROCEDURE check_disabled_jobs (v_username_regexp_like IN  VARCHAR2 DEFAULT '.*')
   IS 
	mail_str 		VARCHAR(4000); 
	v_instance 		VARCHAR2(50); 
	err_msg 		VARCHAR2(100);   
	has_disabled_jobs CHAR := 'N';
	
BEGIN                 

	-- 	Select database name 
	SELECT GLOBAL_NAME INTO v_instance from global_name;     
		       
			 
	FOR disabled_jobs IN (select   jobs.job_name, jobs.OWNER, jobs.LAST_START_DATE, jobs.COMMENTS, jobs.enabled 
										from dba_scheduler_jobs jobs 
										where enabled = 'FALSE' 
                    and OWNER not in 
                    (select param_value from monitoring_params where param_nm = IGNORE_JOB_OWNER_PARM)
                    and jobs.job_name not in 
                    (select param_value from monitoring_params where param_nm = IGNORE_JOB_NM_PARAM)
										order by jobs.job_name )           
	LOOP 
			IF has_disabled_jobs = 'N' THEN
				has_disabled_jobs := 'Y';
				
				-- initialize the HTML content
				mail_str:= mail_str||'The following is the report of the current disabled jobs on '||v_instance||' instance'||'.'; 
				mail_str:= mail_str||'<p><table border="1">'; 
						mail_str:= mail_str||'<tr style="background-color:red;font-weight=bold">'|| '<td>JOB_NAME</td>'||'<td>OWNER</td>'||'<td>LAST TIME</td>' 
									   ||'<td>Comments</td>'||'<td>enabled</td></tr>';       
			END IF;
 
-- DBMS_OUTPUT.PUT_LINE('Disabled jobs JOB_NAME='||disabled_jobs.JOB_NAME);  

			 mail_str:=   mail_str||'<tr style="vertical-align:text-top"><td>'||disabled_jobs.JOB_NAME||'</td>' 
				  ||'<td>'||disabled_jobs.OWNER||'</td>' 
				  ||'<td>'||NVL(TO_CHAR(disabled_jobs.last_start_date,'MM/dd/YYY HH:mi:ss AM'),chr(38)||'nbsp;')||'</td>' 
				  ||'<td>'||disabled_jobs.comments||'</td>' 
				  ||'<td>'||disabled_jobs.enabled||'</td></tr>'; 
                    
	END LOOP; 
                     
    	
	-- END DISABLE JOBS
  
		-- ==================================================== 
		-- Send Email if needed 
		IF  has_disabled_jobs = 'Y' THEN 
		
			-- end HTML table
			mail_str:=mail_str||'</table></p>';
			
			
			mail_str:= mail_str||' These jobs may require further investigation.'; 

		   -- Sending notification for the jobs
		   send_notification(JOBS_DISABLED_TOPIC_NM,mail_str,v_username_regexp_like);      
		END IF;         

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  
  mail_str := mail_str || 'Error with DB_MON_MGR.check_disabled_jobs - ' || err_msg;  
  
  -- Send Error
  send_notification(ERRORS_TOPIC_NM,mail_str,v_username_regexp_like);
 
END; -- Procedure check_disabled_jobs
-- ================================================================ 
-- = Summarize a sessions  
-- ================================================================ 
FUNCTION get_sess_html 
		( in_session_id number)  
		RETURN CLOB 
		IS 
			v_html clob; 
			v_full_sql clob; 
			v_session_info  v$session%rowtype; 
			v_blocked_session varchar2(20); 
		BEGIN 
			SELECT *  
			into v_session_info 
			FROM v$session 
			WHERE SID = in_session_id; 
			 
			 
			--select SQL 
			IF v_session_info.sql_id is not null THEN 
				SELECT SQL_FULLTEXT into v_full_sql 
				FROM v$sql 
				WHERE sql_id = v_session_info.sql_id; 
			END IF; 
  
			 
			-- Decorate blocked sessions 
			IF v_session_info.BLOCKING_SESSION IS NULL THEN 
				v_blocked_session  := chr(38)||'nbsp'; -- nbps; HTML none blocking space 
			ELSE 
				v_blocked_session  := TO_CHAR(v_session_info.BLOCKING_SESSION); 
			END IF; 
			 
			-- Format HTML 
			 
			v_html := '<tr style="vertical-align:text-top">' 
				||'<td>'||upper(v_session_info.SID)||' '||v_session_info.serial#||'</td>'					 
				||'<td>'||upper(v_session_info.USERNAME)||'</td>' 
				||'<td>'||to_char(v_session_info.LOGON_TIME, 'DD-MON-YYYY,HH:MI:SS AM')||'</td>'				 
				||'<td>'||to_char(round((sysdate-v_session_info.LOGON_TIME)*24,2),'9999999999.99')||'</td>' 
				||'<td>'||replace(v_session_info.MACHINE,'\','\ ')||'</td>'				 
				||'<td>'||upper(v_session_info.program)||'</td>' 
				||'<td>'||v_full_sql||'</td>' 
				||'<td>'||v_blocked_session||'</td>' 
				||'<td>'||v_session_info.status||'</td>' 
				||'</tr>'; 
						 
			return v_html; 
		END;	 
		
		
-- =============================================================================== 
-- = Check User Sessions 
-- =============================================================================== 
PROCEDURE check_sessions (v_rule_nm_REGEXP_LIKE    IN VARCHAR2 DEFAULT '.*',v_username_regexp_like IN  VARCHAR2 DEFAULT '.*')
    IS 
      lv_recepientList VARCHAR2(4000); 
 
      mail_str clob; 
      msg_body VARCHAR2(4000); 
      
      --msg_subj VARCHAR2(200):='User Sessions Report from server'; 
       --recepient_list VARCHAR2(2000):=null; 
      v_schema_name VARCHAR2(32); 
		  v_instance VARCHAR2(50); 
		  N_CNT number := 0; 
		  blocking_sessions_details clob := null; 
		  v_block_session varchar2(20); 
      err_msg VARCHAR2(100); 
      --v_max_session_hr_duration number;
      
      has_long_sessions CHAR := 'N';
      v_step VARCHAR2(50) := NULL;
      
      MAX_STR_LENGTH number := 3072;

	BEGIN 
  
      v_step := 'START';
     -- DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 
      
      
      --select max session duration
      --v_max_session_hr_duration := TO_NUMBER(get_param_value(MAX_SESSION_DURATON_HR_PARAM,TO_CHAR(DEFAULT_MAX_SESSION_DURATON_HR)));
             
      -- Select database name 
      SELECT GLOBAL_NAME INTO v_instance from global_name; 
 
 
       v_step := 'global name';
      --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

 
     FOR cur_runn_sess IN ( 
      SELECT distinct s.SID,s.SERIAL#,s.USERNAME, 
            s.SCHEMANAME, s.TERMINAL,s.LOGON_TIME,s.MACHINE as machine,s.STATUS status, s.program, s.sql_id,  
            s.BLOCKING_SESSION, dbms_lob.substr(NVL(sql_info.SQL_FULLTEXT,chr(38)||'nbsp;'), 4000, 1) 
            SQL_FULLTEXT, rules.max_hr max_hr, sql_info.CPU_TIME	cpu_time			
       FROM v$session s, V$sql sql_info, monitoring_session_rules rules
       where STATUS like rules.status_like
        and sid not in (select SID from dba_jobs_running) 
        and s.sql_id = sql_info.sql_id (+)      
        and REGEXP_LIKE(rules.rule_nm,v_rule_nm_REGEXP_LIKE)
        and REGEXP_LIKE(s.username, rules.USERNAME_REGEXP_LIKE,'i')
        and not REGEXP_LIKE(s.username, nvl(rules.USERNAME_NOT_REGEXP_LIKE,' '),'i')    
        and TO_NUMBER(TO_CHAR(s.LOGON_TIME,'D')) >= rules.start_dt_d
        and TO_NUMBER(TO_CHAR(s.LOGON_TIME,'D')) <= rules.end_dt_d
        and TO_NUMBER(TO_CHAR(s.LOGON_TIME,'HH24')) >= rules.start_hh24
        and TO_NUMBER(TO_CHAR(s.LOGON_TIME,'HH24')) <= rules.end_hh24
        and s.LOGON_TIME < sysdate- rules.max_hr/24 
        and s.USERNAME not in
          (select param_value from monitoring_params where param_nm = IGNORE_SESSION_USERNAME_PARM)  
        and s.osuser not in 
         (select param_value from monitoring_params where param_nm = IGNORE_SESSION_OSUSER_NM_PARAM)  
        order by s.USERNAME
        )              
      LOOP 
      
       v_step := 'START LOOP';
       --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

        N_CNT := N_CNT + 1; -- increment number
        
        IF has_long_sessions = 'N' THEN
          -- found long running session
          has_long_sessions := 'Y';
          
          
          v_step := 'build header HTML';
           --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

          -- initialize HTML content
          mail_str:='The following sessions in '||v_instance||' instance'||' server '|| 
                    ' have been running for more than the allowed duration.'||CHR(10); 
 
             mail_str:=mail_str||'<p><table border="1">'; 
             mail_str:= mail_str||'<tr style="background-color:red;font-weight=bold">' 
              ||'<td>SID</td>'						 
              ||'<td>USER</td>' 
              ||'<td>STATUS</td>' 
              ||'<td>LOGON TIME</td>' 
              ||'<td>MAX TIME (HRS)</td>' 
              ||'<td>SESSION TIME(HRS)</td>' 
              ||'<td>MACHINE</td>' 
              ||'<td>Program</td>' 
              ||'<td>CPU Time (secs)</td>' 
              ||'<td>Current SQL</td>' 
              ||'<td>BLOCKING SESSION</td>' 
              ||'</tr>'; 
          
        END IF;
        
        
        -- decorate blocking sessions 
        IF cur_runn_sess.BLOCKING_SESSION IS NULL THEN 
						v_block_session :=  chr(38)||'nbsp;'; -- nbps; HTML none blocking space 
        ELSE 
						v_block_session := TO_CHAR(cur_runn_sess.BLOCKING_SESSION); 
        END IF; 
    
        v_step := 'decorated blocking lock';
        --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

        mail_str:= mail_str ||'<tr style="vertical-align:text-top"><td>'||nvl(cur_runn_sess.sid,0)||' '||nvl(cur_runn_sess.serial#,0)||'</td>'						 
                        ||'<td>'||upper(cur_runn_sess.USERNAME)||'</td>' 
                        ||'<td>'||cur_runn_sess.STATUS||'</td>' 
                  ||'<td>'||to_char(cur_runn_sess.LOGON_TIME, 'DD-MON-YYYY,HH:MI:SS AM')||'</td>' 
                  ||'<td>'||to_char(nvl(cur_runn_sess.max_hr,0),'9999999999.99')||'</td>' 
                  ||'<td>'||to_char(round((sysdate-cur_runn_sess.LOGON_TIME)*24,2),'9999999999.99')||'</td>' 
                  ||'<td>'||replace(cur_runn_sess.MACHINE,'\','\ ')||'</td>' 
                  ||'<td>'||upper(cur_runn_sess.program)||'</td>' 
                  ||'<td>'||NVL(cur_runn_sess.cpu_time,0)/1000000||'</td>' 
                  ||'<td>'||cur_runn_sess.SQL_FULLTEXT||'</td>' 
                  ||'<td>'||v_block_session||'</td></tr>'; 



        v_step := 'built row';
        --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 
 
						-- add blocking session 
						IF cur_runn_sess.BLOCKING_SESSION IS NOT NULL THEN 
						    IF blocking_sessions_details is null THEN -- first decorator table 
								blocking_sessions_details := '<p>The following have been identified as blocking one or more of the reported long; ' 
														 ||'running user sessions <br/>' 
														 ||'<p><table border="1">' 
														 ||'<tr style="background-color:red;font-weight=bold">' 
														 ||'<td>SID</td>'						 
														 ||'<td>USER</td>' 
														 ||'<td>LOGON TIME</td>' 
														 ||'<td>SESSION TIME(HRS)</td>' 
														 ||'<td>MACHINE</td>' 
														 ||'<td>Program</td>' 
														 ||'<td>SQL</td>' 
														 ||'<td>BLOCKING SESSION</td>' 
														 ||'<td>STATUS</td>' 
														 ||'</tr>'; 
							END IF; 
							 
						     blocking_sessions_details := blocking_sessions_details || get_sess_html(v_block_session); 
                 
                v_step := 'built blocked session';
                --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

						END IF; 
            
            -- ======================
            --Detected if row buffer is full            
            IF LENGTH(blocking_sessions_details) > MAX_STR_LENGTH OR 
               LENGTH(mail_str) > MAX_STR_LENGTH THEN
               
               -- Send Information
               IF blocking_sessions_details is not null THEN 
                --close HTML for blocking sessions details
                blocking_sessions_details := blocking_sessions_details||'</table><br/></p>'; 
              END IF; 
               
               
             v_step := 'sending information inside loop';
             --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 
      
             mail_str:=mail_str||'</table></p>'||blocking_sessions_details; 
             mail_str:= mail_str||'These sessions may require further investigation.'; 
                
            -- Select email address TODO: move to function 
             send_notification(SESSIONS_TOPIC_NM,mail_str);
             
             -- reset variables
             has_long_sessions := 'N';
             blocking_sessions_details := NULL;
             mail_str := NULL;
             
            END IF;
            
				END LOOP; 
 
        IF blocking_sessions_details is not null THEN 
          --close HTML for blocking sessions details
					blocking_sessions_details := blocking_sessions_details||'</table><br/></p>'; 
				END IF; 
				 
         
       v_step := 'sending information';
       --DBMS_OUTPUT.PUT_LINE('STEP:'||v_step); 

       IF has_long_sessions = 'Y' THEN
          mail_str:=mail_str||'</table></p>'||blocking_sessions_details; 
          mail_str:= mail_str||'These sessions may require further investigation.'; 
          
         -- Select email address TODO: move to function 
         send_notification(SESSIONS_TOPIC_NM,mail_str,v_username_regexp_like);
       END IF;

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  
  mail_str := 'Error with DB_MON_MGR.check_sessions - ' || err_msg||' step:'||v_step;
  
  send_notification(ERRORS_TOPIC_NM,mail_str,v_username_regexp_like);
 
END; -- Procedure check_sessions
-- =============================================================================== 
-- = Check Long running SQLs
-- =============================================================================== 
PROCEDURE check_sqls (v_rule_nm_REGEXP_LIKE    IN VARCHAR2 DEFAULT '.*',v_username_regexp_like IN  VARCHAR2 DEFAULT '.*')
    IS 
      lv_recepientList VARCHAR2(4000); 
 
      mail_str clob; 
      msg_body VARCHAR2(4000); 
      
      v_schema_name VARCHAR2(32); 
	  v_instance VARCHAR2(50); 
	  N_CNT number;
	  blocking_sessions_details clob := null; 
	  v_block_session varchar2(20); 
      err_msg VARCHAR2(100); 
      
      has_long_sqls CHAR := 'N';

      v_BUFFER_ROW_CNT NUMBER :=  100;
      
	BEGIN 
             
      -- Select database name 
      SELECT GLOBAL_NAME INTO v_instance from global_name; 
 
     FOR cur_runn_sql IN ( 
      SELECT  distinct s.SID,s.SERIAL#,s.USERNAME, 
            s.SCHEMANAME,s.TERMINAL,s.LOGON_TIME, s.last_call_et AS last_call_et,
			sql_info.LAST_ACTIVE_TIME as LAST_ACTIVE_TIME,s.MACHINE as machine,s.STATUS, s.program, s.sql_id,  
            s.BLOCKING_SESSION, dbms_lob.substr(NVL(sql_info.SQL_FULLTEXT,chr(38)||'nbsp;'), 4000, 1) 
            SQL_FULLTEXT, rules.max_exec_time_secs max_exec_time_secs, sql_info.cpu_time	
       FROM v$session s, V$sql sql_info, monitoring_sql_rules rules
       where s.STATUS = ACTIVE_STATUS_CONST 
        AND   RAWTOHEX(s.sql_address) <> '00'     
        AND   s.username is not null
      and sid not in (select SID from dba_jobs_running) 
      and sid not in (select SESSION_ID from dba_SCHEDULER_RUNNING_JOBS)
        and   s.sql_address = sql_info.address		
        and   s.sql_hash_value = sql_info.hash_value
        and REGEXP_LIKE(rules.rule_nm,v_rule_nm_REGEXP_LIKE)
        and REGEXP_LIKE(s.username, rules.USERNAME_REGEXP_LIKE,'i')
        and not REGEXP_LIKE(s.username, nvl(rules.USERNAME_NOT_REGEXP_LIKE,' '),'i')
        and TO_NUMBER(TO_CHAR(sql_info.LAST_ACTIVE_TIME,'D')) >= rules.start_dt_d
        and TO_NUMBER(TO_CHAR(sql_info.LAST_ACTIVE_TIME,'D')) <= rules.end_dt_d
        and TO_NUMBER(TO_CHAR(sql_info.LAST_ACTIVE_TIME,'HH24')) >= rules.start_hh24
        and TO_NUMBER(TO_CHAR(sql_info.LAST_ACTIVE_TIME,'HH24')) <= rules.end_hh24
        and s.last_call_et >= rules.max_exec_time_secs 
        and s.USERNAME not in
          (select param_value from monitoring_params where param_nm = IGNORE_SQL_USERNAME_PARM)
        and s.osuser not in 
         (select param_value from monitoring_params where param_nm = IGNORE_SQL_OSUSER_NM_PARAM)  
        order by s.USERNAME
        )              
      LOOP 
      
        IF has_long_sqls = 'N' THEN
          -- found long running session
          has_long_sqls := 'Y';
          
          -- initialize HTML content
          mail_str:='The following ACTIVE sqls in '||v_instance||' instance'||' server '|| 
                    ' have been running for more than the allowed duration.'||CHR(10); 
 
             mail_str:=mail_str||'<p><table border="1">'; 
             mail_str:= mail_str||'<tr style="background-color:red;font-weight=bold">' 
              ||'<td>SID</td>'						 
              ||'<td>USER</td>' 
			  ||'<td>LOGON_TIME</td>' 			  
              ||'<td>LAST ACTIVE TIME</td>' 
              ||'<td>MAX Exec Time(Secs)</td>' 
              ||'<td>Exec Time (Secs)</td>' 
              ||'<td>MACHINE</td>' 
              ||'<td>Program</td>' 
              ||'<td>CPU Time (secs)</td>' 
              ||'<td>SQL</td>' 
              ||'<td>BLOCKING SESSION</td>' 
              ||'</tr>'; 
          
        END IF;
			    
        -- decorate blocking sessions 
        IF cur_runn_sql.BLOCKING_SESSION IS NULL THEN 
						v_block_session :=  chr(38)||'nbsp;'; -- nbps; HTML none blocking space 
        ELSE 
						v_block_session := TO_CHAR(cur_runn_sql.BLOCKING_SESSION); 
        END IF; 
			    
        mail_str:= mail_str ||'<tr style="vertical-align:text-top"><td>'||cur_runn_sql.sid||' '||cur_runn_sql.serial#||'</td>'						 
                        ||'<td>'||upper(cur_runn_sql.USERNAME)||'</td>' 
                  ||'<td>'||to_char(cur_runn_sql.LOGON_TIME, 'DD-MON-YYYY,HH:MI:SS AM')||'</td>' 
				  ||'<td>'||to_char(cur_runn_sql.LAST_ACTIVE_TIME, 'DD-MON-YYYY,HH:MI:SS AM')||'</td>' 
                  ||'<td>'||to_char(cur_runn_sql.max_exec_time_secs,'9999999999.99')||'</td>' 
                  ||'<td>'||to_char(round(cur_runn_sql.last_call_et,2),'9999999999.99')||'</td>' 
                  ||'<td>'||replace(cur_runn_sql.MACHINE,'\','\ ')||'</td>' 
                  ||'<td>'||upper(cur_runn_sql.program)||'</td>' 
                  ||'<td>'||NVL(cur_runn_sql.cpu_time,0)/1000000||'</td>' 
                  ||'<td>'||cur_runn_sql.SQL_FULLTEXT||'</td>' 
                  ||'<td>'||v_block_session||'</td></tr>'; 
												 
						-- add blocking session 
						IF cur_runn_sql.BLOCKING_SESSION IS NOT NULL THEN 
						    IF blocking_sessions_details is null THEN -- first decorator table 
								blocking_sessions_details := '<p>The following have been identified as blocking one or more of the reported long ' 
														 ||'running user sessions <br/>' 
														 ||'<p><table border="1">' 
														 ||'<tr style="background-color:red;font-weight=bold">' 
														 ||'<td>SID</td>'						 
														 ||'<td>USER</td>' 
														 ||'<td>LOGON TIME</td>' 
														 ||'<td>SESSION TIME(HRS)</td>' 
														 ||'<td>MACHINE</td>' 
														 ||'<td>Program</td>' 
														 ||'<td>SQL</td>' 
														 ||'<td>BLOCKING SESSION</td>' 
														 ||'<td>STATUS</td>' 
														 ||'</tr>'; 
							END IF; 
							 
						     blocking_sessions_details := blocking_sessions_details || get_sess_html(v_block_session); 
						END IF; 
            
            -- Test two many rows
            
				END LOOP; 
 
        IF blocking_sessions_details is not null THEN 
          --close HTML for blocking sessions details
					blocking_sessions_details := blocking_sessions_details||'</table><br/></p>'; 
				END IF; 
        
         
       IF has_long_sqls = 'Y' THEN
          mail_str:=mail_str||'</table></p>'||blocking_sessions_details; 
          mail_str:= mail_str||'These SQLs may require further investigation.'; 
          
         -- Select email address
         send_notification(SQLS_TOPIC_NM,mail_str,v_username_regexp_like);
       END IF;

EXCEPTION

WHEN Others THEN
  err_msg := SUBSTR(SQLERRM, 1, 100); 
  
  mail_str := 'Error with DB_MON_MGR.check_sqls - ' || err_msg;
  
  send_notification(ERRORS_TOPIC_NM,mail_str,v_username_regexp_like);
 
END; -- Procedure check_sqls
 

end DB_MON_MGR;
