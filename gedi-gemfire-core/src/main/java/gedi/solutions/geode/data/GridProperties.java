package gedi.solutions.geode.data;

import java.io.Serializable;
import java.util.Properties;
import com.gemstone.gemfire.cache.Declarable;

/**
 * A properties that consists of runtime Grid Properties.  Instances
 * of <code>GridProperties</code> are stored in a GemFire
 * <code>Region</code> and their contents can be queried using the
 * GemFire query service.
 * 
 * @author farmer
 */
public class GridProperties implements Declarable, Serializable {
  private static final long serialVersionUID = 1L;
  
  private String key;
  private Object value = new Object();

  public void init(Properties props) {
    this.key = props.getProperty("key");
    this.value = props.getProperty("value");
  }

/**
 * @return the key
 */
public String getKey() {
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
