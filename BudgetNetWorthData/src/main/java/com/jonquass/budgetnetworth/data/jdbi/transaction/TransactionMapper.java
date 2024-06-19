package com.jonquass.budgetnetworth.data.jdbi.transaction;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionMapper implements RowMapper<Transaction> {

    @Override
    public Transaction map(final ResultSet rs, final StatementContext ctx) throws SQLException {
        Transaction.Builder transactionBuilder = Transaction.builder()
                .setId(rs.getLong("id"))
                .setDate(LocalDate.parse(rs.getString("date")))
                .setMemo(rs.getString("memo"))
                .setAmount(BigDecimal.valueOf(rs.getFloat("amount")))
                .setAccountId(rs.getLong("account_id"));
        if (rs.getString("upload_id") != null) {
            transactionBuilder.setUploadId(rs.getLong("upload_id"));
        }
        if (rs.getString("upload_row_id") != null) {
            transactionBuilder.setUploadRowId(rs.getLong("upload_row_id"));
        }
        return transactionBuilder.build();
    }

}
