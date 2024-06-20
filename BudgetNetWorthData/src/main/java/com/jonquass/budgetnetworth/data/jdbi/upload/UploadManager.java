package com.jonquass.budgetnetworth.data.jdbi.upload;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.core.transaction.TransactionEgg;
import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import com.jonquass.budgetnetworth.core.upload.row.MappedUploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadRowDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Singleton
public class UploadManager {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final Logger LOG = LoggerFactory.getLogger(UploadManager.class);
    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;
    private final UploadRowDbManager uploadRowDbManager;
    private final TransactionDbManager transactionDbManager;

    @Inject
    UploadManager(UploadDbManager uploadDbManager,
                  UploadHeaderDbManager uploadHeaderDbManager,
                  UploadRowDbManager uploadRowDbManager,
                  TransactionDbManager transactionDbManager) {
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
        this.uploadRowDbManager = uploadRowDbManager;
        this.transactionDbManager = transactionDbManager;
    }

    private static BigDecimal getBigDecimalFromCol(String col) {
        return new BigDecimal(col.replaceAll("\\$", ""));
    }

    public Optional<UploadContext> getUploadContext(long uploadId) {
        Optional<Upload> uploadOptional = uploadDbManager.get(uploadId);
        if (uploadOptional.isEmpty()) {
            return Optional.empty();
        }

        Upload upload = uploadOptional.get();
        List<UploadHeader> uploadHeaders = uploadHeaderDbManager.getAllForUploadId(uploadId);
        List<UploadRow> uploadRows = uploadRowDbManager.getAllForUploadId(uploadId);

        List<MappedUploadRow> mappedUploadRows;
        if (upload.getUploadStatus() != UploadStatus.STAGED) {
            mappedUploadRows = List.of();
        } else {
            mappedUploadRows = new ArrayList<>();
            Map<UploadHeaderType, Integer> headerMap = new HashMap<>(uploadHeaders.size());
            int headerIndex = 0;
            for (UploadHeader uploadHeader : uploadHeaders) {
                if (uploadHeader.getType().isPresent()) {
                    switch (uploadHeader.getType().orElseThrow()) {
                        case MEMO -> headerMap.put(UploadHeaderType.MEMO, headerIndex);
                        case TRANSACTION_DATE -> headerMap.put(UploadHeaderType.TRANSACTION_DATE, headerIndex);
                        case DEBIT_HEADER -> headerMap.put(UploadHeaderType.DEBIT_HEADER, headerIndex);
                        case CREDIT_HEADER -> headerMap.put(UploadHeaderType.CREDIT_HEADER, headerIndex);
                    }
                }
                headerIndex++;
            }

            for (UploadRow uploadRow : uploadRows) {
                LOG.debug("UploadRow {}", uploadRow);
                List<String> columns = List.of(uploadRow.getFullRow().split(",", -1));
                LOG.debug("Columns {}", columns);

                MappedUploadRow.Builder mappedUploadRowBuilder = MappedUploadRow.builder();
                for (int i = 0; i < columns.size(); i++) {
                    if (headerMap.get(UploadHeaderType.MEMO) == i) {
                        mappedUploadRowBuilder.setMemo(columns.get(i));
                    } else if (headerMap.get(UploadHeaderType.TRANSACTION_DATE) == i) {
                        mappedUploadRowBuilder.setDate(LocalDate.parse(columns.get(i), df));
                    } else if (headerMap.get(UploadHeaderType.DEBIT_HEADER) == i && !columns.get(i).isEmpty()) {
                        String debit = buildDebit(columns, i, headerMap);
                        mappedUploadRowBuilder.setDebit(getBigDecimalFromCol(debit));
                    } else if (headerMap.containsKey(UploadHeaderType.CREDIT_HEADER) && headerMap.get(UploadHeaderType.CREDIT_HEADER) == i && !columns.get(i).isEmpty()) {
                        String debit = buildDebit(columns, i, headerMap);
                        BigDecimal creditValue = getBigDecimalFromCol(debit);
                        mappedUploadRowBuilder.setDebit(creditValue.negate());
                    }
                }
                mappedUploadRows.add(mappedUploadRowBuilder.build());
            }
        }

        UploadContext uploadContext = UploadContext.builder()
                .setUpload(upload)
                .setUploadHeaders(uploadHeaders)
                .setUploadRows(uploadRows)
                .setMappedUploadRows(mappedUploadRows)
                .build();
        return Optional.of(uploadContext);
    }

    private String buildDebit(List<String> columns, int i, Map<UploadHeaderType, Integer> headerMap) {
        StringBuilder debit = new StringBuilder(columns.get(i));
        while (!debit.toString().contains(".")) {
            i++;
            debit.append(columns.get(i));
        }
        return debit.toString();
    }

    public Optional<UploadContext> mapHeaders(long uploadId,
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
            Transaction transaction = transactionDbManager.insert(transactionEgg);
        }

        uploadDbManager.updateStatus(uploadId, UploadStatus.COMPLETE);

        return getUploadContext(uploadId);
    }

}
