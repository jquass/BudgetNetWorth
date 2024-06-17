package com.jonquass.budgetnetworth.data.jdbi.upload.mapper;

import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderType;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UploadHeaderMapper implements RowMapper<UploadHeader> {

    @Override
    public UploadHeader map(ResultSet rs, StatementContext ctx) throws SQLException {
        UploadHeader.Builder uploadHeaderBuilder = UploadHeader.builder()
                .setId(rs.getLong("id"))
                .setUploadId(rs.getLong("upload_id"))
                .setValue(rs.getString("value"));

        if (rs.getString("type") != null) {
            uploadHeaderBuilder.setType(UploadHeaderType.valueOf(rs.getString("type")));
        }

        return uploadHeaderBuilder.build();
    }
}
