package com.jonquass.budgetnetworth.core.budget.month;

import java.math.BigDecimal;
import java.time.Month;

public interface BudgetMonthFields {
    Month getMonth();

    int getYear();

    BigDecimal getAmount();

    long getBudgetId();
}
