package com.jonquass.budgetnetworth.data.upload;

import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import com.jonquass.budgetnetworth.core.upload.row.MappedUploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MappedUploadRowCreator {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Inject
    MappedUploadRowCreator() {
    }

    public List<MappedUploadRow> mapUploadRows(List<UploadHeader> uploadHeaders, List<UploadRow> uploadRows) {
        List<MappedUploadRow> mappedUploadRows = new ArrayList<>();
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
            MappedUploadRow.Builder builder = MappedUploadRow.builder();
            String fullRow = uploadRow.getFullRow();

            int commaIdx = fullRow.indexOf(',');
            int columnIdx = 0;
            int column = 0;
            while (commaIdx != -1) {
                String columnString = fullRow.substring(columnIdx, commaIdx);
                if (headerMap.get(UploadHeaderType.MEMO) == column) {
                    builder.setMemo(columnString);
                } else if (headerMap.get(UploadHeaderType.TRANSACTION_DATE) == column) {
                    builder.setDate(LocalDate.parse(columnString, DTF));
                } else if (headerMap.get(UploadHeaderType.DEBIT_HEADER) == column && !columnString.isEmpty()) {
                    while (!columnString.contains(".")) {
                        commaIdx = fullRow.indexOf(',', commaIdx + 1);
                        columnString = fullRow.substring(columnIdx, commaIdx);
                    }
                    String finalString = columnString.replace("$", "").replace(",", "");
                    BigDecimal debitValue = new BigDecimal(finalString);
                    builder.setDebit(debitValue);
                } else if (headerMap.containsKey(UploadHeaderType.CREDIT_HEADER) && headerMap.get(UploadHeaderType.CREDIT_HEADER) == column && !columnString.isEmpty()) {
                    while (!columnString.contains(".")) {
                        commaIdx = fullRow.indexOf(',', commaIdx + 1);
                        columnString = fullRow.substring(columnIdx, commaIdx);
                    }
                    String finalString = columnString.replace("$", "").replace(",", "");
                    BigDecimal creditValue = new BigDecimal(finalString);
                    builder.setDebit(creditValue.negate());
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
