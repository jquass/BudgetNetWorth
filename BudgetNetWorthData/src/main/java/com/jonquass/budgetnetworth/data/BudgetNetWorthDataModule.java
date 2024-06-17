package com.jonquass.budgetnetworth.data;

import com.google.inject.Inject;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountMapper;
import com.jonquass.budgetnetworth.data.jdbi.upload.mapper.UploadHeaderMapper;
import com.jonquass.budgetnetworth.data.jdbi.upload.mapper.UploadMapper;
import com.jonquass.budgetnetworth.data.jdbi.upload.mapper.UploadRowMapper;
import org.jdbi.v3.guice.AbstractJdbiDefinitionModule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class BudgetNetWorthDataModule extends AbstractJdbiDefinitionModule {

    @Inject
    public BudgetNetWorthDataModule() {
        super(GuiceJdbi.class);
    }

    @Override
    public void configureJdbi() {
        bindPlugin().to(SqlObjectPlugin.class);

        bindRowMapper().to(AccountMapper.class);
        bindRowMapper().to(UploadMapper.class);
        bindRowMapper().to(UploadRowMapper.class);
        bindRowMapper().to(UploadHeaderMapper.class);
    }

}
