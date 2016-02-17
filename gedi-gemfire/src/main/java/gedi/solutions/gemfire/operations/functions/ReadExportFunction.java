package gedi.solutions.gemfire.operations.functions;

import gedi.solutions.gemfire.data.ExportFileType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import nyla.solutions.global.io.IO;
import nyla.solutions.global.util.Debugger;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultSender;


/**
 * <pre>
 * 
 * Exports the JSON string of all region data on the server.
 * 
 * The output will be stored in the current working directory.
 * One file will be written per region (format: ${region.name}.json)
 * 
 * Example:			gfsh>execute function --id="ExportJsonFunction" --arguments=myRegion
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class ReadExportFunction  implements Function
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6888599942501300780L;


	private static final String directoryPath = ".";

	
	
	public ReadExportFunction()
	{
	}// ------------------------------------------------

	/**
	 * This function will use the JsonExportFunction function
	 * to export json data and read the results to be returned to callers
	 * 
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext functionContext)
	{

		ResultSender<Object> sender = functionContext.getResultSender();
		Cache cache = CacheFactory.getAnyInstance();
		LogWriter logWriter = cache.getLogger();
		
		try
		{
			//export data
			
			String[] args = (String[])functionContext.getArguments();
			
			if(args == null || args.length != 2 )
				throw new FunctionException("Required array args: [region,extension]");

			String extensionArg = args[0];
			if(extensionArg == null || extensionArg.length() == 0)
			{
				throw new IllegalArgumentException("File extension required");
			}
			ExportFileType extension =  ExportFileType.valueOf(extensionArg);
			String regionName = args[1]; //TODO: accept multiple regions
			
			
			
			Region<Object,Object> region = cache.getRegion(regionName);
			
			if(region == null)
			{
				sender.lastResult(null);
				return;
			}
			
			//TODO: get file from functions
			File file = new File(new StringBuilder(directoryPath).append("/").append(regionName)
					.append(".").append(extensionArg).toString());
			
			
			//get server name
			String serverName = cache.getDistributedSystem().getDistributedMember().getName();
			
			switch(extension)
			{
				case gfd:
					new GfdExportFunction().exportRegion(region);
				break;
				case json:
					new JsonExportFunction().exportRegion(region);
				break;
				default:
					throw new IllegalArgumentException("Unsupported extension file type:"+extension);
			}
			
			Serializable content = readContent(file,extension,logWriter);
			
			Serializable[] arrayResults = {serverName,content,file.getAbsolutePath()};
			
			sender.lastResult(arrayResults);
		}
		catch (Exception e)
		{
			String stackTrace = Debugger.stackTrace(e);
			logWriter.error(stackTrace);
			
			throw new FunctionException(stackTrace);
		}
			
	}// --------------------------------------------------------
	private Serializable readContent(File file, ExportFileType exportFileType, LogWriter logWriter)
	throws IOException
	{
		String filePath = file.getAbsolutePath();
		
		logWriter.info("reading "+filePath);
		
		switch(exportFileType)
		{
			case gfd: return IO.readBinaryFile(file);
			
			case json: return IO.readFile(file.getAbsolutePath());
			default:
				throw new RuntimeException("Unknown extension file type:"+exportFileType);
		}
				
	}// --------------------------------------------------------

	@Override
	public String getId()
	{
		return "ReadExportFunction";
	}// --------------------------------------------------------

	@Override
	public boolean hasResult()
	{
		return true;
	}// --------------------------------------------------------

	@Override
	public boolean isHA()
	{
		return false;
	}

	@Override
	public boolean optimizeForWrite()
	{
		return false;
	}

}
