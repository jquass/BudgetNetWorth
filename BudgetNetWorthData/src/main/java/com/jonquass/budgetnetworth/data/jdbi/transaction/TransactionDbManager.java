package com.jonquass.budgetnetworth.data.jdbi.transaction;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.core.transaction.TransactionEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

import static com.jonquass.budgetnetworth.data.BudgetNetWorthDataModule.DELETE_BATCH_SIZE;

@Singleton
public class TransactionDbManager {

    private final Jdbi jdbi;

    @Inject
    TransactionDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<Transaction> insert(TransactionEgg transactionEgg) {
        return jdbi.withExtension(TransactionDao.class, dao -> {
            long id = dao.insert(
                    transactionEgg.getDate(),
                    transactionEgg.getMemo(),
                    transactionEgg.getAmount(),
                    transactionEgg.getAccountId(),
                    transactionEgg.getUploadId(),
                    transactionEgg.getUploadRowId(),
                    Optional.empty()
            );
            return dao.get(id);
        });
    }

    public Optional<Transaction> get(long id) {
        return jdbi.withExtension(TransactionDao.class, dao -> dao.get(id));
    }

    public List<Transaction> listForAccount(long accountId, int limit) {
        return jdbi.withExtension(TransactionDao.class, dao -> dao.list(accountId, limit));
    }

    public List<Transaction> list(int limit) {
        return jdbi.withExtension(TransactionDao.class, dao -> dao.list(limit));
    }

    public int deleteForAccount(long accountId) {
        return jdbi.withExtension(TransactionDao.class, dao -> {
            int totalDeleted = 0;
            int deleted;
            do {
                deleted = dao.delete(accountId, DELETE_BATCH_SIZE);
                totalDeleted += deleted;
            } while (deleted > 0);
            return totalDeleted;
        });
    }

    public void updateBudgetId(long transactionId, Optional<Long> budgetId) {
        jdbi.withExtension(TransactionDao.class, dao -> dao.updateBudgetId(transactionId, budgetId));
    }
}
