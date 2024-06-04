package com.jonquass.budgetnetworth.data.jdbi.account;

import com.jonquass.budgetnetworth.core.account.Account;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper implements RowMapper<Account> {

    @Override
    public Account map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Account.builder()
                .setId(rs.getLong("id"))
                .setAccountName(rs.getString("account_name"))
                .build();
    }

}
