package com.jonquass.budgetnetworth.data;

import com.google.inject.Inject;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountMapper;
import org.jdbi.v3.guice.AbstractJdbiDefinitionModule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class BudgetNetWorthDataModule extends AbstractJdbiDefinitionModule {

    public static final String DATA_SOURCE = "data";

    @Inject
    public BudgetNetWorthDataModule() {
        super(GuiceJdbi.class);
    }

    @Override
    public void configureJdbi() {
        bindPlugin().to(SqlObjectPlugin.class);

        bindRowMapper().to(AccountMapper.class);
    }

}
