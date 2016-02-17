package gedi.solutions.gemfire.io;

import java.io.Serializable;

import com.gemstone.gemfire.cache.execute.ResultSender;

/**
 * Interface to result function/execution results
 * @author Gregory Green
 *
 */
public interface BridgeResultSender extends ResultSender<Serializable>
{
	/**
	 * Uses a result sender to send data to a client
	 * @param resultSender GemFire result sender
	 * @param data the data used to send
	 */
	public void sendResults(ResultSender<Serializable> resultSender, Object data);
}
