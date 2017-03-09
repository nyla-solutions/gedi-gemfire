package gedi.solutions.gemfire.integration.spring.batch;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class KeyRowMapper implements RowMapper<Serializable>
{

	public Serializable mapRow(ResultSet rs, int arg1) throws SQLException
	{
		return rs.getString(1);
	}
}
