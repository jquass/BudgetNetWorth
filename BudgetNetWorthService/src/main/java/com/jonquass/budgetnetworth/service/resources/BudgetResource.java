package com.jonquass.budgetnetworth.service.resources;

import com.jonquass.budgetnetworth.core.budget.Budget;
import com.jonquass.budgetnetworth.core.budget.BudgetEgg;
import com.jonquass.budgetnetworth.core.budget.BudgetUsageDetails;
import com.jonquass.budgetnetworth.core.budget.month.BudgetMonth;
import com.jonquass.budgetnetworth.core.budget.month.MonthYear;
import com.jonquass.budgetnetworth.data.jdbi.budget.BudgetDbManager;
import com.jonquass.budgetnetworth.data.jdbi.budget.month.BudgetMonthDbManager;
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
            .setId(0)
            .build();

    private final BudgetDbManager budgetDbManager;
    private final BudgetMonthDbManager budgetMonthDbManager;

    @Inject
    public BudgetResource(BudgetDbManager budgetDbManager,
                          BudgetMonthDbManager budgetMonthDbManager) {
        this.budgetDbManager = budgetDbManager;
        this.budgetMonthDbManager = budgetMonthDbManager;
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

    @GET
    @Path("/months")
    public List<BudgetMonth> getBudgetMonths(@QueryParam("month") Month month, @QueryParam("year") int year) {
        return budgetMonthDbManager.list();
    }

    @GET
    @Path("/months/{date}")
    public List<BudgetMonth> getBudgetMonthsForMonthYear(@PathParam("date") int date) {

        return budgetMonthDbManager.listForDate(date);
    }

    @GET
    @Path("/usage-details/{date}")
    public BudgetUsageDetails getBudgetUsageDetails(@PathParam("date") int date) {
        List<BudgetMonth> budgetMonths = budgetMonthDbManager.listForDate(date);

        return BudgetUsageDetails.builder()
                .setDate(date)
                .setBudgetMonths(budgetMonths)
                .setNetWorth(BigDecimal.TEN)
                .build();
    }

    @GET
    @Path("/month-years")
    public List<MonthYear> getMonthYears() {
        return budgetMonthDbManager.listMonthYears();
    }
}
