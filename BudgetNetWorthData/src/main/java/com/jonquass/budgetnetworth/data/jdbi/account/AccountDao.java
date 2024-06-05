package com.jonquass.budgetnetworth.data.jdbi.account;

import com.jonquass.budgetnetworth.core.account.Account;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface AccountDao {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO accounts (account_name) VALUES (:account_name)")
    long insert(@Bind("account_name") String accountName);

    @SqlQuery("SELECT * FROM accounts WHERE id = :id")
    Optional<Account> get(@Bind("id") long id);

    @SqlQuery("SELECT * FROM accounts ORDER BY id ASC LIMIT :limit")
    List<Account> list(@Bind("limit") int limit);

    @SqlUpdate("DELETE FROM accounts WHERE id = :id")
    int delete(@Bind("id") long id);
}
