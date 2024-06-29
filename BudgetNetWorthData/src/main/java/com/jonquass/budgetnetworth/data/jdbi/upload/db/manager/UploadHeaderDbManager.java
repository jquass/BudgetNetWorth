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

import static com.jonquass.budgetnetworth.data.BudgetNetWorthDataModule.DELETE_BATCH_SIZE;

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

    public void mapHeader(long headerId, UploadHeaderType uploadHeaderType) {
        jdbi.withExtension(UploadHeaderDao.class, dao -> {
            dao.setHeaderType(headerId, uploadHeaderType);
            return null;
        });
    }

    public void unmapHeadersForUpload(long uploadId) {
        jdbi.withExtension(UploadHeaderDao.class, dao -> {
            dao.unmapHeadersForUpload(uploadId);
            return null;
        });
    }

    public int deleteAllForUploadId(long uploadId) {
        return jdbi.withExtension(UploadHeaderDao.class, dao -> {
            int totalDeleted = 0;
            int deleted;
            do {
                deleted = dao.delete(uploadId, DELETE_BATCH_SIZE);
                totalDeleted += deleted;
            } while (deleted > 0);
            return totalDeleted;
        });
    }

}
