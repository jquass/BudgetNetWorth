package com.jonquass.budgetnetworth.data.jdbi.budget;

import com.jonquass.budgetnetworth.core.budget.Budget;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BudgetMapper implements RowMapper<Budget> {

    @Override
    public Budget map(ResultSet rs, StatementContext ctx) throws SQLException {
        Budget.Builder builder = Budget.builder()
                .setId(rs.getLong("id"))
                .setBudgetName(rs.getString("budget_name"));
        Optional.ofNullable(rs.getBigDecimal("monthly_amount")).ifPresent(builder::setMonthlyAmount);
        return builder.build();

    }
}
