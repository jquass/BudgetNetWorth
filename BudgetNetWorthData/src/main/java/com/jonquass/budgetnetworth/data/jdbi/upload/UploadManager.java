package com.jonquass.budgetnetworth.data.jdbi.upload;

import com.jonquass.budgetnetworth.core.transaction.TransactionEgg;
import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.row.MappedUploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadRowDbManager;
import com.jonquass.budgetnetworth.data.upload.MappedUploadRowCreator;
import com.jonquass.budgetnetworth.data.upload.UploadHeaderCreator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
public class UploadManager {


    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;
    private final UploadRowDbManager uploadRowDbManager;
    private final TransactionDbManager transactionDbManager;
    private final MappedUploadRowCreator mappedUploadRowCreator;
    private final UploadHeaderCreator uploadHeaderCreator;

    @Inject
    UploadManager(UploadDbManager uploadDbManager,
                  UploadHeaderDbManager uploadHeaderDbManager,
                  UploadRowDbManager uploadRowDbManager,
                  TransactionDbManager transactionDbManager,
                  MappedUploadRowCreator mappedUploadRowCreator,
                  UploadHeaderCreator uploadHeaderCreator) {
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
        this.uploadRowDbManager = uploadRowDbManager;
        this.transactionDbManager = transactionDbManager;
        this.mappedUploadRowCreator = mappedUploadRowCreator;
        this.uploadHeaderCreator = uploadHeaderCreator;
    }

    public Optional<UploadContext> getUploadContextInProgress() {
        Optional<Upload> upload = uploadDbManager.getInProgress();
        return upload.flatMap(this::buildUploadContext);
    }

    public Optional<UploadContext> getUploadContext(long uploadId) {
        Optional<Upload> uploadOptional = uploadDbManager.get(uploadId);
        return uploadOptional.flatMap(this::buildUploadContext);
    }

    private Optional<UploadContext> buildUploadContext(Upload upload) {
        List<UploadHeader> uploadHeaders = uploadHeaderDbManager.getAllForUploadId(upload.getId());
        List<UploadRow> uploadRows = uploadRowDbManager.getAllForUploadId(upload.getId());
        List<MappedUploadRow> mappedUploadRows;
        if (upload.getUploadStatus() != UploadStatus.STAGING) {
            mappedUploadRows = mappedUploadRowCreator.mapUploadRows(uploadHeaders, uploadRows);
        } else {
            mappedUploadRows = List.of();
        }
        UploadContext uploadContext = UploadContext.builder()
                .setUpload(upload)
                .setUploadHeaders(uploadHeaders)
                .setUploadRows(uploadRows)
                .setMappedUploadRows(mappedUploadRows)
                .build();
        return Optional.of(uploadContext);
    }

    public Optional<UploadContext> mapHeaders(long uploadId,
                                              long transactionDateHeaderId,
                                              long memoHeaderId,
                                              long debitHeaderId,
                                              Optional<Long> creditHeaderId) {
        uploadHeaderCreator.mapHeaders(uploadId, transactionDateHeaderId, memoHeaderId, debitHeaderId, creditHeaderId);
        return getUploadContext(uploadId);
    }

    public Optional<UploadContext> finalizeUpload(long uploadId) {
        Optional<UploadContext> uploadContextOptional = getUploadContext(uploadId);
        if (uploadContextOptional.isEmpty()) {
            return Optional.empty();
        }

        UploadContext uploadContext = uploadContextOptional.get();
        if (uploadContext.getUpload().getUploadStatus() != UploadStatus.STAGED) {
            return Optional.empty();
        }

        for (MappedUploadRow mappedUploadRow : uploadContext.getMappedUploadRows()) {
            TransactionEgg transactionEgg = TransactionEgg.builder()
                    .setUploadId(uploadId)
                    .setAccountId(uploadContext.getUpload().getAccountId())
                    .setDate(mappedUploadRow.getDate())
                    .setMemo(mappedUploadRow.getMemo())
                    .setAmount(mappedUploadRow.getDebit())
                    .build();
            transactionDbManager.insert(transactionEgg);
        }

        uploadDbManager.updateStatus(uploadId, UploadStatus.COMPLETE);

        return getUploadContext(uploadId);
    }

}
