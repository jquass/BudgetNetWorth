package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.data.jdbi.upload.UploadManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Path("/uploads")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);

    private final UploadManager uploadManager;

    @Inject
    UploadResource(UploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }

    @GET
    @Path("/{id}/context")
    public Optional<UploadContext> getContext(@PathParam("id") long uploadId) {
        return uploadManager.getUploadContext(uploadId);
    }

    @POST
    @Path("/{id}/map-headers")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Optional<UploadContext> mapHeadersAjax(@PathParam("id") long uploadId,
                                                  @FormParam("transaction-date-header") long transactionDateHeaderId,
                                                  @FormParam("memo-header") long memoHeaderId,
                                                  @FormParam("debit-header") long debitHeaderId,
                                                  @FormParam("credit-header") Optional<Long> creditHeaderId) {
        return uploadManager.mapHeaders(uploadId, transactionDateHeaderId, memoHeaderId, debitHeaderId, creditHeaderId);
    }

    @POST
    @Path("/{id}/finalize")
    public Optional<UploadContext> finalizeUpload(@PathParam("id") long uploadId) {
        return uploadManager.finalizeUpload(uploadId);
    }
}
