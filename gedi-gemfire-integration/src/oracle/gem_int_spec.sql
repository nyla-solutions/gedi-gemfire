create or replace PACKAGE GEMFIRE_INT
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
  
   PROCEDURE check_all;         -- Calls check_jobs and check_sessions
   
   -- =============================================================
   -- Package procedures
   -- Job Monitoring
   procedure check_jobs;          -- Calls check_running_jobs, check_failed_jobs and check_disabled_jobs
   procedure check_running_jobs(v_username_regexp_like IN  VARCHAR2 DEFAULT '.*');  -- Monitor long running database jobs
   procedure check_failed_jobs(v_username_regexp_like IN  VARCHAR2 DEFAULT '.*');    -- Monitor failed database jobs
   procedure check_disabled_jobs(v_username_regexp_like IN  VARCHAR2 DEFAULT '.*');   -- Monitor disabled jobs
   
   -- User Session Monitoring
   procedure check_sessions(v_rule_nm_REGEXP_LIKE    IN VARCHAR2 DEFAULT '.*',v_username_regexp_like IN  VARCHAR2 DEFAULT '.*'); -- Sends an email indicating  long running user sessions

  -- SQL Monitoring
   procedure check_sqls(v_rule_nm_REGEXP_LIKE    IN VARCHAR2 DEFAULT '.*',v_username_regexp_like IN  VARCHAR2 DEFAULT '.*'); -- Sends an email indicating  long running SQL
   
   -- =============================================================
   -- Default subject when no value found in parameters table
   DEFAULT_SUBJECT_MSG VARCHAR2(40) := 'DB monitoring alert';
   
   --Max 1/2 hour duration
   DEFAULT_MAX_SESSION_HR NUMBER := 1;


   --email_sender VARCHAR2(1000) := 'DLC@sctdp280.merck.com';
   error_email_sender VARCHAR2(1000) := 'gemInt@adp.com';
   error_destion_emails VARCHAR2(1000) := 'app_db_notifications@adp.com';
      

   -- ============================================================
   -- Package General Constants
   FAILED_STATUS_CONST dba_scheduler_job_log.status%TYPE := 'FAILED';
   ACTIVE_STATUS_CONST dba_scheduler_job_log.status%TYPE := 'ACTIVE';
   
   -- ==============================================
   -- Package TOPIC Constants
   ALL_TOPIC_NM monitoring_reg.TOPIC% TYPE      := 'ALL';   
   SESSIONS_TOPIC_NM monitoring_reg.TOPIC% TYPE := 'SESSIONS';   
   SQLS_TOPIC_NM monitoring_reg.TOPIC% TYPE := 'SQLS';   
   
   -- Topic related to error
   ERRORS_TOPIC_NM monitoring_reg.TOPIC% TYPE     := 'ERRORS';
   
   -- Configuration paramater than indicates the email subject for this subject
   SESSIONS_TOPIC_SUBJECT_PARAM monitoring_reg.TOPIC% TYPE := 'SESSIONS_SUBJECT';
   SQLS_TOPIC_SUBJECT_PARAM monitoring_reg.TOPIC% TYPE := 'SQLS_SUBJECT';
   
   -- Registration topic related to monitored jobs
   JOBS_FAILED_TOPIC_NM monitoring_reg.TOPIC% TYPE       := 'JOBS_FAILED';
   JOBS_RUNNING_TOPIC_NM monitoring_reg.TOPIC% TYPE     := 'JOBS_RUNNING';
   JOBS_DISABLED_TOPIC_NM monitoring_reg.TOPIC% TYPE     := 'JOBS_DISABLED';
   
   -- =========================================================
   -- The following are related to parmaters in the monitoring params table
   
   -- Param the indicated the subject for jobs    
   JOBS_FAILED_TOPIC_SUBJ_PARAM monitoring_reg.TOPIC% TYPE := 'JOBS_FAILED_SUBJECT';
   JOBS_RUNNING_TOPIC_SUBJ_PARAM monitoring_reg.TOPIC% TYPE := 'JOBS_RUNNING_SUBJECT';
   JOBS_DISABLED_TOPIC_SUBJ_PARAM monitoring_reg.TOPIC% TYPE := 'JOBS_DISABLED_SUBJECT';   
   
   -- Configuration paramater than indicates the email subject for this subject
   ERRORS_TOPIC_SUBJECT_PARAM monitoring_reg.TOPIC% TYPE := 'ERRORS_SUBJECT';
   
   -- ==================================================================
   -- Package parameters (the values are stored in the monitoring_param table   
   -- Configure email destination
   EMAIL_SENDER_PARAM monitoring_params.PARAM_NM% TYPE := 'EMAIL_SENDER' ;   
   
   -- Database job names to ignore
   IGNORE_JOB_NM_PARAM monitoring_params.PARAM_NM% TYPE := 'IGNORE_JOB_NM' ;
   
   -- Database owner related jobs that should be ignored to ignore
   IGNORE_JOB_OWNER_PARM monitoring_params.PARAM_NM% TYPE := 'IGNORE_JOB_OWNER' ;
    
   -- Configure interval before jobs are alert as outs (sample value 0 0:00:01)
   --MAX_JOB_INTERVAL_PARAM  monitoring_params.PARAM_NM%TYPE := 'MAX_JOB_INTERVAL' ;
   
    -- OS level user sessions to ignore
   IGNORE_SESSION_OSUSER_NM_PARAM monitoring_params.PARAM_NM% TYPE := 'IGNORE_SESSION_OSUSER' ;
   
   -- Username sessions to ignore
   IGNORE_SESSION_USERNAME_PARM monitoring_params.PARAM_NM% TYPE := 'IGNORE_SESSION_USERNAME' ;
      
  -- OS level user SQL sessions to ignore
   IGNORE_SQL_OSUSER_NM_PARAM monitoring_params.PARAM_NM% TYPE := 'IGNORE_SQL_OSUSER' ;
   
   -- Username SQL sessions to ignore
   IGNORE_SQL_USERNAME_PARM monitoring_params.PARAM_NM% TYPE := 'IGNORE_SQL_USERNAME' ;
   
   --Indicate the maximum number of hours to review the runnings jobs
   JOB_MEMORY_HRS_PARAM monitoring_params.PARAM_NM% TYPE := 'JOB_MEMORY_HRS' ;
   
   -- Default job lookup time window
   DEFAULT_JOB_MEMORY_HRS monitoring_params.PARAM_NM% TYPE := '24';
   
   -- =========================================
   -- User Session Management
   MAX_SESSION_DURATON_HR_PARAM monitoring_params.PARAM_NM%TYPE := 'MAX_SESSION_DURATON_HR' ;
   DEFAULT_MAX_SESSION_DURATON_HR NUMBER := 1;
   
end DB_MON_MGR;
