package com.jonquass.budgetnetworth.data.jdbi.upload.dao;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UploadDao {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO uploads (filename, status, account_id) VALUES (:filename, :status, :account_id)")
    long insert(@Bind("filename") String filename,
                @Bind("status") UploadStatus uploadStatus,
                @Bind("account_id") long accountId);


    @SqlQuery("SELECT * FROM uploads WHERE id = :id")
    Optional<Upload> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM uploads WHERE account_id = :account_id AND id > :offset_id ORDER BY id LIMIT :limit")
    List<Upload> getBatchForAccount(@Bind("account_id") long id, @Bind("offset_id") long offsetId, @Bind("limit") int limit);

    @SqlQuery("SELECT * FROM uploads WHERE status in ('STAGING', 'STAGED') ORDER BY id DESC LIMIT 1")
    Optional<Upload> getInProgress();

    @SqlUpdate("UPDATE uploads SET status = :status WHERE id = :id")
    void updateStatus(@Bind("id") long id, @Bind("status") UploadStatus uploadStatus);

    @SqlUpdate("DELETE FROM uploads WHERE id = :id")
    int delete(@Bind("id") long id);

}
