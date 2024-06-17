package com.jonquass.budgetnetworth.data.jdbi.upload.dao;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface UploadDao {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO uploads (filename, status, account_id) VALUES (:filename, :status, :account_id)")
    long insert(@Bind("filename") String filename,
                @Bind("status") UploadStatus uploadStatus,
                @Bind("account_id") long accountId);


    @SqlQuery("SELECT * FROM uploads WHERE id = :id")
    Optional<Upload> get(@Bind("id") long id);

    @SqlUpdate("UPDATE uploads SET status = :status WHERE id = :id")
    void updateStatus(@Bind("id") long id, @Bind("status") UploadStatus uploadStatus);
}
