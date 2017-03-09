package gedi.solutions.geode.operations.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

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

import gedi.solutions.geode.data.ExportFileType;
import nyla.solutions.core.io.IO;
import nyla.solutions.global.json.JacksonJSON;

/**
 * <pre>
 * Import region data using exported JSON formatted data.
 * 
 * Example: 
 * gfsh>execute function --id="ImportJsonFunction" --region=/securityRegion
 * 
 * </pre>
 * 
 * @see JsonExportFunction
 * @author Gregory Green
 * 
 */
public class JsonImportFunction implements Function, Declarable
{


	public JsonImportFunction()
	{
	}// ------------------------------------------------

	public void execute(FunctionContext fc)
	{
		ResultSender<Object> rs = fc.getResultSender();

		try
		{
			boolean results = false;
			if (fc instanceof RegionFunctionContext)
			{
				results = importOnRegion((RegionFunctionContext) fc);
			} else
			{
				String[] args = (String[])fc.getArguments();
				
				if(args == null || args.length == 0)
					throw new IllegalArgumentException("Arguments with region name required");
				
				String regionName = args[0];
				this.importRegion(regionName);
			}

			rs.lastResult(results);
		}
		catch (Exception e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			CacheFactory.getAnyInstance().getLogger().error(sw.toString());
			rs.sendException(e);
			
			throw new FunctionException(sw.toString());

		}

	}// --------------------------------------------------------

	private boolean importRegion(String regionName) throws Exception
	{
		if(regionName == null || regionName.length() == 0)
			return false;

		Cache cache = CacheFactory.getAnyInstance();

		Region<Object, Object> region = cache.getRegion(regionName);

		return importRegion(region);

	}// ------------------------------------------------

	protected boolean importOnRegion(RegionFunctionContext rfc) throws Exception
	{
		// get argument

		// check if region is partitioned

		Region<Object, Object> region = rfc.getDataSet();

		return importRegion(region);
	}// ------------------------------------------------
	/**
	 * Import exported data from a given
	 * @param region the region to be import
	 * @return true is the import was successful
	 * @throws Exception
	 */
	private boolean importRegion(Region<Object, Object> region)
			throws Exception
	{
		if(region == null)
			return false;
		
		if (PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}

		// get file

		File file = DataOpsSecretary.determineFile(ExportFileType.json, region.getName());

		if (!file.exists())
		{
			CacheFactory.getAnyInstance().getLogger()
					.config(file.getAbsolutePath() + " does not exists");
			return false;
		}


        BufferedReader reader = null;

        try
        {
        	reader = new BufferedReader(
        	           new InputStreamReader(
        	                      new FileInputStream(file), IO.CHARSET));
        	
        	JacksonJSON.populateMap(region, reader);
        	
        	boolean result =  true;
        	
		
        	return result;
        }
        finally
        {
        	try { if(reader !=null) reader.close(); } catch(Exception e){e.printStackTrace();}
        }
		
	}// ------------------------------------------------

	

	/***
	 * @return ImportJsonFunction
	 */
	public String getId()
	{

		return "JsonImportFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return true;
	}

	public boolean optimizeForWrite()
	{
		return true;
	}// --------------------------------------------------------
	@Override
	public void init(Properties properties)
	{
	}// --------------------------------------------------------


	/**
	 * 
	 */
	private static final long serialVersionUID = -3148806554381339703L;


}
