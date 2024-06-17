package com.jonquass.budgetnetworth.data.jdbi.upload;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadRowDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class UploadManager {

    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;
    private final UploadRowDbManager uploadRowDbManager;

    @Inject
    UploadManager(UploadDbManager uploadDbManager,
                  UploadHeaderDbManager uploadHeaderDbManager,
                  UploadRowDbManager uploadRowDbManager) {
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
        this.uploadRowDbManager = uploadRowDbManager;
    }

    public Optional<UploadContext> getUploadContext(long uploadId) {
        Optional<Upload> upload = uploadDbManager.get(uploadId);
        if (upload.isEmpty()) {
            return Optional.empty();
        }

        List<UploadHeader> uploadHeaders = uploadHeaderDbManager.getAllForUploadId(uploadId);
        List<UploadRow> uploadRows = uploadRowDbManager.getAllForUploadId(uploadId);
        UploadContext uploadContext = UploadContext.builder()
                .setUpload(upload.get())
                .setUploadHeaders(uploadHeaders)
                .setUploadRows(uploadRows)
                .build();
        return Optional.of(uploadContext);
    }

    public void mapHeaders(long uploadId,
                           long transactionDateHeaderId,
                           long memoHeaderId,
                           long debitHeaderId) {
        uploadHeaderDbManager.mapHeaders(transactionDateHeaderId, memoHeaderId, debitHeaderId);
        uploadDbManager.updateStatus(uploadId, UploadStatus.STAGED);
    }

}
