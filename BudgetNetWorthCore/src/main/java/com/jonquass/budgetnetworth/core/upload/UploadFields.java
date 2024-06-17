package com.jonquass.budgetnetworth.core.upload;

public interface UploadFields {

    String getFilename();

    UploadStatus getUploadStatus();

    long getAccountId();

}
