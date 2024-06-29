package com.jonquass.budgetnetworth.data.jdbi.upload.dao;

import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UploadRowDao {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO upload_rows (upload_id, full_row) VALUES (:upload_id, :full_row)")
    long insert(@Bind("upload_id") long uploadId,
                @Bind("full_row") String filename);


    @SqlQuery("SELECT * FROM upload_rows WHERE id = :id")
    Optional<UploadRow> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM upload_rows WHERE upload_id = :upload_id")
    List<UploadRow> getAllForUploadId(@Bind("upload_id") long uploadId);

    @SqlUpdate("DELETE FROM upload_rows WHERE upload_id = :upload_id LIMIT :limit")
    int delete(@Bind("upload_id") long uploadId, @Bind("limit") int limit);
}
