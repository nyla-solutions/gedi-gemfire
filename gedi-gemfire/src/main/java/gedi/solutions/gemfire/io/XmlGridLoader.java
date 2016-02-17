package gedi.solutions.gemfire.io;



import java.io.File;
import java.io.IOException;
import java.util.Map;

import nyla.solutions.global.exception.RequiredException;
import nyla.solutions.global.patterns.command.Command;
import nyla.solutions.global.util.Config;
import nyla.solutions.global.util.Debugger;
import nyla.solutions.global.xml.XML;
import nyla.solutions.global.xml.XMLInterpreter;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionEvent;

/**
 * <pre>
 * Generate object to load region grid Map data from an XML
 * file representing Java Map object entries
 * 
 * Example:
 * 
 * &lt;map&gt;
  &lt;entry&gt;
    &lt;com.company.data.eligibility.Obj__Key&gt;
      &lt;mshpPartCntlNbr&gt;31&lt;/mshpPartCntlNbr&gt;
      &lt;membershipAgnId&gt;31&lt;/membershipAgnId&gt;
      &lt;personNbr&gt;Obj30&lt;/personNbr&gt;
      &lt;insertTms&gt;Obj30&lt;/insertTms&gt;
    &lt;/com.company.data.eligibility.Obj__Key&gt;
    &lt;com.company.data.eligibility.Obj&gt;
      &lt;mshpPartCntlNbr&gt;31&lt;/mshpPartCntlNbr&gt;
      &lt;membershipAgnId&gt;31&lt;/membershipAgnId&gt;
      &lt;groupOpId&gt;Obj30&lt;/groupOpId&gt;
      &lt;membershipNbr&gt;Obj30&lt;/membershipNbr&gt;
      &lt;personNbr&gt;Obj30&lt;/personNbr&gt;
      &lt;individualAgnId&gt;31&lt;/individualAgnId&gt;
      &lt;insertTms&gt;Obj30&lt;/insertTms&gt;
      &lt;bnftGroupId&gt;Obj30&lt;/bnftGroupId&gt;
      &lt;trsfrTypeCde&gt;Obj30&lt;/trsfrTypeCde&gt;
      &lt;oldGroupOpId&gt;Obj30&lt;/oldGroupOpId&gt;
      &lt;oldMembershipNbr&gt;Obj30&lt;/oldMembershipNbr&gt;
      &lt;oldPersonNbr&gt;Obj30&lt;/oldPersonNbr&gt;
      &lt;oldMshpAgnId&gt;31&lt;/oldMshpAgnId&gt;
      &lt;insertMethodCde&gt;Obj30&lt;/insertMethodCde&gt;
      &lt;insertUserId&gt;Obj30&lt;/insertUserId&gt;
      &lt;oldBnftGroupId&gt;Obj30&lt;/oldBnftGroupId&gt;
      &lt;endEffDte&gt;2011-06-20 10:48:55.850 EDT&lt;/endEffDte&gt;
      &lt;currentRowInd&gt;Obj30&lt;/currentRowInd&gt;
      &lt;groupSourceCode&gt;Obj30&lt;/groupSourceCode&gt;
      &lt;oldIndivAgnId&gt;31&lt;/oldIndivAgnId&gt;
      &lt;oldMemberAgnId&gt;31&lt;/oldMemberAgnId&gt;
      &lt;memberAgnId&gt;31&lt;/memberAgnId&gt;
      &lt;trsfrEffDte&gt;2011-06-20 10:48:55.850 EDT&lt;/trsfrEffDte&gt;
    &lt;/com.company.data.eligibility.Obj&gt;
  &lt;/entry&gt;
  &lt;/map&gt
  
  
  Add the following in the config.properties
  #Root XML load directory
  solutions.gedi.ioXmlGridLoader.rootDirectory=/some_directory
  </pre>
 * @author Gregory Green
 * 
 */
public class XmlGridLoader implements Command<Object,Object>
{
	/**
	 * Constructor
	 */
	public XmlGridLoader()
	{
	}// ------------------------------------------------	
	/**
	 * Calls load GRID
	 * @param source the input argument (not used
	 * @return null
	 */
	@SuppressWarnings("unchecked")
	//@Override
	public Object execute(Object source)
	{
		try
		{
			if(source == null)
			{
				//TODO:loadGrid();
			}
			else if(source instanceof RegionEvent)
			{
				loadGrid((RegionEvent<Object, Object>)source);
			}
			else if(source instanceof Region)
			{
				loadGrid((Region<Object, Object>)source);
			}
			else
			{
				throw new IllegalArgumentException("Unknown type "+source.getClass().getName());
			}
			
			
		}
		catch (RuntimeException e)
		{
			Debugger.printFatal(e);
			System.exit(-1);
		}
		catch (Exception e)
		{
			Debugger.printFatal(e);
			System.exit(-1);
			
		}
	 
		return null;  // no data to return
	}// ------------------------------------------------
	/**
	 * Load the region grid with the data
	 * @param regionEvent the listener region event
	 * @throws IOException
	 */
	public void loadGrid(RegionEvent<Object, Object> regionEvent)
	throws IOException
	{
		//Get Region
		loadGrid(regionEvent.getRegion());
	}// ------------------------------------------------
	/**
	 * Load a given region grid with the data from the load
	 * @param regionEvent the listener region event
	 * @throws IOException
	 */	
	public void loadGrid(Region<Object, Object> region)
	throws IOException
	{
		Debugger.println(this, "Loading the region");
		//region.putAll(this.loadFile());
		
		Map<Object, Object> data = this.loadFile();

		//Executing on a region requires on server processing
		//GridDAOFactory<Object, Object> factory = new GridDAOFactory<Object, Object>(true);
		
		//GridDAO<Object, Object> grid = factory.connectRegion(region.getName());
		for(Object key : data.keySet())
		{
			Debugger.println("Region="+region.getName()+" putting key="+key);
			
			//region.create(key, data.get(key));
			//grid.save(key,data.get(key));
			
			region.put(key, data.get(key));
		}
		
		//grid.commit();
		
		
		Debugger.println(this,"Loaded size="+data.size());
	}// ------------------------------------------------
//	/**
//	 * Load the region grid with the Map data
//	 * @param regionEvent the listener region event
//	 * @throws IOException
//	 */
//	public void loadGrid()
//	throws IOException
//	{
//		
//		// save test data
//		GemFireDAOFactory<Object, Object> gridFactory = null;
//
//		GemFireDAO<Object, Object> dao = null;
//		
//		try
//		{
//			gridFactory= new GemFireDAOFactory<Object, Object>(
//					isServer);
//
//			dao = gridFactory.connectRegion(regionName);
//			
//			Map<Object, Object> map = loadFile();
//			
//			for(Object key : map.keySet())
//			{
//				dao.create(key, map.get(key));
//			}
//
//			dao.commit();
//		}
//		catch (Exception e)
//		{
//			if(dao != null)
//				dao.rollback();
//			
//			throw new SetupException(Debugger.stackTrace(e));
//		}
//		finally
//		{
//			/*if(dao != null)
//				try { dao.dispose(); } catch(Exception e){}
//				*/
//		}
//		
//	}// ------------------------------------------------
	/**
	 * Load Map from this.mapFilePaht
	 * @return the Map Java Objects based on the XML information 
	 * @throws IOException
	 */
	public Map<Object, Object> loadFile()
	throws IOException
	{
		if( this.mapFilePath == null || this.mapFilePath.length() == 0)
		{
			//Get by region in root directory
			if(this.regionName == null || this.regionName.length() == 0)
				    	throw new RequiredException("this.regionName");
			
			//get but default path
			this.mapFilePath = Config.getProperty(XmlGridLoader.class.getName()+".rootDirectory")+"/"+regionName+mapFileSuffix;
			
		}

		Debugger.println(this, "this.mapFilePath="+this.mapFilePath );
		
		return readFile(new File(this.mapFilePath));
	}// ------------------------------------------------
	/**
	 * Read map data for an XML file
	 * @param mapFilePath
	 * @return mapObject
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> readFile(File mapXmlFilePath) 
	throws IOException
	{
		if (mapXmlFilePath == null)
			throw new IllegalArgumentException("mapXmlFilePath is required");
		
		if (!mapXmlFilePath.exists())
			throw new IllegalArgumentException(mapXmlFilePath.getAbsolutePath()+" does not exist");
		
		//Debugger.println(XmlGridLoader.class,"getting intepreter");
		//XMLInterpreter intepreter = XML.getInterpreter();
		
		
		Debugger.println(XmlGridLoader.class,"use intepreter");
		Object obj = intepreter.toObject(mapXmlFilePath);
		
		Debugger.println(XmlGridLoader.class,"Got results");
		if (obj == null)
			throw new RequiredException("Cannot create object from file "+mapXmlFilePath.getAbsolutePath());
	     
		if(!(obj instanceof Map))
	     {
	    	 throw new IllegalArgumentException(obj.getClass().getName()+" must be an instance of Map<Object, Object>");
	     }
		return ( Map<Object, Object>)obj;
	}// ------------------------------------------------
	
	/**
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
	}
	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}
	/**
	 * @return the isServer
	 */
	public boolean isServer()
	{
		return isServer;
	}
	/**
	 * @param isServer the isServer to set
	 */
	public void setServer(boolean isServer)
	{
		this.isServer = isServer;
	}
	/**
	 * @return the mapFilePath
	 */
	public String getMapFilePath()
	{
		return mapFilePath;
	}
	/**
	 * @param mapFilePath the mapFilePath to set
	 */
	public void setMapFilePath(String mapFilePath)
	{
		this.mapFilePath = mapFilePath;
	}

	
	/**
	 * @return the mapFileSuffix
	 */
	public String getMapFileSuffix()
	{
		return mapFileSuffix;
	}
	/**
	 * @param mapFileSuffix the mapFileSuffix to set
	 */
	public void setMapFileSuffix(String mapFileSuffix)
	{
		this.mapFileSuffix = mapFileSuffix;
	}

	private static XMLInterpreter intepreter = XML.getInterpreter();
	private String mapFileSuffix = Config.getProperty(XmlGridLoader.class,"mapFileSuffix",".xml");
	private String regionName = null;
	private boolean isServer = false;
	private String mapFilePath = null;

}
