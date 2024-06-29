package com.jonquass.budgetnetworth.data.jdbi.upload.dao;

import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UploadHeaderDao {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO upload_headers (upload_id, value) VALUES (:upload_id, :value)")
    long insert(@Bind("upload_id") long uploadId,
                @Bind("value") String value);

    @SqlQuery("SELECT * FROM upload_headers WHERE id = :id")
    Optional<UploadHeader> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM upload_headers WHERE upload_id = :upload_id")
    List<UploadHeader> getAllForUploadId(@Bind("upload_id") long uploadId);

    @SqlUpdate("UPDATE upload_headers SET type = :type WHERE id = :id")
    void setHeaderType(@Bind("id") long id, @Bind("type") UploadHeaderType type);

    @SqlUpdate("UPDATE upload_headers SET type = null WHERE upload_id = :upload_id")
    void unmapHeadersForUpload(@Bind("upload_id") long uploadId);

    @SqlUpdate("DELETE FROM upload_headers WHERE upload_id = :upload_id LIMIT :limit")
    int delete(@Bind("upload_id") long uploadId, @Bind("limit") int limit);
}
