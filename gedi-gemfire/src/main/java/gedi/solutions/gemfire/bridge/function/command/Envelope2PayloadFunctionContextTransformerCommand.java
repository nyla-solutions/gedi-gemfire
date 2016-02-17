/**
 * 
 */
package gedi.solutions.gemfire.bridge.function.command;


import nyla.solutions.global.data.Envelope;
import nyla.solutions.global.patterns.command.Command;

/**
 * Assumes the argument is an envelope and return the payload
 * @author Gregory Green
 *
 */
public class Envelope2PayloadFunctionContextTransformerCommand implements Command<Object,Object>
{


	/**
	 * Changes the first array object into a payload from the Envelope
	 * returnArray[0] = ((Envelope)envFuncContextArrayObject[0]).getPayload()
	 * @param envFuncContextArrayObject 2 object array 0= Envelope, 1=FunctionContext
	 * @throws ClassCastException if the argument is not an Envelope
	 * 
	 */
	//@Override
	@SuppressWarnings("rawtypes")
	public Object execute(Object envFuncContextArrayObject)
	{
		
		Object[] payloadFuncContextArrayObject = (Object[])envFuncContextArrayObject;
		
		payloadFuncContextArrayObject[0] = ((Envelope)payloadFuncContextArrayObject[0]).getPayload();
		
		return payloadFuncContextArrayObject;
	}// -----------------------------------------------

}
