package solutions.gedi.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


public class SimpleObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2702741187335693984L;

	enum SimpleEnum {
		ENUM1,
		ENUM2
	}
	
	
	
	/**
	 * @return the simpleEnum
	 */
	public SimpleEnum getSimpleEnum()
	{
		return simpleEnum;
	}
	/**
	 * @param simpleEnum the simpleEnum to set
	 */
	public void setSimpleEnum(SimpleEnum simpleEnum)
	{
		this.simpleEnum = simpleEnum;
	}
	/**
	 * @return the fieldSqlDate
	 */
	public java.sql.Date getFieldSqlDate()
	{
		return fieldSqlDate;
	}
	/**
	 * @param fieldSqlDate the fieldSqlDate to set
	 */
	public void setFieldSqlDate(java.sql.Date fieldSqlDate)
	{
		this.fieldSqlDate = fieldSqlDate;
	}
	/**
	 * @return the fieldDate
	 */
	public Date getFieldDate()
	{
		return fieldDate;
	}
	/**
	 * @param fieldDate the fieldDate to set
	 */
	public void setFieldDate(Date fieldDate)
	{
		this.fieldDate = fieldDate;
	}
	/**
	 * @return the fieldTime
	 */
	public Time getFieldTime()
	{
		return fieldTime;
	}
	/**
	 * @param fieldTime the fieldTime to set
	 */
	public void setFieldTime(Time fieldTime)
	{
		this.fieldTime = fieldTime;
	}
	/**
	 * @return the fieldTimestamp
	 */
	public Timestamp getFieldTimestamp()
	{
		return fieldTimestamp;
	}
	/**
	 * @param fieldTimestamp the fieldTimestamp to set
	 */
	public void setFieldTimestamp(Timestamp fieldTimestamp)
	{
		this.fieldTimestamp = fieldTimestamp;
	}
	/**
	 * @return the fieldCalendar
	 */
	public Calendar getFieldCalendar()
	{
		return fieldCalendar;
	}
	/**
	 * @param fieldCalendar the fieldCalendar to set
	 */
	public void setFieldCalendar(Calendar fieldCalendar)
	{
		this.fieldCalendar = fieldCalendar;
	}
	/**
	 * @return the fieldLongObject
	 */
	public Long getFieldLongObject()
	{
		return fieldLongObject;
	}
	/**
	 * @param fieldLongObject the fieldLongObject to set
	 */
	public void setFieldLongObject(Long fieldLongObject)
	{
		this.fieldLongObject = fieldLongObject;
	}
	/**
	 * @return the fieldLong
	 */
	public long getFieldLong()
	{
		return fieldLong;
	}
	/**
	 * @param fieldLong the fieldLong to set
	 */
	public void setFieldLong(long fieldLong)
	{
		this.fieldLong = fieldLong;
	}
	/**
	 * @return the fieldDouble
	 */
	public double getFieldDouble()
	{
		return fieldDouble;
	}
	/**
	 * @param fieldDouble the fieldDouble to set
	 */
	public void setFieldDouble(double fieldDouble)
	{
		this.fieldDouble = fieldDouble;
	}
	/**
	 * @return the fieldDoubleObject
	 */
	public Double getFieldDoubleObject()
	{
		return fieldDoubleObject;
	}
	/**
	 * @param fieldDoubleObject the fieldDoubleObject to set
	 */
	public void setFieldDoubleObject(Double fieldDoubleObject)
	{
		this.fieldDoubleObject = fieldDoubleObject;
	}
	/**
	 * @return the fieldFloatObject
	 */
	public Float getFieldFloatObject()
	{
		return fieldFloatObject;
	}
	/**
	 * @param fieldFloatObject the fieldFloatObject to set
	 */
	public void setFieldFloatObject(Float fieldFloatObject)
	{
		this.fieldFloatObject = fieldFloatObject;
	}
	/**
	 * @return the fieldFloat
	 */
	public float getFieldFloat()
	{
		return fieldFloat;
	}
	/**
	 * @param fieldFloat the fieldFloat to set
	 */
	public void setFieldFloat(float fieldFloat)
	{
		this.fieldFloat = fieldFloat;
	}
	/**
	 * @return the fieldInteger
	 */
	public Integer getFieldInteger()
	{
		return fieldInteger;
	}
	/**
	 * @param fieldInteger the fieldInteger to set
	 */
	public void setFieldInteger(Integer fieldInteger)
	{
		this.fieldInteger = fieldInteger;
	}
	/**
	 * @return the fieldString
	 */
	public String getFieldString()
	{
		return fieldString;
	}
	/**
	 * @param fieldString the fieldString to set
	 */
	public void setFieldString(String fieldString)
	{
		this.fieldString = fieldString;
	}
	/**
	 * @return the fieldInt
	 */
	public int getFieldInt()
	{
		return fieldInt;
	}
	/**
	 * @param fieldInt the fieldInt to set
	 */
	public void setFieldInt(int fieldInt)
	{
		this.fieldInt = fieldInt;
	}
	
	
	/**
	 * @return the bigDecimal
	 */
	public BigDecimal getBigDecimal()
	{
		return bigDecimal;
	}
	/**
	 * @param bigDecimal the bigDecimal to set
	 */
	public void setBigDecimal(BigDecimal bigDecimal)
	{
		this.bigDecimal = bigDecimal;
	}

	

	/**
	 * @return the fieldBooleanObject
	 */
	public Boolean getFieldBooleanObject()
	{
		return fieldBooleanObject;
	}
	/**
	 * @param fieldBooleanObject the fieldBooleanObject to set
	 */
	public void setFieldBooleanObject(Boolean fieldBooleanObject)
	{
		this.fieldBooleanObject = fieldBooleanObject;
	}
	/**
	 * @return the fieldBoolean
	 */
	public boolean isFieldBoolean()
	{
		return fieldBoolean;
	}
	/**
	 * @param fieldBoolean the fieldBoolean to set
	 */
	public void setFieldBoolean(boolean fieldBoolean)
	{
		this.fieldBoolean = fieldBoolean;
	}
	/**
	 * @return the fieldByteObject
	 */
	public Byte getFieldByteObject()
	{
		return fieldByteObject;
	}
	/**
	 * @param fieldByteObject the fieldByteObject to set
	 */
	public void setFieldByteObject(Byte fieldByteObject)
	{
		this.fieldByteObject = fieldByteObject;
	}
	/**
	 * @return the fiedByte
	 */
	public byte getFiedByte()
	{
		return fiedByte;
	}
	/**
	 * @param fiedByte the fiedByte to set
	 */
	public void setFiedByte(byte fiedByte)
	{
		this.fiedByte = fiedByte;
	}
	/**
	 * @return the fieldCharObject
	 */
	public Character getFieldCharObject()
	{
		return fieldCharObject;
	}
	/**
	 * @param fieldCharObject the fieldCharObject to set
	 */
	public void setFieldCharObject(Character fieldCharObject)
	{
		this.fieldCharObject = fieldCharObject;
	}
	/**
	 * @return the fieldChar
	 */
	public char getFieldChar()
	{
		return fieldChar;
	}
	/**
	 * @param fieldChar the fieldChar to set
	 */
	public void setFieldChar(char fieldChar)
	{
		this.fieldChar = fieldChar;
	}
	/**
	 * @return the fieldClass
	 */
	public java.lang.Class<?> getFieldClass()
	{
		return fieldClass;
	}
	/**
	 * @param fieldClass the fieldClass to set
	 */
	public void setFieldClass(java.lang.Class<?> fieldClass)
	{
		this.fieldClass = fieldClass;
	}
	/**
	 * @return the error
	 */
	public Error getError()
	{
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(Error error)
	{
		this.error = error;
	}
	/**
	 * @return the exception
	 */
	public Exception getException()
	{
		return exception;
	}
	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	/**
	 * @return the fieldShortObject
	 */
	public Short getFieldShortObject()
	{
		return fieldShortObject;
	}
	/**
	 * @param fieldShortObject the fieldShortObject to set
	 */
	public void setFieldShortObject(Short fieldShortObject)
	{
		this.fieldShortObject = fieldShortObject;
	}
	/**
	 * @return the fieldShort
	 */
	public short getFieldShort()
	{
		return fieldShort;
	}
	/**
	 * @param fieldShort the fieldShort to set
	 */
	public void setFieldShort(short fieldShort)
	{
		this.fieldShort = fieldShort;
	}



	/**
	 * @return the getWithNoSet
	 */
	public byte getGetWithNoSet()
	{
		return getWithNoSet;
	}
	/**
	 * @param setWithNoGet the setWithNoGet to set
	 */
	public void setSetWithNoGet(boolean setWithNoGet)
	{
		this.setWithNoGet = setWithNoGet;
	}



	/**
	 * @return the overloadedRestriction
	 */
	public String getOverloadedRestriction()
	{
		return overloadedRestriction;
	}
	/**
	 * @param overloadedRestriction the overloadedRestriction to set
	 */
	public void setOverloadedRestriction(String overloadedRestriction)
	{
		this.overloadedRestriction = overloadedRestriction;
	}
	

	/*
	 * Overloaded properties are not supported
	 * 
	 * Ex: Exception 
	 * 
	 * [error 2015/01/14 10:08:35.979 EST server1 <Function Execution Processor1> tid=0x3d] org.codehaus.jackson.map.JsonMappingException: Conflicting setter definitions for property "overloadedRestriction": solutions.gedi.demo.data.SimpleObject#setOverloadedRestriction(1 params) vs solutions.gedi.demo.data.SimpleObject#setOverloadedRestriction(1 params)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createAndCache2(StdDeserializerProvider.java:315)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createAndCacheValueDeserializer(StdDeserializerProvider.java:290)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider.findValueDeserializer(StdDeserializerProvider.java:159)
  	at org.codehaus.jackson.map.deser.std.StdDeserializer.findDeserializer(StdDeserializer.java:620)
  	at org.codehaus.jackson.map.deser.BeanDeserializer.resolve(BeanDeserializer.java:379)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._resolveDeserializer(StdDeserializerProvider.java:407)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createAndCache2(StdDeserializerProvider.java:352)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createAndCacheValueDeserializer(StdDeserializerProvider.java:290)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider.findValueDeserializer(StdDeserializerProvider.java:159)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider.findTypedValueDeserializer(StdDeserializerProvider.java:180)
  	at org.codehaus.jackson.map.ObjectMapper._findRootDeserializer(ObjectMapper.java:2829)
  	at org.codehaus.jackson.map.ObjectMapper._readValue(ObjectMapper.java:2699)
  	at org.codehaus.jackson.map.ObjectMapper.readValue(ObjectMapper.java:1286)
  	at solutions.gedi.gemfire.operations.functions.ImportJsonFunction.importRegion(ImportJsonFunction.java:201)
  	at solutions.gedi.gemfire.operations.functions.ImportJsonFunction.importOnRegion(ImportJsonFunction.java:125)
  	at solutions.gedi.gemfire.operations.functions.ImportJsonFunction.execute(ImportJsonFunction.java:76)
  	at com.gemstone.gemfire.internal.cache.execute.AbstractExecution.executeFunctionLocally(AbstractExecution.java:356)
  	at com.gemstone.gemfire.internal.cache.execute.AbstractExecution$2.run(AbstractExecution.java:320)
  	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
  	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
  	at com.gemstone.gemfire.distributed.internal.DistributionManager.runUntilShutdown(DistributionManager.java:726)
  	at com.gemstone.gemfire.distributed.internal.DistributionManager$9$1.run(DistributionManager.java:1198)
  	at java.lang.Thread.run(Thread.java:745)
  Caused by: java.lang.IllegalArgumentException: Conflicting setter definitions for property "overloadedRestriction": solutions.gedi.demo.data.SimpleObject#setOverloadedRestriction(1 params) vs solutions.gedi.demo.data.SimpleObject#setOverloadedRestriction(1 params)
  	at org.codehaus.jackson.map.introspect.POJOPropertyBuilder.getSetter(POJOPropertyBuilder.java:199)
  	at org.codehaus.jackson.map.deser.BeanDeserializerFactory.addBeanProps(BeanDeserializerFactory.java:1161)
  	at org.codehaus.jackson.map.deser.BeanDeserializerFactory.buildBeanDeserializer(BeanDeserializerFactory.java:707)
  	at org.codehaus.jackson.map.deser.BeanDeserializerFactory.createBeanDeserializer(BeanDeserializerFactory.java:636)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createDeserializer(StdDeserializerProvider.java:401)
  	at org.codehaus.jackson.map.deser.StdDeserializerProvider._createAndCache2(StdDeserializerProvider.java:310)
  	... 22 more
	 * public void setOverloadedRestriction(Integer overloadedRestriction)
	{
		this.overloadedRestriction = String.valueOf(overloadedRestriction);
	}*/


	/**
	 * @return the setWithNoGet
	 */
	public boolean isSetWithNoGet()
	{
		return setWithNoGet;
	}
	/**
	 * @param getWithNoSet the getWithNoSet to set
	 */
	public void setGetWithNoSet(byte getWithNoSet)
	{
		this.getWithNoSet = getWithNoSet;
	}


	private SimpleEnum simpleEnum; 
	
	private java.sql.Date fieldSqlDate;
	private Date fieldDate;
	private Time fieldTime;
	private Timestamp fieldTimestamp;
	private Calendar fieldCalendar;
	private Long fieldLongObject;
	private long fieldLong;
	private double fieldDouble;
	private Double fieldDoubleObject;
	private Float fieldFloatObject;
	private float fieldFloat;
	private Integer fieldInteger;
	private String fieldString;
	private int fieldInt;
	private BigDecimal bigDecimal;
	
	private Boolean fieldBooleanObject;
	private boolean fieldBoolean;
	private Byte fieldByteObject;
	private byte fiedByte;
	private Character fieldCharObject;
	private char fieldChar;
	private java.lang.Class<?> fieldClass;
	private Error error;
	private Exception exception;
	private Short fieldShortObject;
	private short fieldShort;
	
	private boolean setWithNoGet;
	
	private byte getWithNoSet = 23;
	
	private String overloadedRestriction;
	
}
