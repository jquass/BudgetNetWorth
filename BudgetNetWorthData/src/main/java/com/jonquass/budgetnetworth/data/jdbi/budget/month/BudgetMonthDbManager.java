package com.jonquass.budgetnetworth.data.jdbi.budget.month;

import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonthEgg;
import com.jonquass.budgetnetworth.core.budget.month.MonthYear;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Singleton
public class BudgetMonthDbManager {

    private final Jdbi jdbi;

    @Inject
    public BudgetMonthDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Optional<BudgetMonth> insert(BudgetMonthEgg budgetMonthEgg) {
        return jdbi.withExtension(BudgetMonthDao.class, dao -> {
            long id = dao.insert(budgetMonthEgg);
            return dao.get(id);
        });
    }

    public List<BudgetMonth> list() {
        return jdbi.withExtension(BudgetMonthDao.class, BudgetMonthDao::list);
    }

    public List<MonthYear> listMonthYears() {
        return jdbi.withExtension(BudgetMonthDao.class, BudgetMonthDao::listMonthYears);
    }

    public List<BudgetMonth> listForDate(int date) {
        return jdbi.withExtension(BudgetMonthDao.class, dao -> dao.listForDate(date));
    }

    public Optional<BudgetMonth> get(long id) {
        return jdbi.withExtension(BudgetMonthDao.class, dao -> dao.get(id));
    }

    public Optional<BudgetMonth> getByBudgetAndMonthYear(Month month, int year, long budgetId) {
        return jdbi.withExtension(BudgetMonthDao.class, dao -> dao.getForBudgetAndMonthYear(month, year, budgetId));
    }

    public int updateAmount(long id, BigDecimal amount) {
        return jdbi.withExtension(BudgetMonthDao.class, dao -> dao.updateAmount(id, amount));
    }
}
