package com.jonquass.budgetnetworth.data.jdbi.upload.db.manager;

import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRowEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.upload.dao.UploadRowDao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

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

}
