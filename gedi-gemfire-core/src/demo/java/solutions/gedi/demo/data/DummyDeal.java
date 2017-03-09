package solutions.gedi.demo.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import nyla.solutions.global.security.user.data.UserProfile;


public class DummyDeal implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8291045310578730352L;
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
	 * @return the numberDouble
	 */
	public double getNumberDouble()
	{
		return numberDouble;
	}
	/**
	 * @param numberDouble the numberDouble to set
	 */
	public void setNumberDouble(double numberDouble)
	{
		this.numberDouble = numberDouble;
	}
	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}
	/**
	 * @return the calendar
	 */
	public Calendar getCalendar()
	{
		return calendar;
	}
	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar)
	{
		this.calendar = calendar;
	}
	
	/**
	 * @return the userProfile
	 */
	public UserProfile getUserProfile()
	{
		return userProfile;
	}
	/**
	 * @param userProfile the userProfile to set
	 */
	public void setUserProfile(UserProfile userProfile)
	{
		this.userProfile = userProfile;
	}

	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bigDecimal == null) ? 0 : bigDecimal.hashCode());
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(numberDouble);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result
				+ ((userProfile == null) ? 0 : userProfile.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DummyDeal other = (DummyDeal) obj;
		if (bigDecimal == null)
		{
			if (other.bigDecimal != null)
				return false;
		} else
			if (!bigDecimal.equals(other.bigDecimal))
				return false;
		if (calendar == null)
		{
			if (other.calendar != null)
				return false;
		} else
			if (!calendar.equals(other.calendar))
				return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else
			if (!id.equals(other.id))
				return false;
		if (Double.doubleToLongBits(numberDouble) != Double
				.doubleToLongBits(other.numberDouble))
			return false;
		if (text == null)
		{
			if (other.text != null)
				return false;
		} else
			if (!text.equals(other.text))
				return false;
		if (userProfile == null)
		{
			if (other.userProfile != null)
				return false;
		} else
			if (!userProfile.equals(other.userProfile))
				return false;
		return true;
	}



	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	@Override
	public String toString()
	{
		return String
				.format("DummyDeal [userProfile=%s, bigDecimal=%s, numberDouble=%s, text=%s, calendar=%s, id=%s]",
						userProfile, bigDecimal, numberDouble, text, calendar,
						id);
	}



	private UserProfile userProfile = null;
	private BigDecimal bigDecimal;
	private double numberDouble;
	private String text;
	private Calendar calendar;
	private String id;
}
