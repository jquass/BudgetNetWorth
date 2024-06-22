package com.jonquass.budgetnetworth.data.upload;

import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class UploadHeaderCreator {

    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;

    @Inject
    UploadHeaderCreator(UploadDbManager uploadDbManager,
                        UploadHeaderDbManager uploadHeaderDbManager) {
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
    }

    public void mapHeaders(long uploadId,
                           long transactionDateHeaderId,
                           long memoHeaderId,
                           long debitHeaderId,
                           Optional<Long> creditHeaderId) {
        uploadHeaderDbManager.unmapHeadersForUpload(uploadId);
        uploadHeaderDbManager.mapHeader(transactionDateHeaderId, UploadHeaderType.TRANSACTION_DATE);
        uploadHeaderDbManager.mapHeader(memoHeaderId, UploadHeaderType.MEMO);
        uploadHeaderDbManager.mapHeader(debitHeaderId, UploadHeaderType.DEBIT_HEADER);
        creditHeaderId.ifPresent(c -> uploadHeaderDbManager.mapHeader(c, UploadHeaderType.CREDIT_HEADER));
        uploadDbManager.updateStatus(uploadId, UploadStatus.STAGED);
    }
}
