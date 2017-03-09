package gedi.solutions.geode.data;

import java.io.Serializable;
import java.util.Map;

/**
 * This object encapsulates a request and response.
 * @author Gregory Green
 *
 */
public class GediEnvelope implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 543197843593634035L;
	/**
	 * @return the header
	 */
	public final Map<Serializable, Serializable> getHeader()
	{
		return header;
	}// --------------------------------------------
	/**
	 * @param header the header to set
	 */
	public final void setHeader(Map<Serializable, Serializable> header)
	{
		this.header = header;
	}// --------------------------------------------
	
	/**
	 * @return the pay load
	 */
	public final  Serializable getPayload()
	{
		return payload;
	}
	/**
	 * @param payload the pay load to set
	 */
	public final void setPayload(Serializable payload)
	{
		this.payload = payload;
	}// ------------------------------------------------'	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Envelope [header=" + header + ", payload=" + payload + "]";
	}



	private Map<Serializable,Serializable> header = null;
	private Serializable payload  = null;
}
