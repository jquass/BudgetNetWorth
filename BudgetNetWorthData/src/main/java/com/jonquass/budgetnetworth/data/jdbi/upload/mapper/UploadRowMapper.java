package com.jonquass.budgetnetworth.data.jdbi.upload.mapper;

import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UploadRowMapper implements RowMapper<UploadRow> {

    @Override
    public UploadRow map(ResultSet rs, StatementContext ctx) throws SQLException {
        return UploadRow.builder()
                .setId(rs.getLong("id"))
                .setUploadId(rs.getLong("upload_id"))
                .setFullRow(rs.getString("full_row"))
                .build();
    }
}
