package com.jonquass.budgetnetworth.core.budget.month;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface BudgetMonthIF extends BudgetMonthFields {
    long getId();
}
