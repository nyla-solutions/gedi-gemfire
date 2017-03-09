/**
 * 
 */
package gedi.solutions.geode.bridge.function.command;

import nyla.solutions.commas.Command;
import nyla.solutions.core.data.Envelope;

/**
 * Assumes the argument is an envelope and returns the payload
 * @author Gregory Green
 *
 */
public class Envelope2PayloadTransformerCommand implements Command<Object,Object>
{
	/**
	 * @param envFuncContext an array containing the envelope and FunctionContext
	 * @throws ClassCastException if the argument is not an Envelope
	 * 
	 */
	//@Override
	@SuppressWarnings("unchecked")
	public Object execute(Object envFuncContextObject)
	{
		return ((Envelope<Object>)((Object[])envFuncContextObject)[0]).getPayload();
	}// -----------------------------------------------

}
