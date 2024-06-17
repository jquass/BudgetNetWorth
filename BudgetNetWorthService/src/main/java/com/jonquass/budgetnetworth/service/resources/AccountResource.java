package com.jonquass.budgetnetworth.service.resources;

import com.google.inject.Inject;
import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.core.account.Account;
import com.jonquass.budgetnetworth.core.account.AccountEgg;
import com.jonquass.budgetnetworth.core.upload.UploadContext;
import com.jonquass.budgetnetworth.data.csv.CsvReader;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountDbManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private static final int LIST_LIMIT = 100;

    private final AccountDbManager accountDbManager;
    private final CsvReader csvReader;

    @Inject
    public AccountResource(AccountDbManager accountDbManager,
                           CsvReader csvReader
    ) {
        this.accountDbManager = accountDbManager;
        this.csvReader = csvReader;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Account createAccountAjax(@FormParam("account-name") String accountName) {
        AccountEgg accountEgg = AccountEgg.builder().setAccountName(accountName).build();
        Account account = accountDbManager.insert(accountEgg);
        LOG.info("Created Account ID: {}", account.getId());
        return account;
    }

    @GET
    public List<Account> getAccounts() {
        return accountDbManager.list(LIST_LIMIT);
    }

    @DELETE
    @Path("/{id}")
    public Result<String, String> delete(@PathParam("id") long id) {
        return accountDbManager.delete(id);
    }

    @POST
    @Path("/{id}/stage-upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Optional<UploadContext> stageUploadAjax(@PathParam("id") long accountId,
                                                   File transactionsFile) {
        return csvReader.stageUpload(accountId, transactionsFile);
    }
}
