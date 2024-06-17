package com.jonquass.budgetnetworth.data.jdbi.upload.db.manager;


import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderEgg;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.upload.dao.UploadHeaderDao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

@Singleton
public class UploadHeaderDbManager {

    private final Jdbi jdbi;

    @Inject
    UploadHeaderDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public UploadHeader insert(UploadHeaderEgg uploadHeaderEgg) {
        return jdbi.withExtension(UploadHeaderDao.class, dao -> {
            long id = dao.insert(uploadHeaderEgg.getUploadId(), uploadHeaderEgg.getValue());
            return dao.get(id).orElseThrow();
        });
    }

    public List<UploadHeader> getAllForUploadId(long uploadId) {
        return jdbi.withExtension(UploadHeaderDao.class, dao -> dao.getAllForUploadId(uploadId));
    }

    public void mapHeaders(long transactionDateHeaderId,
                           long memoHeaderId,
                           long debitHeaderId) {
        jdbi.withExtension(UploadHeaderDao.class, dao -> {
            dao.setHeaderType(transactionDateHeaderId, UploadHeaderType.TRANSACTION_DATE);
            dao.setHeaderType(memoHeaderId, UploadHeaderType.MEMO);
            dao.setHeaderType(debitHeaderId, UploadHeaderType.DEBIT_HEADER);
            return null;
        });
    }

}
