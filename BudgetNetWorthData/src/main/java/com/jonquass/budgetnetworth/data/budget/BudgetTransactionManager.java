package com.jonquass.budgetnetworth.data.budget;

import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonthEgg;
import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.data.jdbi.budget.month.BudgetMonthDbManager;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

@Singleton
public class BudgetTransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(BudgetTransactionManager.class);

    private final BudgetMonthDbManager budgetMonthDbManager;
    private final TransactionDbManager transactionDbManager;

    @Inject
    BudgetTransactionManager(
            BudgetMonthDbManager budgetMonthDbManager,
            TransactionDbManager transactionDbManager
    ) {
        this.budgetMonthDbManager = budgetMonthDbManager;
        this.transactionDbManager = transactionDbManager;
    }

    @org.jdbi.v3.sqlobject.transaction.Transaction
    public void updateBudgetId(Transaction transaction, Optional<Long> budgetId) {
        LOG.info("Updating budget id to {} for {}", budgetId, transaction);

        if (transaction.getBudgetId().equals(budgetId)) {
            LOG.info("Skipping Update, budget ID hasn't changed");
            return;
        }

        if (budgetId.isEmpty()) {
            LOG.info("Removing budget id from transaction");
            removeBudgetId(transaction);
        } else if (transaction.getBudgetId().isEmpty()) {
            LOG.info("Setting budget ID on transaction");
            setBudgetId(transaction, budgetId.get());
        } else {
            LOG.info("Replacing budget ID on transaction");
            replaceBudgetId(transaction, transaction.getBudgetId().get(), budgetId.get());
        }
    }

    private void setBudgetId(Transaction transaction, long budgetId) {
        Optional<BudgetMonth> budgetMonth = budgetMonthDbManager.getByBudgetAndMonthYear(
                transaction.getDate().getMonth(),
                transaction.getDate().getYear(),
                budgetId
        );
        if (budgetMonth.isEmpty()) {
            budgetMonthDbManager.insert(BudgetMonthEgg.builder()
                    .setBudgetId(budgetId)
                    .setAmount(transaction.getAmount())
                    .setMonth(transaction.getDate().getMonth())
                    .setYear(transaction.getDate().getYear())
                    .build());
        } else {
            BigDecimal newAmount = budgetMonth.get().getAmount().add(transaction.getAmount());
            int updated = budgetMonthDbManager.updateAmount(budgetMonth.get().getId(), newAmount);
        }
        transactionDbManager.updateBudgetId(transaction.getId(), Optional.of(budgetId));
    }

    private void replaceBudgetId(Transaction transaction, long existingBudgetId, long newBudgetId) {
        BudgetMonth existingBudgetMonth = budgetMonthDbManager.getByBudgetAndMonthYear(
                transaction.getDate().getMonth(),
                transaction.getDate().getYear(),
                existingBudgetId
        ).orElseThrow();
        BigDecimal newAmount = existingBudgetMonth.getAmount().subtract(transaction.getAmount());
        LOG.info("Updating budget amount to {} for {}", newAmount, existingBudgetId);
        int updated = budgetMonthDbManager.updateAmount(existingBudgetMonth.getId(), newAmount);
        setBudgetId(transaction, newBudgetId);
    }

    private void removeBudgetId(Transaction transaction) {
        long existingBudgetId = transaction.getBudgetId().orElseThrow();
        BudgetMonth existingBudgetMonth = budgetMonthDbManager.getByBudgetAndMonthYear(
                transaction.getDate().getMonth(),
                transaction.getDate().getYear(),
                existingBudgetId
        ).orElseThrow();
        BigDecimal newAmount = existingBudgetMonth.getAmount().subtract(transaction.getAmount());
        LOG.info("Updating budget amount to {} for {}", newAmount, existingBudgetId);
        int updated = budgetMonthDbManager.updateAmount(existingBudgetMonth.getId(), newAmount);
        transactionDbManager.updateBudgetId(transaction.getId(), Optional.empty());
    }

}
