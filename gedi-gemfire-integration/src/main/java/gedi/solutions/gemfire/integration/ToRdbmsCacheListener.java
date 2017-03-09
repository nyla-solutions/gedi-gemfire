package gedi.solutions.gemfire.integration;

import java.io.Serializable;
import java.util.Properties;
import nyla.solutions.global.patterns.servicefactory.ServiceFactory;
import org.springframework.data.repository.CrudRepository;
import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.RegionEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

/**
 * 
 * @author Gregory Green
 *
 */
public class ToRdbmsCacheListener extends CacheListenerAdapter<Object,Object> 
implements CacheListener<Object, Object>, Declarable
{

	public void close()
	{
	}// --------------------------------------------------------

	@Override
	public void afterRegionCreate(RegionEvent<Object, Object> event)
	{
		event.getRegion().registerInterestRegex(".*");
		
	}// --------------------------------------------------------
	public void afterCreate(EntryEvent<Object, Object> event)
	{	
		if(crudRepositoryName == null || crudRepositoryName.length() == 0)
			throw new IllegalStateException(ToRdbmsCacheListener.class.getName()
					+" not provided with crudRepositoryName. Example: "+
					"<parameter name=\"crudRepositoryName\"><string>employeeRepository</string></parameter>");
		

		
		CrudRepository<Object, Serializable> crudRepository = 
				ServiceFactory.getInstance().create(this.crudRepositoryName);
		
		crudRepository.save(event.getNewValue());
		
	}// --------------------------------------------------------
	/**
	 * Save to RDBMS after a region update
	 * @see com.gemstone.gemfire.cache.util.CacheListenerAdapter#afterUpdate(com.gemstone.gemfire.cache.EntryEvent)
	 */
	public void afterUpdate(EntryEvent<Object, Object> event)
	{
		

		
		if(crudRepositoryName == null || crudRepositoryName.length() == 0)
			throw new IllegalStateException(ToRdbmsCacheListener.class.getName()
					+" not provided with crudRepositoryName. Example: "+
					"<parameter name=\"crudRepositoryName\"><string>employeeRepository</string></parameter>");

		
		final Object newValue = event.getNewValue();
		final Serializable key = (Serializable)event.getKey();
		
		if(GemIntegrationApp.getInstance().getInflight().keySet().contains(key))
			return;
		
		new Thread(new Runnable()
		{
			
			public void run()
			{
				try
				{
					CrudRepository<Object, Serializable> crudRepository = 
							ServiceFactory.getInstance().create(crudRepositoryName);
					
					GemIntegrationApp.getInstance().getInflight().put(key, "");
					
					final Object  oldValue = crudRepository.findOne(key);
					
					
					
					if(!newValue.equals(oldValue))
						crudRepository.save(newValue);
					
					
					GemIntegrationApp.getInstance().getInflight().remove(key);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}).start();
		
		
	}// --------------------------------------------------------	
	
	public void init(Properties properties)
	{
		crudRepositoryName = properties.getProperty("crudRepositoryName");
		
		regionName =  properties.getProperty("regionName");
		
		
		if(regionName == null)
			throw new IllegalArgumentException("regionName parameter is required");
		
		if(crudRepositoryName == null)
			throw new IllegalArgumentException("crudRepositoryName parameter is required");
		
		
		//Region<Object,Object> region = CacheFactory.getAnyInstance().getRegion(regionName);

		
	}

	private String crudRepositoryName = null; 
	private String regionName;


}
