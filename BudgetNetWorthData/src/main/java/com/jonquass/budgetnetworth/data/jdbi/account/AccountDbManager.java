package com.jonquass.budgetnetworth.data.jdbi.account;

import com.jonquass.budgetnetworth.core.account.Account;
import com.jonquass.budgetnetworth.core.account.AccountEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

@Singleton
public class AccountDbManager {

    private final Jdbi jdbi;

    @Inject
    AccountDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Account insert(AccountEgg accountEgg) {
        return jdbi.withHandle(handle ->
        {
            long id = handle.createUpdate("INSERT INTO accounts (account_name) VALUES (:account_name)")
                    .bind("account_name", accountEgg.getAccountName())
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(Long.class)
                    .one();
            return handle.createQuery("SELECT * FROM accounts WHERE id = :id")
                    .bind("id", id)
                    .mapTo(Account.class)
                    .one();
        });
    }
}
