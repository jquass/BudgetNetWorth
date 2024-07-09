package com.jonquass.budgetnetworth.data.jdbi.budget;

import com.jonquass.budgetnetworth.core.budget.Budget;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.Optional;

public class BudgetMapper implements RowMapper<Budget> {

    @Override
    public Budget map(ResultSet rs, StatementContext ctx) throws SQLException {
        Budget.Builder builder = Budget.builder()
                .setId(rs.getLong("id"))
                .setBudgetName(rs.getString("budget_name"))
                .setAmount(BigDecimal.valueOf(rs.getFloat("amount")))
                .setMonthPeriod(rs.getInt("month_period"))
                .setStartMonth(Month.valueOf(rs.getString("start_month")))
                .setStartYear(rs.getInt("start_year"));

        Optional.ofNullable(rs.getString("stop_month"))
                .map(Month::valueOf)
                .ifPresent(builder::setStopMonth);
        if (rs.getInt("stop_year") > 0) {
            builder.setStopYear(rs.getInt("stop_year"));
        }
        Optional.ofNullable(rs.getBigDecimal("stop_amount")).ifPresent(builder::setStopAmount);
        return builder.build();

    }
}
