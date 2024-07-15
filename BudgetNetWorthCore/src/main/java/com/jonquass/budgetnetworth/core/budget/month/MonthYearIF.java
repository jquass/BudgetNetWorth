package com.jonquass.budgetnetworth.core.budget.month;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

import java.time.Month;

@BNWStyle
@Value.Immutable
public interface MonthYearIF {

    Month getMonth();

    int getYear();

    // Format YYYYMM
    int getDate();

}
