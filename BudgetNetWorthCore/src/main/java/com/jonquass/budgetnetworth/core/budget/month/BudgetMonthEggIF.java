package com.jonquass.budgetnetworth.core.budget.month;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface BudgetMonthEggIF extends BudgetMonthFields {

    @Value.Lazy
    default Integer getDate() {
        int monthNum = getMonth().ordinal() + 1;
        String month = monthNum < 10 ? "0" + monthNum : "" + monthNum;
        return Integer.valueOf(String.format("%d%s", getYear(), month));
    }

}
