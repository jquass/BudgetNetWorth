package com.jonquass.budgetnetworth.core.upload;

import com.jonquass.budgetnetworth.core.BNWStyle;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeader;
import com.jonquass.budgetnetworth.core.upload.row.MappedUploadRow;
import com.jonquass.budgetnetworth.core.upload.row.UploadRow;
import org.immutables.value.Value;

import java.util.List;

@BNWStyle
@Value.Immutable
public interface UploadContextIF {

    Upload getUpload();

    List<UploadHeader> getUploadHeaders();

    List<UploadRow> getUploadRows();

    List<MappedUploadRow> getMappedUploadRows();

    @Value.Lazy
    default long getUploadId() {
        return getUpload().getId();
    }

}
