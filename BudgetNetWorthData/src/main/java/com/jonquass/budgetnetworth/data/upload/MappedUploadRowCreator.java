package com.jonquass.budgetnetworth.data.upload;

import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import com.jonquass.budgetnetworth.core.upload.row.MappedUploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MappedUploadRowCreator {

    private static final Logger LOG = LoggerFactory.getLogger(MappedUploadRowCreator.class);

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Inject
    MappedUploadRowCreator() {
    }

    public List<MappedUploadRow> mapUploadRows(List<UploadHeader> uploadHeaders, List<UploadRow> uploadRows) {
        List<MappedUploadRow> mappedUploadRows = new ArrayList<>();
        Map<UploadHeaderType, Integer> headerMap = new HashMap<>(Map.of(
                UploadHeaderType.MEMO, -1,
                UploadHeaderType.TRANSACTION_DATE, -1,
                UploadHeaderType.DEBIT_HEADER, -1,
                UploadHeaderType.CREDIT_HEADER, -1)
        );
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
            MappedUploadRow.Builder builder = MappedUploadRow.builder();
            String fullRow = uploadRow.getFullRow();

            int commaIdx = fullRow.indexOf(',');
            int columnIdx = 0;
            int column = 0;
            while (commaIdx != -1) {
                LOG.info("Command IDX: {}, ColumnIdx: {}, Column: {}", commaIdx, columnIdx, column);
                String columnString = fullRow.substring(columnIdx, commaIdx);
                if (headerMap.get(UploadHeaderType.MEMO) == column) {
                    LOG.info("Memo Header: {}", columnString);
                    builder.setMemo(columnString);
                } else if (headerMap.get(UploadHeaderType.TRANSACTION_DATE) == column) {
                    LOG.info("Transaction Date Header: {}", columnString);
                    builder.setDate(LocalDate.parse(columnString, DTF));
                } else if (headerMap.get(UploadHeaderType.DEBIT_HEADER) == column && !columnString.isEmpty()) {
                    LOG.info("Debit Header: {}", columnString);
                    if (!columnString.contains("$")) {
                        builder.setDebit(BigDecimal.ZERO);
                        columnIdx++;
                        commaIdx++;
                        continue;
                    }
                    while (!columnString.contains(".")) {
                        commaIdx = fullRow.indexOf(',', commaIdx + 1);
                        if (commaIdx == -1) {
                            break;
                        }
                        columnString = fullRow.substring(columnIdx, commaIdx);
                        LOG.info("New Debit Header: {}", columnString);
                    }
                    String finalString = columnString.replace("$", "").replace(",", "");
                    BigDecimal debitValue;
                    try {
                        debitValue = new BigDecimal(finalString);
                    } catch (NumberFormatException e) {
                        debitValue = BigDecimal.ZERO;
                    }
                    builder.setDebit(debitValue);
                } else if (headerMap.containsKey(UploadHeaderType.CREDIT_HEADER) && headerMap.get(UploadHeaderType.CREDIT_HEADER) == column && !columnString.isEmpty()) {
                    LOG.info("Credit Header: {}", columnString);
                    if (!columnString.contains("$")) {
                        builder.setDebit(BigDecimal.ZERO);
                        columnIdx++;
                        commaIdx++;
                        continue;
                    }
                    while (!columnString.contains(".")) {
                        commaIdx = fullRow.indexOf(',', commaIdx + 1);
                        if (commaIdx == -1) {
                            break;
                        }
                        columnString = fullRow.substring(columnIdx, commaIdx);
                        LOG.info("New Credit Header: {}", columnString);
                    }
                    String finalString = columnString.replace("$", "").replace(",", "");
                    BigDecimal debitValue;
                    try {
                        debitValue = new BigDecimal(finalString);
                    } catch (NumberFormatException e) {
                        debitValue = BigDecimal.ZERO;
                    }
                    builder.setDebit(debitValue.negate());
                }
                columnIdx = commaIdx + 1;
                commaIdx = fullRow.indexOf(',', columnIdx);
                column++;
            }


            mappedUploadRows.add(builder.build());
        }
        return mappedUploadRows;
    }
}
