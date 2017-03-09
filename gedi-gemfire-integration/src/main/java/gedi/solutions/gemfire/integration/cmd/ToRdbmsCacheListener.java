package gedi.solutions.gemfire.integration.cmd;

import java.io.Serializable;
import java.util.Map;

import nyla.solutions.global.util.Debugger;

import org.springframework.data.repository.CrudRepository;

import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

/**
 * To Relation database management system CacheListener
 * @author Gregory Green
 *
 */
public class ToRdbmsCacheListener extends CacheListenerAdapter<Object,Object> 
implements CacheListener<Object, Object>
{
	@Override
	public void afterDestroy(EntryEvent<Object, Object> event)
	{
		Serializable key = (Serializable)event.getKey();
		
		if(this.inflightMap != null && this.inflightMap.keySet().contains(key))
			return;
		
		if(this.inflightMap != null)
			this.inflightMap.put(key, Boolean.TRUE);
		
		Debugger.println(this,"Delete key:"+event.getKey());
		
		crudRepository.delete(key);

		if(this.inflightMap != null)
			this.inflightMap.remove(key);
	}// --------------------------------------------------------
	public void afterCreate(EntryEvent<Object, Object> event)
	{	
		Object key = event.getKey();
		
		if(this.inflightMap != null && this.inflightMap.keySet().contains(key))
			return;
		
		if(this.inflightMap != null)
			this.inflightMap.put(key, Boolean.TRUE);
		
		
		Debugger.println(this,"Creating entry for key:"+event.getKey());
		
		crudRepository.save(event.getNewValue());
		
		
		if(this.inflightMap != null)
			this.inflightMap.remove(key);
	}// --------------------------------------------------------
	/**
	 * Save to RDBMS after a region update
	 * @see com.gemstone.gemfire.cache.util.CacheListenerAdapter#afterUpdate(com.gemstone.gemfire.cache.EntryEvent)
	 */
	public void afterUpdate(EntryEvent<Object, Object> event)
	{		
		Debugger.println(this,"Updating entry for key:"+event.getKey());
		final Object newValue = event.getNewValue();
		final Serializable key = (Serializable)event.getKey();
		
		if(this.inflightMap != null && this.inflightMap.keySet().contains(key))
			return;
		
					
		if(this.inflightMap != null)
			this.inflightMap.put(key, Boolean.TRUE);
					
		final Object  oldValue = crudRepository.findOne(key);
					
		if(!newValue.equals(oldValue))
				crudRepository.save(newValue);
					
		if(this.inflightMap != null)
			this.inflightMap.remove(key);
	
	}// --------------------------------------------------------	

	public Map<Object, Object> getInflightMap()
	{
		return inflightMap;
	}// --------------------------------------------------------
	public void setInflightMap(Map<Object, Object> inflightMap)
	{
		this.inflightMap = inflightMap;
	}

	public CrudRepository<Object, Serializable> getCrudRepository()
	{
		return crudRepository;
	}

	public void setCrudRepository(
			CrudRepository<Object, Serializable> crudRepository)
	{
		this.crudRepository = crudRepository;
	}

	private Map<Object,Object> inflightMap;
	private CrudRepository<Object, Serializable> crudRepository = null; 


}
