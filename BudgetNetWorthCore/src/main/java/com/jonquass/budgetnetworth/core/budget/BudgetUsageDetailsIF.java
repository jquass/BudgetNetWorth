package com.jonquass.budgetnetworth.core.budget;

import com.jonquass.budgetnetworth.core.BNWStyle;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;


@BNWStyle
@Value.Immutable
public interface BudgetUsageDetailsIF {

    List<BudgetMonth> getBudgetMonths();

    int getDate();

    BigDecimal getNetWorth();

}
