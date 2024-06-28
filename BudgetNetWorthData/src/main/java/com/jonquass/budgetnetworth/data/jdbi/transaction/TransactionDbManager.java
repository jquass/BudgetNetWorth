package com.jonquass.budgetnetworth.data.jdbi.transaction;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.core.transaction.TransactionEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

@Singleton
public class TransactionDbManager {

    private final Jdbi jdbi;

    @Inject
    TransactionDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Transaction insert(TransactionEgg transactionEgg) {
        return jdbi.withExtension(TransactionDao.class, dao -> {
            long id = dao.insert(
                    transactionEgg.getDate(),
                    transactionEgg.getMemo(),
                    transactionEgg.getAmount(),
                    transactionEgg.getAccountId(),
                    transactionEgg.getUploadId(),
                    transactionEgg.getUploadRowId()
            );
            return dao.get(id).orElseThrow();
        });
    }

    public List<Transaction> listForAccount(long accountId, int limit) {
        return jdbi.withExtension(TransactionDao.class, dao -> dao.list(accountId, limit));
    }

    public List<Transaction> list(int limit) {
        return jdbi.withExtension(TransactionDao.class, dao -> dao.list(limit));
    }

}
