package gedi.solutions.gemfire.operations.functions;

import gedi.solutions.gemfire.data.ExportFileType;

import java.io.File;


/**
 * Helper class for data manager operations
 * @author Gregory Green
 *
 */
public class DataOpsSecretary
{
	/**
	 * directoryPath = System.getProperty("io.pivotal.gemfire_addon.dataOps.DataOpsSecretary.directoryPath",".")
	 */
	public static final String directoryPath = System.getProperty("io.pivotal.gemfire_addon.dataOps.DataOpsSecretary.directoryPath",".");
	
	private static String fileSeparator = System.getProperty("file.separator");
	
	public  static final String EXPORT_FILE_TYPE_USAGE = "(gfd|json)";
	
	/**
	 * 
	 * @param exportFileType
	 * @param regionName
	 * @return
	 */
	public static File determineFile(ExportFileType exportFileType,String regionName)
	{
		
		File resultFile = new File(new StringBuilder(directoryPath)
		.append(fileSeparator).append(regionName).append(".").append(exportFileType).toString());
		return resultFile;
	}// --------------------------------------------------------

	/**
	 * 
	 * @param extension the value
	 * @return ExportFileType
	 */
	public static ExportFileType determineType(String extension)
	{
		try
		{
			return ExportFileType.valueOf(extension);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Exported type extension:"+EXPORT_FILE_TYPE_USAGE);
		}
		
	}// --------------------------------------------------------
}
