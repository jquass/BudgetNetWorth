package com.jonquass.budgetnetworth.core.upload.header;

import java.util.Optional;

public interface UploadHeaderFields {
    long getUploadId();

    String getValue();

    Optional<UploadHeaderType> getType();
}
