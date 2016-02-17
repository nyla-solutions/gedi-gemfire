package gedi.solutions.gemfire.operations.functions;

import java.io.File;
import java.util.Properties;

import nyla.solutions.global.json.JacksonJSON;
import nyla.solutions.global.util.Debugger;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

/**
 * <pre>
 * 
 * Exports the JSON string containing all region data on the server.
 * 
 * The output will be stored in the current working directory.
 * One file will be written per region (format: ${region.name}.json)
 * 
 * Example:			gfsh>execute function --id="JsonExportFunction" --region-myRegion
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class JsonExportFunction  implements Function, Declarable
{
	private String directoryPath = ".";
	
	private static String fileSeparator = System.getProperty("file.separator");
	private static String suffix = ".json";
	
	
	/**
	 * String keyFileExtension = ".key"
	 */
	public static final String keyFileExtension = ".key";
	
	public JsonExportFunction()
	{
	}// ------------------------------------------------
	
	/**
	 * Export region data in JSON format
	 */
	public void execute(FunctionContext fc)
	{
		
		ResultSender<Object> rs = fc.getResultSender();
		try
		{
			boolean didExport = false;
			
			if(fc instanceof RegionFunctionContext)
			{
				didExport = exportOnRegion((RegionFunctionContext)fc);	
			}
			else
			{
				
				//get region name from argument
				String[] args = (String[])fc.getArguments();
				
				if(args == null || args.length == 0)
					throw new IllegalArgumentException("Region name argument required");
				
				String regionName = args[0];
				
				Cache cache = CacheFactory.getAnyInstance();
				
	
				
				Region<Object,Object> region = cache.getRegion(regionName);
				
				if(region != null)
					didExport = exportRegion(region);
				else
					didExport = false;		
				
			}
			
			rs.lastResult(didExport);
		}
		catch (Exception e)
		{
			
			String stackTrace = Debugger.stackTrace(e);
			
			FunctionException functionException = new FunctionException(stackTrace);
			
			CacheFactory.getAnyInstance().getLogger().error(stackTrace);
			rs.sendException(functionException);
			throw functionException;
		}
		
	    
	}// --------------------------------------------------------
//	private boolean exportAllRegions(FunctionContext fc)
//	{
//		
//		  String[] args = (String[]) fc.getArguments();
//		    
//		    if(args == null || args.length == 0)
//		    {
//		    	throw new FunctionException("Argument not provided");
//		    }
//		    
//			
//		//Get region name from arguments
//		String regionName = args[0];
//		
//	       
//		Cache cache = CacheFactory.getAnyInstance();
//		
//		Region<Object,Object> region = cache.getRegion(regionName);
//		
//		return exportRegion(region);
//	
//	}// ------------------------------------------------

	private boolean exportOnRegion(RegionFunctionContext rfc)
	{
		//get argument 
		
		//check if region is partitioned
	
		Region<Object,Object> region = rfc.getDataSet();
	    
		
	    return exportRegion(region);
	}// ------------------------------------------------

	protected boolean  exportRegion(Region<Object, Object> region)
	{	
		if(region  == null)
			return false;
		
		if(PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}
		
		LogWriter logWriter = CacheFactory.getAnyInstance().getLogger();
		
		logWriter.info("Exporting region"+region.getName());
		
		
		//get name
	    String regionName = region.getName();
	    
		File resultFile = getRegionJsonFile(regionName);
		
		//delete previous
		logWriter.info("deleting file:"+resultFile.getAbsolutePath());
		boolean wasDeleted = resultFile.delete();
		
		logWriter.info("delete:"+wasDeleted);
		
	    try
		{
			//write data
	 		JacksonJSON.writeToFile(resultFile, region);
			
			return true;
			
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	    catch(Exception e)
	    {
	    	throw new FunctionException("Error exporting ERROR:"+ e.getMessage()+" "+Debugger.stackTrace(e));
	    }
	}// ------------------------------------------------

	private File getRegionJsonFile(String regionName)
	{
		File resultFile = new File(new StringBuilder(this.directoryPath)
		.append(fileSeparator).append(regionName).append(suffix).toString());
		return resultFile;
	}// --------------------------------------------------------

	
	public String getId()
	{
		
		return "JsonExportFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return false;
	}
	
	
	@Override
	public void init(Properties properties)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3148806554381339703L;




	
}
