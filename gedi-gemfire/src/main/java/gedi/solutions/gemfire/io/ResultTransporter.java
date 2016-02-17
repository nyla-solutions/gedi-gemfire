package gedi.solutions.gemfire.io;


import com.gemstone.gemfire.cache.execute.ResultSender;

/**
 * Interface to send function/execution results
 * @author Gregory Green
 *
 */
public interface ResultTransporter
{
	/**
	 * Uses a result sender to send data to a client
	 * @param resultSender GemFire result sender
	 * @param data the data used to send
	 */
	public void send(ResultSender<Object> resultSender, Object data);
}
