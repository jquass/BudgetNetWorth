package com.jonquass.budgetnetworth.data.jdbi.account;

import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.core.account.Account;
import com.jonquass.budgetnetworth.core.account.AccountEgg;
import com.jonquass.budgetnetworth.data.jdbi.GuiceJdbi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

@Singleton
public class AccountDbManager {

    private final Jdbi jdbi;

    @Inject
    AccountDbManager(@GuiceJdbi Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public Account insert(AccountEgg accountEgg) {
        return jdbi.withExtension(AccountDao.class, dao -> {
            long id = dao.insert(accountEgg.getAccountName());
            return dao.get(id).orElseThrow();
        });
    }

    public List<Account> list(int limit) {
        return jdbi.withExtension(AccountDao.class, dao -> dao.list(limit));
    }

    public Result<String, String> delete(long id) {
        return jdbi.withExtension(AccountDao.class, dao -> dao.delete(id)) == 1
                ? Result.ok("Successfully deleted id " + id)
                : Result.err("Failed to delete id " + id);
    }
}
