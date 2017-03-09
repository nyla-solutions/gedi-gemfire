package solutions.gedi.demo.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComplexObject implements Serializable
{
	
	/**
	 * @return the simpleObject
	 */
	public SimpleObject getSimpleObject()
	{
		return simpleObject;
	}
	/**
	 * @param simpleObject the simpleObject to set
	 */
	public void setSimpleObject(SimpleObject simpleObject)
	{
		this.simpleObject = simpleObject;
	}
	/**
	 * @return the complexArray
	 */
	public ComplexObject[] getComplexArray()
	{
		return complexArray;
	}
	/**
	 * @param complexArray the complexArray to set
	 */
	public void setComplexArray(ComplexObject[] complexArray)
	{
		this.complexArray = complexArray;
	}
	/**
	 * @return the complexArrayList
	 */
	public ArrayList<ComplexObject> getComplexArrayList()
	{
		return complexArrayList;
	}
	/**
	 * @param complexArrayList the complexArrayList to set
	 */
	public void setComplexArrayList(ArrayList<ComplexObject> complexArrayList)
	{
		this.complexArrayList = complexArrayList;
	}
	/**
	 * @return the complexList
	 */
	public List<ComplexObject> getComplexList()
	{
		return complexList;
	}
	/**
	 * @param complexList the complexList to set
	 */
	public void setComplexList(List<ComplexObject> complexList)
	{
		this.complexList = complexList;
	}
	/**
	 * @return the complexColleciton
	 */
	public Collection<ComplexObject> getComplexColleciton()
	{
		return complexColleciton;
	}
	/**
	 * @param complexColleciton the complexColleciton to set
	 */
	public void setComplexColleciton(Collection<ComplexObject> complexColleciton)
	{
		this.complexColleciton = complexColleciton;
	}
	/**
	 * @return the complexObject
	 */
	public ComplexObject getComplexObject()
	{
		return complexObject;
	}
	/**
	 * @param complexObject the complexObject to set
	 */
	public void setComplexObject(ComplexObject complexObject)
	{
		this.complexObject = complexObject;
	}
	private SimpleObject simpleObject;
	private ComplexObject[] complexArray;
	private ArrayList<ComplexObject> complexArrayList;
	private List<ComplexObject> complexList;
	private Collection<ComplexObject> complexColleciton;;
	private ComplexObject complexObject;

}
