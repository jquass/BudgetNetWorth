package com.jonquass.budgetnetworth.data.jdbi.budget.month;

import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;

public class BudgetMonthMapper implements RowMapper<BudgetMonth> {

    @Override
    public BudgetMonth map(ResultSet rs, StatementContext ctx) throws SQLException {
        return BudgetMonth.builder()
                .setId(rs.getLong("id"))
                .setAmount(BigDecimal.valueOf(rs.getDouble("amount")))
                .setMonth(Month.valueOf(rs.getString("month")))
                .setYear(rs.getInt("year"))
                .setBudgetId(rs.getLong("budget_id"))
                .build();
    }

}
