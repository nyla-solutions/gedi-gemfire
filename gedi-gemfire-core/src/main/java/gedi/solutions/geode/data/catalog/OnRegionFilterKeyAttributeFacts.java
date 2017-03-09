package gedi.solutions.geode.data.catalog;

import java.io.Serializable;
import java.util.Arrays;

import nyla.solutions.core.util.Config;

/**
 * Contains meta-data on how on region filter keys should be created
 * @author Gregory Green
 *
 */
public class OnRegionFilterKeyAttributeFacts implements Serializable
{
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * @return the startIndex
	 */
	public int getStartIndex()
	{
		return startIndex;
	}
	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
	/**
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(int length)
	{
		this.length = length;
	}
	/**
	 * @return the className
	 */
	public String getClassName()
	{
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}
	/**
	 * @return the encoding
	 */
	public String getEncoding()
	{
		return encoding;
	}
	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + length;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + startIndex;
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
		OnRegionFilterKeyAttributeFacts other = (OnRegionFilterKeyAttributeFacts) obj;
		if (className == null)
		{
			if (other.className != null)
				return false;
		}
		else if (!className.equals(other.className))
			return false;
		if (encoding == null)
		{
			if (other.encoding != null)
				return false;
		}
		else if (!encoding.equals(other.encoding))
			return false;
		if (length != other.length)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (startIndex != other.startIndex)
			return false;
		return true;
	}
	/**
	 * @return the attributes
	 */
	public FunctionAttribute[] getAttributes()
	{
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(FunctionAttribute[] attributes)
	{
		this.attributes = attributes;
	}// -----------------------------------------------
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "OnRegionFilterKeyAttributeFacts [name=" + name
				+ ", startIndex=" + startIndex + ", attributes="
				+ Arrays.toString(attributes) + ", length=" + length
				+ ", className=" + className + ", encoding=" + encoding + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8906648993823329772L;
	private String name;
	private int startIndex;
	private FunctionAttribute[] attributes = null;
	private int length;
	private String className;
	private String encoding = Config.getProperty(OnRegionFilterKeyAttributeFacts.class,"encoding","");
}
