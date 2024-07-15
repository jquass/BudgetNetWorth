package com.jonquass.budgetnetworth.data.networth;

import com.jonquass.budgetnetworth.core.budget.BudgetUsageDetails;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import com.jonquass.budgetnetworth.data.jdbi.budget.month.BudgetMonthDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class BudgetUsageDetailsManager {

    private final BudgetMonthDbManager budgetMonthDbManager;

    @Inject
    BudgetUsageDetailsManager(BudgetMonthDbManager budgetMonthDbManager) {
        this.budgetMonthDbManager = budgetMonthDbManager;
    }

    public BudgetUsageDetails getBudgetUsageDetails(int date) {
        List<BudgetMonth> budgetMonths = budgetMonthDbManager.listForDate(date);
        

        return BudgetUsageDetails.builder()
                .setDate(date)
                .setBudgetMonths(budgetMonths)
                .setNetWorth(BigDecimal.TEN)
                .build();
    }

}
