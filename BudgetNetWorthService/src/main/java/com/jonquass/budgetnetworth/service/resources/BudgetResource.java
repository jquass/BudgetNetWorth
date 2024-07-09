package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.budget.Budget;
import com.jonquass.budgetnetworth.core.budget.BudgetEgg;
import com.jonquass.budgetnetworth.data.jdbi.budget.BudgetDbManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Path("/budgets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BudgetResource {

    private static final Budget DEFAULT_BUDGET = Budget.builder()
            .setBudgetName("Income")
            .setAmount(BigDecimal.ZERO)
            .setStartMonth(Month.JANUARY)
            .setStartYear(0)
            .setId(0)
            .setMonthPeriod(1)
            .build();

    private final BudgetDbManager budgetDbManager;

    @Inject
    public BudgetResource(BudgetDbManager budgetDbManager) {
        this.budgetDbManager = budgetDbManager;
    }

    @POST
    public Budget createBudget(BudgetEgg budgetEgg) {
        return budgetDbManager.insert(budgetEgg).orElseThrow();
    }

    @GET
    public List<Budget> getBudgets() {
        List<Budget> budgets = new ArrayList<>(budgetDbManager.list());
        budgets.addFirst(DEFAULT_BUDGET);
        return budgets;

    }
}
