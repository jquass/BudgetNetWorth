package com.jonquass.budgetnetworth.core.budget;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface BudgetIF extends BudgetFields {

    long getId();

}
