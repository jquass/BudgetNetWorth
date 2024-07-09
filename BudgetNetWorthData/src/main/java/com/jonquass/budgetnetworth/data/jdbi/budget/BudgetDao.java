package com.jonquass.budgetnetworth.data.jdbi.budget;

import com.jonquass.budgetnetworth.core.budget.Budget;
import com.jonquass.budgetnetworth.core.budget.BudgetEgg;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface BudgetDao {

    @GetGeneratedKeys
    @SqlUpdate(
            """
                    INSERT INTO budgets (budget_name, amount, month_period, start_month, start_year, stop_month, stop_year, stop_amount)
                    VALUES (:budgetName, :amount, :monthPeriod, :startMonth, :startYear, :stopMonth, :stopYear, :stopAmount);
                    """
    )
    long insert(@BindBean BudgetEgg budget);

    @SqlQuery("SELECT * FROM budgets WHERE id = :id")
    Optional<Budget> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM budgets")
    List<Budget> list();

}
