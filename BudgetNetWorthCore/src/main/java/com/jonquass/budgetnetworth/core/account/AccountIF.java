package com.jonquass.budgetnetworth.core.account;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface AccountIF extends AccountFields {

    long getId();

}
