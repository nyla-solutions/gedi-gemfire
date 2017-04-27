package gedi.solutions.geode.operations;


import java.util.Iterator;
import java.util.Set;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.internal.cache.CachedDeserializable;
import org.apache.geode.internal.cache.EntrySnapshot;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.RegionEntry;
import org.apache.geode.internal.size.ObjectGraphSizer;
import org.apache.geode.internal.size.ReflectionObjectSizer;
import org.apache.geode.pdx.PdxInstance;

import nyla.solutions.core.patterns.reflection.ObjectClassSizer;
public class ObjectSizing
{
	 private LogWriter logger = null;
	  
	  private int totalDeserializedRegionEntrySizeBefore;
	  private int totalDeserializedKeySize;
	  private int totalDeserializedValueSize;
	  private int totalDeserializedRegionEntrySizeAfter;
	  private int totalSerializedValueSize;


	  /**
	   * Creates an instance that logs all the output to <code>System.out</code>.
	   */
	  public ObjectSizing() {
	  }
	  
	  /**
	   * Creates an instance that logs to the provided <code>LogWriter</code>.
	   * @param logger
	   *        <code>LogWriter</code> to use for all the output. 
	   */
	  public ObjectSizing(LogWriter logger) {
	    this.logger = logger;
	  }
	  
	  /**
	   * Calculates the size of an object.
	   * 
	   * @param o The object to size
	   * @return The size of object o
	   */
	  public static long sizeObjectBytes(Object o) 
	  {
	    return new ObjectClassSizer().sizeInBytes(o.getClass());
	  }
	  
	  public static String histObject(Object o) throws IllegalArgumentException, 
	      IllegalAccessException {
	    return ObjectGraphSizer.histogram(o, false);
	  }
	  
	  /**
	   * Calculates and logs the size of all entries in the region. 
	   * 
	   * @param region
	   */
	  public void sizeRegion(Region<?,?> region) {
	    sizeRegion(region, 0);
	  }
	  
	  /**
	   * Calculates and logs the size of first numEntries in the region.
	   * 
	   * @param region the region to size
	   * @param numEntries the  number of entries
	   *          The number of entries to calculate the size for. If 0
	   * all the entries in the region are included.  
	   */
	  public void sizeRegion(Region<?,?> region, int numEntries) {
	    if (region == null) {
	      throw new IllegalArgumentException("Region is null.");
	    }

	    if (region instanceof PartitionedRegion) {
	      sizePartitionedRegion(region, numEntries);
	    } else {
	      sizeReplicatedOrLocalRegion(region, numEntries);
	    }
	  }
	  
	  /**
	   * Sizes numEntries of a partitioned region, or all the entries if 
	   * numEntries is 0.
	   * 
	   * @param numEntries 
	   *        Number of entries to size. If the value is 0, all the 
	   * entries are sized. 
	   */
	  private void sizePartitionedRegion(Region<?,?> region, int numEntries) {
	    Region<?,?> primaryDataSet = PartitionRegionHelper.getLocalData(region);
	    int regionSize = primaryDataSet.size();
	    if (numEntries == 0) {
	      numEntries = primaryDataSet.size();
	    } else if (numEntries > regionSize) {
	      numEntries = regionSize;
	    }
	    
	    int count = 0;
	    for (Iterator<?> i = primaryDataSet.entrySet().iterator(); i.hasNext();) {
	      if (count == numEntries) {
	        break;
	      }
	      EntrySnapshot entry = (EntrySnapshot) i.next();
	      RegionEntry re = entry.getRegionEntry();
	      dumpSizes(entry, re);
	    }

	    dumpTotalAndAverageSizes(numEntries);
	    clearTotals();
	  }
	  
	  /**
	   * Sizes numEntries of a replicated or local region, or all the entries if
	   * numEntries is 0.
	   * 
	   * @param numEntries Number of entries to size. If the value is 0, all the 
	   * entries are sized. 
	   */
	  private void sizeReplicatedOrLocalRegion(Region<?,?> region, int numEntries) {
	    Set<?> entries = region.entrySet();
	    int regionSize = entries.size();
	    if (numEntries == 0) {
	      numEntries = entries.size();
	    } else if (numEntries > regionSize) {
	      numEntries = regionSize;
	    }
	    
	    int count = 0;
	    for (Iterator<?> i = entries.iterator(); i.hasNext();) {
	      if (count == numEntries) {
	        break;
	      }
	      LocalRegion.NonTXEntry entry = (LocalRegion.NonTXEntry) i.next();
	      RegionEntry re = entry.getRegionEntry();
	      dumpSizes(entry, re);
	    }
	    
	    dumpTotalAndAverageSizes(numEntries);
	    clearTotals();
	  }
	    
	  private void dumpSizes(Region.Entry<?,?> entry, RegionEntry re) {
	    long deserializedRegionEntrySizeBefore = ReflectionObjectSizer.getInstance().sizeof(re);
	    long serializedValueSize = calculateSerializedValueSize(entry, re);
	    long deserializedKeySize = ReflectionObjectSizer.getInstance().sizeof(entry.getKey());
	    Object value = entry.getValue();
	    long deserializedValueSize;
	    if (value instanceof PdxInstance) {
	      Object actualObj = ((PdxInstance)value).getObject();
	      deserializedValueSize = sizeObjectBytes(actualObj);
	    } else {
	      deserializedValueSize = sizeObjectBytes(value);
	    }
	    int deserializedRegionEntrySizeAfter = ReflectionObjectSizer.getInstance().sizeof(re);
	    this.totalDeserializedRegionEntrySizeBefore += deserializedRegionEntrySizeBefore;
	    this.totalDeserializedKeySize += deserializedKeySize;
	    this.totalDeserializedValueSize += deserializedValueSize;
	    this.totalSerializedValueSize += serializedValueSize;
	    this.totalDeserializedRegionEntrySizeAfter += deserializedRegionEntrySizeAfter;
	    log("RegionEntry (key = " + re.getKey() + ") size: " + deserializedRegionEntrySizeBefore + 
	        " (serialized), " + deserializedRegionEntrySizeAfter + 
	        " (deserialized). Key size: " + deserializedKeySize + 
	        ". Value size: " + serializedValueSize + " (serialized), " + 
	        deserializedValueSize +  "(deserialized).");
	  }
	  
	  private int calculateSerializedValueSize(Region.Entry<?,?> entry, RegionEntry re) {
	    Object valueInVm = re.getValue(null);
	    int serializedValueSize = 0;
	    if (valueInVm instanceof CachedDeserializable) {
	      // Value is a wrapper
	      Object cdValue = ((CachedDeserializable) valueInVm).getValue();
	      if (cdValue instanceof byte[]) {
	        // The wrapper wraps a serialized domain object
	        serializedValueSize = ((byte[]) cdValue).length;
	      } else {
	        // The wrapper wraps a deserialized domain object
	        serializedValueSize = ReflectionObjectSizer.getInstance().sizeof(cdValue);
	      }
	    } else {
	      // Value is a domain object
	      serializedValueSize = ReflectionObjectSizer.getInstance().sizeof(valueInVm);
	    }

	    return serializedValueSize;
	  }
	  
	  private void dumpTotalAndAverageSizes(int totalEntries) {
	    log("Total RegionEntry size (serialized): " + this.totalDeserializedRegionEntrySizeBefore);
	    log("Total RegionEntry size (deserialized): " + this.totalDeserializedRegionEntrySizeAfter);
	    log("Total Key size: " + this.totalDeserializedKeySize);
	    log("Total Value size (serialized): " + this.totalSerializedValueSize);
	    log("Total Value size (deserialized): " + this.totalDeserializedValueSize);
	    log("Average RegionEntry size (serialized): " + (this.totalDeserializedRegionEntrySizeBefore/totalEntries));
	    log("Average RegionEntry size (deserialized): " + (this.totalDeserializedRegionEntrySizeAfter/totalEntries));
	    log("Average Key size: " + (this.totalDeserializedKeySize/totalEntries));
	    log("Average Value size (serialized): " + (this.totalSerializedValueSize/totalEntries));
	    log("Average Value size (deserialized): " + (this.totalDeserializedValueSize/totalEntries));
	    log("--------------");
	  }
	  
	  private void clearTotals() {
	    this.totalDeserializedRegionEntrySizeBefore = 0;
	    this.totalDeserializedKeySize = 0;
	    this.totalDeserializedValueSize = 0;
	    this.totalSerializedValueSize = 0;
	    this.totalDeserializedRegionEntrySizeAfter = 0;
	  }

	  protected void log(String message) {
	    if (logger != null) {
	      logger.info(message);
	    } else {
	      System.out.println(message);
	    }
	  }
}
