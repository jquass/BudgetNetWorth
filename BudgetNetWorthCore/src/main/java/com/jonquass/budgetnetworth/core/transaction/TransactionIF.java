package com.jonquass.budgetnetworth.core.transaction;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface TransactionIF extends TransactionFields {

    long getId();

}
