package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsResource.class);

    private final TransactionDbManager transactionDbManager;

    @Inject
    public TransactionsResource(TransactionDbManager transactionDbManager) {
        this.transactionDbManager = transactionDbManager;
    }

    @GET
    public List<Transaction> getTransactions(@QueryParam("limit") @DefaultValue("100") int limit) {
        return transactionDbManager.list(limit);
    }

    @GET
    @Path("/{transactionId}")
    public Optional<Transaction> getTransaction(@PathParam("transactionId") long transactionId) {
        return transactionDbManager.get(transactionId);
    }

    @PUT
    @Path("/{transactionId}")
    public Transaction updateTransaction(@PathParam("transactionId") long transactionId, @QueryParam("budgetId") long budgetId) {
        LOG.info("Updating transaction with id {} and budget {}", transactionId, budgetId);
        transactionDbManager.updateBudgetId(transactionId, budgetId);
        return transactionDbManager.get(transactionId).orElseThrow();
    }

}
