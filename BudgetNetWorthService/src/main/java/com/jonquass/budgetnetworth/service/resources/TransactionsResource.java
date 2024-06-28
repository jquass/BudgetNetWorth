package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {

    private final TransactionDbManager transactionDbManager;

    @Inject
    public TransactionsResource(TransactionDbManager transactionDbManager) {
        this.transactionDbManager = transactionDbManager;
    }

    @GET
    public List<Transaction> getTransactions(@QueryParam("limit") @DefaultValue("100") int limit) {
        return transactionDbManager.list(limit);
    }

}
