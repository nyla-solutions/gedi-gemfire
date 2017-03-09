package gedi.solutions.gemfire.integration.spring.batch.gemfire;

import java.util.Collection;
import java.util.Iterator;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.gemstone.gemfire.cache.Region;

import gedi.solutions.gemfire.io.GemFireIO;
import nyla.solutions.global.exception.SetupException;

public class RegionItemReader implements ItemReader<Object>
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeStep
	public void open()
	{
		try
		{
			Collection<Object> keySet = GemFireIO.keySetOnServer(this.region);
			
			this.iterator = keySet.iterator();
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new SetupException(e);
		}
		
	}//--------------------------------------------------------
	public Object read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException
	{
		if(!iterator.hasNext())
			return null;
		
		return this.region.get(iterator.next());
	}//--------------------------------------------------------

	
	/**
	 * @return the region
	 */
	public Region<?, ?> getRegion()
	{
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(Region<Object, ?> region)
	{
		this.region = region;
	}


	private Iterator<Object> iterator  = null;
	private Region<Object,?> region;
	
}
