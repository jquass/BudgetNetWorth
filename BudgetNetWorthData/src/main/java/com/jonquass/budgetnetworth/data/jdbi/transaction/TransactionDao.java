package com.jonquass.budgetnetworth.data.jdbi.transaction;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionDao {

    @GetGeneratedKeys
    @SqlUpdate(
            """
                    INSERT INTO transactions (date, memo, amount, account_id, upload_id, upload_row_id)
                    VALUES (:date, :memo, :amount, :account_id, :upload_id, upload_row_id)
                    """
    )
    long insert(
            @Bind("date") LocalDate date,
            @Bind("memo") String memo,
            @Bind("amount") BigDecimal amount,
            @Bind("account_id") long accountId,
            @Bind("upload_id") Optional<Long> uploadId,
            @Bind("upload_row_id") Optional<Long> uploadRowId
    );

    @SqlQuery("SELECT * FROM transactions WHERE id = :id")
    Optional<Transaction> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM transactions WHERE account_id = :account_id ORDER BY id LIMIT :limit")
    List<Transaction> list(@Bind("account_id") long accountId, @Bind("limit") int limit);

    @SqlQuery("SELECT * FROM transactions ORDER BY id LIMIT :limit")
    List<Transaction> list(@Bind("limit") int limit);
}
