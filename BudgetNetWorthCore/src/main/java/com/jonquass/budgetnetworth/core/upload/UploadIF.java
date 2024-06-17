package com.jonquass.budgetnetworth.core.upload;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface UploadIF extends UploadFields {

    long getId();

}
