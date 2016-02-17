package gedi.solutions.gemfire.operations.functions;


import gedi.solutions.gemfire.data.ExportFileType;

import java.io.File;
import java.util.Properties;

import nyla.solutions.global.util.Debugger;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.snapshot.SnapshotOptions.SnapshotFormat;

/**
 * <pre>
 * Import region data using exported GFD formatted data.
 * 
 * @author Gregory Green
 * 
 */
public class GfdImportFunction implements Function, Declarable
{


	public GfdImportFunction()
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
			String stackTrace = Debugger.stackTrace(e);

			CacheFactory.getAnyInstance().getLogger().error(stackTrace);
			rs.sendException(e);
			
			throw new FunctionException(stackTrace);
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
		File file = DataOpsSecretary.determineFile(ExportFileType.gfd, region.getName());
		
		if(!file.exists())
			return false;
		
		region.getSnapshotService().load(file, SnapshotFormat.GEMFIRE);
		
		return true;
	}// ------------------------------------------------

	
	

	/***
	 * @return ImportJsonFunction
	 */
	public String getId()
	{

		return "GfdImportFunction";
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
