package gedi.solutions.geode.bridge;

import java.util.Set;

/**
 * Provide the keys to use for a onRegion call
 * @author Gregory Green
 *
 */
public interface RegionKeyInterpreter
{
	public Set<Object> toFilter(Object argument);
	
}
