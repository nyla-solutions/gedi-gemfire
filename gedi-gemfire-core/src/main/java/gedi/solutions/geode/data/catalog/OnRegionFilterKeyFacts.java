package gedi.solutions.geode.data.catalog;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Container that indicates information on how filter key attributes are represented.
 * 
 * @author Gregory Green
 *
 */
public class OnRegionFilterKeyFacts implements Serializable
{
	/**
	 * Indicate the data type that should be used to represent the filter key
	 * filterKey = instance of solutions.gedi.data.FilterKey (default)
	 * array = instance of Serializable[]
	 * map = instance of  java.util.Map 
	 * @author Gregory Green
	 *
	 */
	public static enum AttributesType
	{
		filterKey,
		array,
		map
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6950592458545764986L;

	/**
	 * @return the attributeFacts
	 */
	public OnRegionFilterKeyAttributeFacts[] getAttributeFacts()
	{
		return attributeFacts;
	}

	/**
	 * @param attributeFacts the attributeFacts to set
	 */
	public void setAttributeFacts(OnRegionFilterKeyAttributeFacts[] attributeFacts)
	{
		this.attributeFacts = attributeFacts;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(attributeFacts);
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
		OnRegionFilterKeyFacts other = (OnRegionFilterKeyFacts) obj;
		if (!Arrays.equals(attributeFacts, other.attributeFacts))
			return false;
		return true;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "OnRegionFilterKeyFacts [attributeFacts="
				+ Arrays.toString(attributeFacts) + "]";
	}
	/**
	 * @return the attributesType
	 */
	public AttributesType getAttributesType()
	{
		return attributesType;
	}

	/**
	 * @param attributesType the attributesType to set
	 */
	public void setAttributesType(AttributesType attributesType)
	{
		this.attributesType = attributesType;
	}


	private AttributesType attributesType = AttributesType.filterKey;
	private OnRegionFilterKeyAttributeFacts[] attributeFacts;
 }
