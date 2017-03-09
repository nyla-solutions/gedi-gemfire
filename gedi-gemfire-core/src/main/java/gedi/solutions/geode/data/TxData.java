package gedi.solutions.geode.data;

import java.io.Serializable;
import java.util.Properties;
import com.gemstone.gemfire.cache.Declarable;

/**
 * A data type represents a transaction data object.  Instances
 * of <code>TxData</code> are stored in a GemFire
 * <code>Region</code>.
 * 
 * @author farmer
 */
public class TxData implements Declarable, Serializable {
	private static final long serialVersionUID = 1L;

	private Object key = new Object();
	private Object value = new Object();
	
	public void init(Properties props) {
		this.key = props.getProperty("key");
	    this.value = props.getProperty("value");
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
  
}
