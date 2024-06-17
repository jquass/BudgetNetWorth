package com.jonquass.budgetnetworth.data.jdbi.upload.db.manager;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadEgg;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.upload.dao.UploadDao;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

@Singleton
public class UploadDbManager {

    private final Jdbi jdbi;

    @Inject
    UploadDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Upload insert(UploadEgg upload) {
        return jdbi.withExtension(UploadDao.class, dao -> {
            long id = dao.insert(upload.getFilename(), upload.getUploadStatus(), upload.getAccountId());
            return dao.get(id).orElseThrow();
        });
    }

    public Optional<Upload> get(long id) {
        return jdbi.withExtension(UploadDao.class, dao -> dao.get(id));
    }

    public void updateStatus(long id, UploadStatus uploadStatus) {
        jdbi.withExtension(UploadDao.class, dao -> {
            dao.updateStatus(id, uploadStatus);
            return null;
        });
    }
}
