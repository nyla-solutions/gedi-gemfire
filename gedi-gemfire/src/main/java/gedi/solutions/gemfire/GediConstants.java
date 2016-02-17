package gedi.solutions.gemfire;


public interface GediConstants 
{
	/**
	 * TRANSACTION_CONTAINER_NAME = CicsConstants.TRANSACTION_CONTAINER_NAME
	 */
	public static final String TRANSACTION_CONTAINER_NAME = "TRANSACTION";
	
	/**
	 * DEFAULT_FunctionName = "bridgeFunction"
	 */
	public static final String DEFAULT_GridFunctionName = "bridgeFunction";
	
	/**
	 * TRANSACTION_NAME_HEADER = "transactionName"
	 */
	public static final String TRANSACTION_NAME_HEADER = "TRANSACTION_NAME";
	
	
	/**
	 * QNAME_HEADER = "QNAME"
	 */
	public static final String QNAME_HEADER = "QNAME";
	
	/**
	 * USERID_HEADER = "USERID"
	 */
	public static final String USERID_HEADER = "USERID";

	
	/**
	 * STARTCODE_HEADER = "STARTCODE"
	 */
	public static final String STARTCODE_HEADER = "STARTCODE";
	
	/**
	 * TASKNUMBER_HEADER = "TASK_NUMBER"
	 */
	public static final String TASK_NUMBER_HEADER = "TASK_NUMBER";
	

	
	/**
	 * CONTAINER_FUNCTION_NAME =  GediConstants.FUNCTION_NAME_HEADER
	 */
	public static final String CONTAINER_FUNCTION_NAME =  GediConstants.FUNCTION_NAME_HEADER;
	

	
	/**
	 * DEFAULT_FUNCTION_CATALOG_REGION = "__FUNCTION_CATALOG"
	 */
	public static final String DEFAULT_FUNCTION_CATALOG_REGION = "__FUNCTION_CATALOG";
	/**
	 * DEFAULT_POOL_NAME = "client"
	 */
	public static final String DEFAULT_POOL_NAME = "client";
	
	/**
	 * DEFAULT_BRIDGE_FUNCTION_NAME = "bridgeFunction"
	 */
	public static final String DEFAULT_BRIDGE_FUNCTION_NAME = "bridgeFunction";
	
	/**
	 * TXID_HEADER = e.g. "TXID", property configured in GediConstants.properties
	 */
	public static final String TXID_HEADER = "TXID";
	
	/**
	 * PROGRAM_NAME_HEADER = "PROGRAM_NAME"
	 */
	public static final String PROGRAM_NAME_HEADER = "PROGRAM_NAME";
	
	/**
	 * DEFAULT_EXCEPTIONS_REGION = "__EXCEPTIONS"
	 */
	public static final String DEFAULT_EXCEPTIONS_REGION = "__EXCEPTIONS";
	
	/**
	 * GRID_GENERAL_ERROR_CATEGORY = "GF-GENERAL"
	 */
	public static final String GRID_GENERAL_ERROR_CATEGORY = "GFGEN";
	
	/**
	 * GRID_TX__ERROR_CATEGORY = "GFTRA"
	 */
	public static final String GRID_TX_ERROR_CATEGORY = "GFTRA";
	
	/**
	 * JCICS_ERROR_CATEGORY = "JCICS"
	 */
	public static final String JCICS_ERROR_CATEGORY = "JCICS";
	/**
	 * ORGINAL_FUNCTION_ID_HEADER = "ORGINAL_FUNCTION_ID"
	 */
	public static final String ORGINAL_FUNCTION_ID_HEADER = "ORGINAL_FUNCTION_ID";
	/**
	 * ORGINAL_ARGUMENT_HEADER = "ORGINAL_ARGUMENT"
	 */
	public static final String ORGINAL_ARGUMENT_HEADER = "ORGINAL_ARGUMENT";
	
	/**
	 * DEFAULT_INPUT_NAME = "REQUEST"
	 */
	public static final String DEFAULT_INPUT_NAME = "REQUEST";
	
	/**
	 * DEFAULT_OUTPUT_NAME = "RESPONSE"
	 */
	public static final String DEFAULT_OUTPUT_NAME = "RESPONSE";
	
	/**
	 * FILTER_HEADER = "FILTER"
	 */
	public static final String FILTER_HEADER = "FILTER";
	/**
	 * REGION_HEADER = "REGION"
	 */
	public static final String REGION_HEADER = "REGION";
	
	/**
	 * FUNCTION_NAME_HEADER = "FUNCTION"
	 */
	public static final String FUNCTION_NAME_HEADER = "FUNCTION"; 
	
	/**
	 * UOWID_HEADER = "UOWID"
	 */
	public static final String UOW_HEADER = "UOW";
	
	
	
	/**
	 * RECOVERYAREA_MAX_LENGTH = e.g. 4090, the maximum count of System-Id + Application-Id allowed in the recovery area.
	 */
	public static final Integer RECOVERYAREA_MAX_LENGTH = 4090;
	
}

