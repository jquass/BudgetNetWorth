package com.jonquass.budgetnetworth.service.resources;

import com.google.inject.Inject;
import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.core.account.AccountEgg;
import com.jonquass.budgetnetworth.data.html.HtmlReader;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountDbManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/account")
@Produces(MediaType.TEXT_HTML)
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final HtmlReader htmlReader;
    private final AccountDbManager accountDbManager;

    @Inject
    public AccountResource(HtmlReader htmlReader,
                           AccountDbManager accountDbManager
    ) {
        this.htmlReader = htmlReader;
        this.accountDbManager = accountDbManager;
    }

    @GET
    public String createAccount(@QueryParam("account_name") String accountName) {
        AccountEgg accountEgg = AccountEgg.builder()
                .setAccountName(accountName)
                .build();
        LOG.info(accountEgg.toString());
        LOG.info(accountDbManager.insert(accountEgg).toString());
        Result<String, String> result = htmlReader.readHtml(HtmlReader.HtmlFile.DASHBOARD);
        return result.isOk() ? result.unwrapOrElseThrow() : result.unwrapErrOrElseThrow();
    }

}
