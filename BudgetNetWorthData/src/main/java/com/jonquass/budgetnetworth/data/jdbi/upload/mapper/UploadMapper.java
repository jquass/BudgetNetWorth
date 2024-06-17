package com.jonquass.budgetnetworth.data.jdbi.upload.mapper;

import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.core.upload.UploadStatus;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UploadMapper implements RowMapper<Upload> {

    @Override
    public Upload map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Upload.builder()
                .setId(rs.getLong("id"))
                .setAccountId(rs.getLong("account_id"))
                .setFilename(rs.getString("filename"))
                .setUploadStatus(UploadStatus.valueOf(rs.getString("status")))
                .build();
    }

}
