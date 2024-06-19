package com.jonquass.budgetnetworth.core.upload.row;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@BNWStyle
@Value.Immutable
public interface MappedUploadRowIF {
    LocalDate getDate();

    String getMemo();

    @Value.Default
    default BigDecimal getDebit() {
        return BigDecimal.ZERO;
    }
}
