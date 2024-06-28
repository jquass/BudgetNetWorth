package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.core.upload.header.UploadHeaderMapping;
import com.jonquass.budgetnetworth.data.csv.CsvReader;
import com.jonquass.budgetnetworth.data.jdbi.upload.UploadManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.io.File;
import java.util.Optional;

@Path("/uploads")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource {

    private final CsvReader csvReader;
    private final UploadManager uploadManager;

    @Inject
    UploadResource(CsvReader csvReader, UploadManager uploadManager) {
        this.csvReader = csvReader;
        this.uploadManager = uploadManager;
    }

    @GET
    @Path("/{id}/context")
    public Optional<UploadContext> getContext(@PathParam("id") long uploadId) {
        return uploadManager.getUploadContext(uploadId);
    }

    @POST
    @Path("/account/{account-id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Optional<UploadContext> createUpload(@PathParam("account-id") long accountId,
                                                File file) {
        return csvReader.stageUpload(accountId, file);
    }

    @POST
    @Path("/{id}/map-headers")
    public Optional<UploadContext> mapHeadersAjax(@PathParam("id") long uploadId,
                                                  UploadHeaderMapping uploadHeaderMapping) {
        return uploadManager.mapHeaders(
                uploadId,
                uploadHeaderMapping.getTransactionDateHeaderId(),
                uploadHeaderMapping.getMemoHeaderId(),
                uploadHeaderMapping.getDebitHeaderId(),
                uploadHeaderMapping.getCreditHeaderId()
        );
    }

    @POST
    @Path("/{id}/finalize")
    public Optional<UploadContext> finalizeUpload(@PathParam("id") long uploadId) {
        return uploadManager.finalizeUpload(uploadId);
    }
}
