package com.jonquass.budgetnetworth.data.jdbi.budget.month;

import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonthEgg;
import com.jonquass.budgetnetworth.core.budget.month.MonthYear;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface BudgetMonthDao {

    @GetGeneratedKeys
    @SqlUpdate(
            """
                    INSERT INTO budget_months (month, year, amount, budget_id, date)
                    VALUES (:month, :year, :amount, :budgetId, :date);
                    """
    )
    long insert(@BindBean BudgetMonthEgg budgetMonth);

    @SqlQuery("SELECT * FROM budget_months WHERE id = :id")
    Optional<BudgetMonth> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM budget_months ORDER BY date, budget_id DESC")
    List<BudgetMonth> list();
    
    @SqlQuery("SELECT DISTINCT(date), month, year FROM budget_months order by date desc")
    List<MonthYear> listMonthYears();

    @SqlQuery("SELECT * FROM budget_months WHERE date = :date ORDER BY budget_id DESC")
    List<BudgetMonth> listForDate(@Bind("date") int date);

    @SqlQuery("SELECT * FROM budget_months WHERE budget_id = :budget_id")
    List<BudgetMonth> listForBudget(@Bind("budget_id") long budgetId);

    @SqlQuery("SELECT * FROM budget_months WHERE month = :month AND year = :year AND budget_id = :budget_id")
    Optional<BudgetMonth> getForBudgetAndMonthYear(
            @Bind("month") Month month,
            @Bind("year") int year,
            @Bind("budget_id") long budgetId);

    @SqlUpdate("UPDATE budget_months SET amount = :amount WHERE id = :id")
    int updateAmount(@Bind("id") long id, @Bind("amount") BigDecimal amount);
}
