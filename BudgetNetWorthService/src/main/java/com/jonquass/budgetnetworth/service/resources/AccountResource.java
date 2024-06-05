package com.jonquass.budgetnetworth.service.resources;

import com.google.inject.Inject;
import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.core.account.Account;
import com.jonquass.budgetnetworth.core.account.AccountEgg;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountDbManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final AccountDbManager accountDbManager;

    @Inject
    public AccountResource(AccountDbManager accountDbManager
    ) {
        this.accountDbManager = accountDbManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Account createAccountAjax(@FormParam("account_name") String accountName) {
        AccountEgg accountEgg = AccountEgg.builder().setAccountName(accountName).build();
        Account account = accountDbManager.insert(accountEgg);
        LOG.info("Created Account ID: {}", account.getId());
        return account;
    }

    @GET
    public List<Account> getAccounts(
            @QueryParam("limit")
            @DefaultValue("10") int limit,
            @QueryParam("offset") long offset
    ) {
        return accountDbManager.list(limit);
    }

    @DELETE
    @Path("/{id}")
    public Result<String, String> delete(@PathParam("id") long id) {
        return accountDbManager.delete(id);
    }

}
