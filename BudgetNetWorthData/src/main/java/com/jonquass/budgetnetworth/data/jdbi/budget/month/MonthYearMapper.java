package com.jonquass.budgetnetworth.data.jdbi.budget.month;

import com.jonquass.budgetnetworth.core.budget.month.MonthYear;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;

public class MonthYearMapper implements RowMapper<MonthYear> {

    @Override
    public MonthYear map(ResultSet rs, StatementContext ctx) throws SQLException {
        return MonthYear.builder()
                .setMonth(Month.valueOf(rs.getString("month")))
                .setYear(rs.getInt("year"))
                .setDate(rs.getInt("date"))
                .build();
    }

}
