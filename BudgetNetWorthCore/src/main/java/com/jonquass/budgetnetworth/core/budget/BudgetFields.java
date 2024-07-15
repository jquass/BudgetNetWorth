package com.jonquass.budgetnetworth.core.budget;

import java.math.BigDecimal;
import java.util.Optional;

public interface BudgetFields {

    String getBudgetName();

    Optional<BigDecimal> getMonthlyAmount();
}
