package com.jonquass.budgetnetworth.core.upload.header;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface UploadHeaderIF extends UploadHeaderFields {

    long getId();

}
