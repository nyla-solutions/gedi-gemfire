package gedi.solutions.geode.data.catalog;


import java.io.Serializable;
import java.util.Arrays;

import nyla.solutions.core.patterns.transaction.Transactional;


/**
 * <pre>
 * Contains meta on how functions should be executed.
 * 
 * Sample Setup (attributes represented in XML)
 * 
 *   &lt;entry&gt;
    &lt;string&gt;func1&lt;/string&gt;
    &lt;solutions.gedi.data.catalog.FunctionFacts&gt;
      &lt;gridFunctionName&gt;bridgeFunction&lt;/gridFunctionName&gt;
      &lt;inputName&gt;REQUEST&lt;/inputName&gt;
      &lt;executionType&gt;onServer&lt;/executionType&gt;
      &lt;functionName&gt;testFunction&lt;/functionName&gt;
      &lt;regionName&gt;READ-PATIENT&lt;/regionName&gt;
      &lt;onRegionFilterKeyFacts&gt;
        &lt;solutions.gedi.data.catalog.OnRegionFilterKeyFacts&gt;
          &lt;attributesType&gt;filterKey&lt;/attributesType&gt;
          &lt;attributeFacts&gt;
            &lt;solutions.gedi.data.catalog.OnRegionFilterKeyAttributeFacts&gt;
              &lt;name&gt;Attrib1&lt;/name&gt;
              &lt;startIndex&gt;1&lt;/startIndex&gt;
              &lt;length&gt;1&lt;/length&gt;
              &lt;className&gt;java.lang.String&lt;/className&gt;
              &lt;encoding&gt;Cp1047&lt;/encoding&gt;
            &lt;/solutions.gedi.data.catalog.OnRegionFilterKeyAttributeFacts&gt;
            &lt;solutions.gedi.data.catalog.OnRegionFilterKeyAttributeFacts&gt;
              &lt;name&gt;Attrib2&lt;/name&gt;
              &lt;startIndex&gt;2&lt;/startIndex&gt;
              &lt;length&gt;2&lt;/length&gt;
              &lt;className&gt;java.lang.String&lt;/className&gt;
              &lt;encoding&gt;Cp1047&lt;/encoding&gt;
            &lt;/solutions.gedi.data.catalog.OnRegionFilterKeyAttributeFacts&gt;
          &lt;/attributeFacts&gt;
        &lt;/solutions.gedi.data.catalog.OnRegionFilterKeyFacts&gt;
        &lt;solutions.gedi.data.catalog.OnRegionFilterKeyFacts&gt;
          &lt;attributesType&gt;filterKey&lt;/attributesType&gt;
        &lt;/solutions.gedi.data.catalog.OnRegionFilterKeyFacts&gt;
      &lt;/onRegionFilterKeyFacts&gt;
      &lt;transactionStatus&gt;WRITE&lt;/transactionStatus&gt;
    &lt;/solutions.gedi.data.catalog.FunctionFacts&gt;
  &lt;/entry&gt;
&lt;/map&gt;
</pre>
 * @author Gregory Green
 *
 */
public class FunctionFacts implements Serializable, Transactional
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4219290774337042571L;
	
	/**
	 *  onRegion,
		onMember,
		onMembers,		
		onServers,		
		onServer
	 * @author Gregory 
	 *
	 */
	public enum ExecutionType
	{
		onRegion,
		onMember,
		onMembers,		
		onServers,		
		onServer
	};
	
	/**
	 * @return the executionType (onRegion, onServer, etc)
	 */
	public ExecutionType getExecutionType()
	{
		return executionType;
	}// -----------------------------------------------
	
	/**
	 * @param executionType the executionType to set (onRegion, onServer, etc)
	 */
	public void setExecutionType(ExecutionType executionType)
	{
		if (executionType == null)
			throw new IllegalArgumentException("executionType required");
		
		this.executionType = executionType;
	}// -----------------------------------------------
	/**
	 * @return the functionName
	 */
	public String getFunctionName()
	{
		return functionName;
	}
	/**
	 * @param functionName the functionName to set
	 */
	public void setFunctionName(String functionName)
	{
		this.functionName = functionName;
	}
	
	/**
	 * Note the default input name is REQUEST
	 * @return the inputName (i.e. the request container name for CICS bridge calls)
	 */
	public String getInputName()
	{
		return inputName;
	}
	/**
	 * @param inputName the inputName to set (i.e. the request container name for CICS bridge calls)
	 */
	public void setInputName(String inputName)
	{
		this.inputName = inputName;
	}
	

	/**
	 * Note the default input name is RESPONSE
	 * @return the outputName (i.e. the response container name for CICS bridge calls)
	 */
	public String getOutputName()
	{
		return outputName;
	}

	/**
	 * @param outputName the outputName to set
	 */
	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	/**
	 * Get pool name to use for onServer or onServers execution types
	 * @return the poolName the pool name
	 */
	public String getPoolName()
	{
		return poolName;
	}// -----------------------------------------------
	/**
	 * Set the pool name to use for onServer or onServers execution types
	 * @param poolName the poolName to set
	 */
	public void setPoolName(String poolName)
	{
		this.poolName = poolName;
	}// -----------------------------------------------


	/**
	 * On region filter facts determine how to parse the inputs typically for CICS to grid calls
	 * @return the onRegionFilterKeyFacts 
	 */
	public OnRegionFilterKeyFacts[] getOnRegionFilterKeyFacts()
	{
		return onRegionFilterKeyFacts;
	}
	/**
	 * @param onRegionFilterKeyFacts the onRegionFilterKeyFacts to set
	 */
	public void setOnRegionFilterKeyFacts(
			OnRegionFilterKeyFacts[] onRegionFilterKeyFacts)
	{
		this.onRegionFilterKeyFacts = onRegionFilterKeyFacts;
	}// -----------------------------------------------
	/**
	 * Set the name of the region for onRegion calls
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}// -----------------------------------------------

	

	/**
	 * Set the name of the region for onRegion calls
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
	}// -----------------------------------------------

	/**
	 * Set the transaction type of the function
	 * TransactionType.NONE - no transaction support
	 * TransactionType.READONLY- read (not write) transaction
	 * TransactionType.WRITE - read/write or read transaction data
	 */
	//@Override
	public void setTransactionType(TransactionType transactionType)
	{
		this.transactionType = transactionType;
	}// -----------------------------------------------
	/**
	 * Get the current transaction type
	 */
	//@Override
	public TransactionType getTransactionType()
	{
		return this.transactionType;
	}// -----------------------------------------------
	
	

	/**
	 * Gemfire bridge function name
	 * @return the gridFunctionName
	 */
	public String getGridFunctionName()
	{
		return gridFunctionName;
	}// -----------------------------------------------
	/**
	 * Set Gemfire bridge function name
	 * @param gridFunctionName the gridFunctionName to set
	 */
	public void setGridFunctionName(String gridFunctionName)
	{
		this.gridFunctionName = gridFunctionName;
	}

	


	/**
	 * @return the notes
	 */
	public String getNotes()
	{
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes)
	{
		this.notes = notes;
	}// -----------------------------------------------
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((executionType == null) ? 0 : executionType.hashCode());
		result = prime * result
				+ ((functionName == null) ? 0 : functionName.hashCode());
		result = prime
				* result
				+ ((gridFunctionName == null) ? 0 : gridFunctionName.hashCode());
		result = prime * result
				+ ((inputName == null) ? 0 : inputName.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		//result = prime * result + Arrays.hashCode(OnRegionFilterKeyFacts);
		result = prime * result
				+ ((outputName == null) ? 0 : outputName.hashCode());
		result = prime * result
				+ ((poolName == null) ? 0 : poolName.hashCode());
		result = prime * result
				+ ((regionName == null) ? 0 : regionName.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result
				+ ((transactionType == null) ? 0 : transactionType.hashCode());
		return result;
	}// -----------------------------------------------

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
		FunctionFacts other = (FunctionFacts) obj;
		if (executionType != other.executionType)
			return false;
		if (functionName == null)
		{
			if (other.functionName != null)
				return false;
		}
		else if (!functionName.equals(other.functionName))
			return false;
		if (gridFunctionName == null)
		{
			if (other.gridFunctionName != null)
				return false;
		}
		else if (!gridFunctionName.equals(other.gridFunctionName))
			return false;
		if (inputName == null)
		{
			if (other.inputName != null)
				return false;
		}
		else if (!inputName.equals(other.inputName))
			return false;
		if (notes == null)
		{
			if (other.notes != null)
				return false;
		}
		else if (!notes.equals(other.notes))
			return false;
		if (!Arrays
				.equals(onRegionFilterKeyFacts, other.onRegionFilterKeyFacts))
			return false;
		if (outputName == null)
		{
			if (other.outputName != null)
				return false;
		}
		else if (!outputName.equals(other.outputName))
			return false;
		if (poolName == null)
		{
			if (other.poolName != null)
				return false;
		}
		else if (!poolName.equals(other.poolName))
			return false;
		if (regionName == null)
		{
			if (other.regionName != null)
				return false;
		}
		else if (!regionName.equals(other.regionName))
			return false;
		if (serviceName == null)
		{
			if (other.serviceName != null)
				return false;
		}
		else if (!serviceName.equals(other.serviceName))
			return false;
		if (transactionType != other.transactionType)
			return false;
		return true;
	}// -----------------------------------------------

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "FunctionFacts [functionName=" + functionName + ", executionType="
				+ executionType + ", inputName=" + inputName
				+ ", outputName=" + outputName
				+ ", serviceName=" + serviceName
				+ ", notes=" + notes
				+ ", regionName=" + regionName + ", poolName=" + poolName
				+ ", onRegionFilterKeyFacts="
				+ Arrays.toString(onRegionFilterKeyFacts)
				+ ", transactionType=" + transactionType
				+ ", gridFunctionName=" + gridFunctionName + "]";
	}// -----------------------------------------------
	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		return serviceName;
	}// -----------------------------------------------
	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	

	private String notes = null;

	private String serviceName = "";
	private String inputName = "default";
	private String outputName = "output";
	private ExecutionType executionType = ExecutionType.onServer;
	private String functionName = null;
	private String regionName = null;
	private String poolName = "default";
	private OnRegionFilterKeyFacts[] onRegionFilterKeyFacts =null;
	private TransactionType transactionType = TransactionType.WRITE;
	private String gridFunctionName = "bridge";

}
