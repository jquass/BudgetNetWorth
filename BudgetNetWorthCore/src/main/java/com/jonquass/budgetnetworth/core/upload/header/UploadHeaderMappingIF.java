package com.jonquass.budgetnetworth.core.upload.header;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

import java.util.Optional;

@BNWStyle
@Value.Immutable
public interface UploadHeaderMappingIF {

    long getTransactionDateHeaderId();

    Optional<Long> getPayeeHeaderId();

    long getMemoHeaderId();

    long getDebitHeaderId();

    Optional<Long> getCreditHeaderId();

    Optional<Long> getCategoryHeaderId();

    Optional<Long> getPostDateHeaderId();

}
