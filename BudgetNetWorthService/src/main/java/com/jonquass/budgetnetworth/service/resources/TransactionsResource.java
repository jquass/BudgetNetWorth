package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.transaction.Transaction;
import com.jonquass.budgetnetworth.data.budget.BudgetTransactionManager;
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
    private final BudgetTransactionManager budgetTransactionManager;

    @Inject
    public TransactionsResource(TransactionDbManager transactionDbManager, BudgetTransactionManager budgetTransactionManager) {
        this.transactionDbManager = transactionDbManager;
        this.budgetTransactionManager = budgetTransactionManager;
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
    public Optional<Transaction> updateTransactionBudgetId(
            @PathParam("transactionId") long transactionId,
            @QueryParam("budgetId") Optional<Long> budgetId
    ) {
        Optional<Transaction> transaction = transactionDbManager.get(transactionId);
        if (transaction.isEmpty()) {
            return Optional.empty();
        }

        LOG.info("Updating transaction with id {} and budget {}", transactionId, budgetId);
        budgetTransactionManager.updateBudgetId(transaction.get(), budgetId);
        return transactionDbManager.get(transactionId);
    }

}
