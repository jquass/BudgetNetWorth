package com.jonquass.budgetnetworth.core.upload.row;

import com.jonquass.budgetnetworth.core.BNWStyle;
import org.immutables.value.Value;

@BNWStyle
@Value.Immutable
public interface UploadRowIF extends UploadRowFields {
    long getId();
}
