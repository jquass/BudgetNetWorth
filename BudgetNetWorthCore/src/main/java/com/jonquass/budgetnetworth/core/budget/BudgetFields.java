package com.jonquass.budgetnetworth.core.budget;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Optional;

public interface BudgetFields {

    String getBudgetName();

    BigDecimal getAmount();

    int getMonthPeriod();

    Month getStartMonth();

    int getStartYear();

    Optional<Month> getStopMonth();

    Optional<Integer> getStopYear();

    Optional<BigDecimal> getStopAmount();
}
