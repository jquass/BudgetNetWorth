package com.jonquass.budgetnetworth.data.jdbi.budget;

import com.jonquass.budgetnetworth.core.budget.Budget;
import com.jonquass.budgetnetworth.core.budget.BudgetEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

@Singleton
public class BudgetDbManager {

    private final Jdbi jdbi;

    @Inject
    public BudgetDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<Budget> insert(BudgetEgg budgetEgg) {
        return jdbi.withExtension(BudgetDao.class, dao -> {
            long id = dao.insert(budgetEgg);
            return dao.get(id);
        });
    }

    public Optional<Budget> get(long id) {
        return jdbi.withExtension(BudgetDao.class, dao -> dao.get(id));
    }

    public List<Budget> list() {
        return jdbi.withExtension(BudgetDao.class, BudgetDao::list);
    }
}
