package com.jonquass.budgetnetworth.core.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface TransactionFields {

    LocalDate getDate();

    String getMemo();

    BigDecimal getAmount();

    long getAccountId();

    Optional<Long> getUploadId();

    Optional<Long> getUploadRowId();

    Optional<Long> getBudgetId();
}
