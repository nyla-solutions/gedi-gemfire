package gedi.solutions.gemfire.data.catalog;

import java.io.Serializable;


/**
 * 
 * 
 * 
 * <pre>
 * 
 * 
 *  FunctionAttribute is a value object representation of a entity FunctionAttribute with
 *  name and a value
 * 
 *  
 * 
 *  
 * </pre>
 * 
 * @author Gregory Green
 * 
 * @version 1.0
 *  
 */

public class FunctionAttribute 
implements Serializable
{
   public FunctionAttribute()
   {

      name = null;

      value = null;

   }//--------------------------------------------
   /**
    * 
    * Constructor for FunctionAttribute initializes internal 
    * data settings.
    * @param aName the property name
    * @param aValue the property value
    */
   public FunctionAttribute(String aName,Serializable aValue )
   {
      this.setName(aName);
      this.setValue(aValue);
      
   }//--------------------------------------------
   /**
    * 
    * @see java.lang.Object#clone()
    */
   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }//----------------------------------------
   /**
    * @param aOther the other property to compare
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    * @throws ClassCastException if the other is not a property
    */
   public int compareTo(Object aOther)
   {
      FunctionAttribute other = (FunctionAttribute)aOther;
      
      //compare names
     return other.getName().compareTo(this.getName());
   }//--------------------------------------------
   /**
    * 
    * @return the property name
    */
   public String getName()
   {
      return name;

   }//--------------------------------------------
   /**
    * Set property name
    * @param name name to set
    */
   public void setName(String name)
   {

      if (name == null)
      {
        name = "";
      }

     this.name = name.trim();

   }//--------------------------------------------
   /**
    * 
    * @return the value of the property
    * @see solutions.global.data.Mappable#getValue()
    */
   public Object getValue()
   {
      return value;
   }//--------------------------------------------
   /**
    * 
    * @param value the property value to set
    */
   public void setValue(Serializable value)
   {
      this.value = value;
   }//--------------------------------------------

   /**
    * 
    * @return name of the property
    * 
    * @see solutions.global.data.Mappable#getKey()
    *  
    */
   public Object getKey()
   {

      return name;
   }//--------------------------------------------
   /**
    * Two properties are equal if they have the same property name
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if(aOther == this)
         return true;
      
      if(aOther == null || !(aOther instanceof FunctionAttribute))
         return false;
      
      FunctionAttribute otherNP= (FunctionAttribute)aOther;
      
      return otherNP.getName().equals(this.getName());
   }//--------------------------------------------
   /**
    * 
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
     StringBuffer text = new StringBuffer("[")
        .append(getClass().getName()).append("]")
        .append(" name: ").append(name)
        .append(" value: ").append(value);
     
      return text.toString();
   }//----------------------------------------
   /**
    * 
    * @param aValue the property value
    * @return true if string version of the property value
    * equals (ignore case) aValue
    */
   public boolean equalsValueIgnoreCase(Object aValue)
   {
      return String.valueOf(value).equalsIgnoreCase(
             String.valueOf(aValue));
   }//--------------------------------------------
   /**
    * Set name to key
    * @see solutions.global.data.Key#setKey(java.lang.Object)
    */
   public void setKey(Object key)
   {
      if (key == null)
         throw new IllegalArgumentException("key required in FunctionAttribute.setKey");
      
      this.name = key.toString();
   }//--------------------------------------------
   /**
    * 
    * @param text the text value
    */
   public void setTextValue(String text)
   {
      this.setValue(text);
   }// --------------------------------------------

   /**
    * 
    * @return (String)getValue()
    */
   public String getTextValue()
   {
      return (String)getValue();
   }// --------------------------------------------

   private String name = "";
   private Serializable value = "";
   static final long serialVersionUID = FunctionAttribute.class.getName().hashCode();

}

