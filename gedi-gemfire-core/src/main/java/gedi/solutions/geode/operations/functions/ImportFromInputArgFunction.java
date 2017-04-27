package gedi.solutions.geode.operations.functions;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.snapshot.SnapshotOptions.SnapshotFormat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gedi.solutions.geode.data.ExportFileType;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.global.json.JacksonJSON;


/**
 * <pre>
 * ImportJsonFromInputArgFunction import JSON data into a given region
 * where the json data is provided as an input argument.
 * 
 * If the function
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class ImportFromInputArgFunction  implements Function
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6888599942501300780L;


	
	public ImportFromInputArgFunction()
	{
	}// ------------------------------------------------
	
	public static final int CONTENT_ARG_POS = 0;
	public static final int EXTENSION_ARG_POS = 1;
	public static final int SERVER_ARG_POS = 2;
	public static final int REGION_ARG_POS = 3;

	/**          
	 * Arguments: 0=content 1=format 2=[server] 3=[region]
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext functionContext)
	{

		ResultSender<Object> sender = functionContext.getResultSender();
		

		
		Object[] args = (Object[])functionContext.getArguments();
		
		if(args == null || args.length < EXTENSION_ARG_POS+1 )
			throw new IllegalArgumentException("Required args: content extension");
		
		Cache cache = CacheFactory.getAnyInstance();
		
		LogWriter logWriter = cache.getLogger();
		
		logWriter.info("Executing "+this.getId());
		
		
		//get extension
		String extension = (String)args[EXTENSION_ARG_POS];
		if(extension == null || extension.length() == 0)
			throw new IllegalArgumentException("Required extension");
		
		ExportFileType exportFileType = ExportFileType.valueOf(extension);
		
	
		//get server name
		if(args.length > SERVER_ARG_POS)
		{
			String argServerName = (String)args[SERVER_ARG_POS];
			
			//check servername
			//get server name
			String serverName = cache.getDistributedSystem().getDistributedMember().getName();
			
			if(!serverName.equals(argServerName))
			{
				logWriter.info("argument ServerName:"+argServerName+" does not match servername:"+serverName+" so no data will be imported");
			}
			
		}
		
		
		//Process regions
		Region<Object,Object> region = null;
		if(functionContext instanceof RegionFunctionContext)
		{
			RegionFunctionContext rfc = (RegionFunctionContext) functionContext;
			region = rfc.getDataSet();
			
		}
		else
		{
			if(args.length < 3)
				throw new IllegalArgumentException("Argument name required in argument if function not executed onRegion");
			
			String regionName = (String)args[REGION_ARG_POS];
			region = cache.getRegion(regionName);
			
			if(region == null)
				throw new IllegalArgumentException("region:"+regionName+"  region not found");
		}
		
		//export data set(as a backup)
		//TODO: new JsonExportFunction().exportOnRegion(rfc);
		
		try
		{
			
			switch(exportFileType)
			{
				case gfd:
					importGfd((byte[])args[0], region, logWriter);
				break;
				case json:
					importJson((String)args[0], region, logWriter);
				break;
				default:
					throw new IllegalArgumentException("Unsupport export format:"+exportFileType);
					
			}
			
			sender.lastResult(region.size());
		}
		catch (IOException e)
		{
			String stackTrace = Debugger.stackTrace(e);
			
			logWriter.error(stackTrace);
			sender.sendException(e);
			throw new FunctionException(stackTrace);
		}
		catch (Exception e)
		{
			String stackTrace = Debugger.stackTrace(e);
			
			logWriter.error(stackTrace);
			sender.sendException(e);
			throw new FunctionException(stackTrace);
		}
			
	}// --------------------------------------------------------
	private void importGfd(byte[] fileBytes,Region<?,?> region, LogWriter logWriter) 
	throws IOException, ClassNotFoundException
	{
		//write file
		File file = DataOpsSecretary.determineFile(ExportFileType.gfd, region.getName());
		IO.writeFile(file.getAbsolutePath(), fileBytes, false);
		
		
		//load file
		region.getSnapshotService().load(file, 
				SnapshotFormat.GEMFIRE);
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @param json
	 * @param region
	 * @param logWriter
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private void importJson(String json, Region<Object,Object> region, LogWriter logWriter) 
	throws JsonParseException, JsonMappingException, JsonProcessingException, ClassNotFoundException, IOException
	{
		//validate json
		if(json == null || json.length() == 0)
			throw new IllegalArgumentException("First argument json string required ");

		StringReader reader = null;
		
		try
		{
			reader = new StringReader(json);
			logWriter.info(String.format("Importing json:%s into region:%s",json,region.getName()));
			//now import data
			JacksonJSON.populateMap(region, reader);
		}
		finally
		{
			if(reader != null) try{ reader.close(); } catch(Exception e){e.printStackTrace();}
		}
	}// --------------------------------------------------------

	@Override
	public String getId()
	{
		return "ImportFromInputArgFunction";
	}// --------------------------------------------------------

	@Override
	public boolean hasResult()
	{
		return true;
	}// --------------------------------------------------------

	@Override
	public boolean isHA()
	{
		return true;
	}

	@Override
	public boolean optimizeForWrite()
	{
		return true;
	}

}
