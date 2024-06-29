package com.jonquass.budgetnetworth.data.jdbi.upload.db.manager;

import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRowEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.upload.dao.UploadRowDao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

import static com.jonquass.budgetnetworth.data.BudgetNetWorthDataModule.DELETE_BATCH_SIZE;

@Singleton
public class UploadRowDbManager {

    private final Jdbi jdbi;

    @Inject
    public UploadRowDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public UploadRow insert(UploadRowEgg uploadRowEgg) {
        return jdbi.withExtension(UploadRowDao.class, dao -> {
            long id = dao.insert(uploadRowEgg.getUploadId(), uploadRowEgg.getFullRow());
            return dao.get(id).orElseThrow();
        });
    }

    public List<UploadRow> getAllForUploadId(long uploadId) {
        return jdbi.withExtension(UploadRowDao.class, dao -> dao.getAllForUploadId(uploadId));
    }

    public int deleteAllForUploadId(long uploadId) {
        return jdbi.withExtension(UploadRowDao.class, dao -> {
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
