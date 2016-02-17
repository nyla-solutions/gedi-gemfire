package gedi.solutions.gemfire.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Used to represent an filter for an onRegion function call
 * @author Gregory Green
 *
 */
public class FilterKey implements Serializable
{
	/**
	 * Default constructor
	 */
	public FilterKey()
	{
	}// -----------------------------------------------
	/**
	 * Create with initial keys
	 * @param keys the initial keys
	 */
	public FilterKey(Serializable[] keys)
	{
		this.keys = keys;
	}// -----------------------------------------------
	/**
	 * 
	 */
	private static final long serialVersionUID = 1763375876527572807L;

	/**
	 * @return the keys
	 */
	public Serializable[] getKeys()
	{
		return keys;
	}

	/**
	 * @param keys the keys to set
	 */
	public void set(Serializable[] keys)
	{
		this.keys = keys;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keys);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterKey other = (FilterKey) obj;
		if (!Arrays.equals(keys, other.keys))
			return false;
		return true;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "FilterKey [keys=" + Arrays.toString(keys) + "]";
	}
	private Serializable[] keys;
	
}
