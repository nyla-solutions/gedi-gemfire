package gedi.solutions.gemfire.integration.spring.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nyla.solutions.global.patterns.servicefactory.ServiceFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import gedi.solutions.gemfire.integration.cmd.WebCommand;

/**
 * Web Command wrapper to start Spring batch jobs
 * @author Gregory Green
 *
 */
public class JobLaucherCmd implements WebCommand
{
	/**
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @see gedi.solutions.gemfire.integration.cmd.WebCommand#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String jobName = request.getParameter(jobNameParameter);
		
		if(jobName == null || jobName.length() == 0)
					throw new IllegalArgumentException(jobNameParameter+" is required");
		
		Map<String,JobParameter> map = new HashMap<String, JobParameter>();
		map.put("id",new JobParameter(UUID.randomUUID().toString()));
		JobParameters jobParameters = new JobParameters(map);
		
		
		Job job = ServiceFactory.getInstance().create(jobName);
		if(job == null)
					throw new IllegalArgumentException("job:"+jobName+" is not found");
		
		org.springframework.batch.core.JobExecution exe = jobLauncher.run(job, jobParameters);

		response.getWriter().println("Executing job:"+jobName);
		
		response.getWriter().println("Batch Status:"+exe.getStatus());
		
		int cnt =0;
		while((BatchStatus.STARTING.equals(exe.getStatus()) ||
				BatchStatus.STARTED.equals(exe.getStatus())) && cnt < maxChecks)
		{
			response.getWriter().println("waiting for batch process to complete");
			
			cnt++;
			Thread.sleep(sleepPeriod);
		}
		
		response.getWriter().println("Batch Status:"+exe.getStatus());
		
		ExitStatus exitStatus = exe.getExitStatus();
		
		
		response.getWriter().println("Exit code:"+exitStatus.getExitCode());
		response.getWriter().println("Exit code:"+exitStatus.getExitDescription());
		
		return null;
	}// --------------------------------------------------------
	
	/**
	 * 
	 * @return the jobNameParameter
	 */
	public String getJobNameParameter()
	{
		return jobNameParameter;
	}
	/**
	 * 
	 * @param jobNameParameter the job name parameter
	 */
	public void setJobNameParameter(String jobNameParameter)
	{
		this.jobNameParameter = jobNameParameter;
	}//--------------------------------------------------------
	/**
	 * 
	 * @return the job launcher
	 */
	public JobLauncher getJobLauncher()
	{
		return jobLauncher;
	}//--------------------------------------------------------
	/**
	 * 
	 * @param jobLauncher the job launcher
	 */
	public void setJobLauncher(JobLauncher jobLauncher)
	{
		this.jobLauncher = jobLauncher;
	}


	private int maxChecks = 100;
	private long sleepPeriod = 100;
	private String jobNameParameter = "job";
	private JobLauncher jobLauncher;
}
