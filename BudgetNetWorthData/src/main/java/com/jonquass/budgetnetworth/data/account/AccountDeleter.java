package com.jonquass.budgetnetworth.data.account;

import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.core.upload.Upload;
import com.jonquass.budgetnetworth.data.jdbi.account.AccountDbManager;
import com.jonquass.budgetnetworth.data.jdbi.transaction.TransactionDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadHeaderDbManager;
import com.jonquass.budgetnetworth.data.jdbi.upload.db.manager.UploadRowDbManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class AccountDeleter {

    private static final Logger LOG = LoggerFactory.getLogger(AccountDeleter.class);

    private final AccountDbManager accountDbManager;
    private final TransactionDbManager transactionDbManager;
    private final UploadDbManager uploadDbManager;
    private final UploadHeaderDbManager uploadHeaderDbManager;
    private final UploadRowDbManager uploadRowDbManager;

    @Inject
    AccountDeleter(AccountDbManager accountDbManager,
                   TransactionDbManager transactionDbManager,
                   UploadDbManager uploadDbManager,
                   UploadHeaderDbManager uploadHeaderDbManager,
                   UploadRowDbManager uploadRowDbManager
    ) {
        this.accountDbManager = accountDbManager;
        this.transactionDbManager = transactionDbManager;
        this.uploadDbManager = uploadDbManager;
        this.uploadHeaderDbManager = uploadHeaderDbManager;
        this.uploadRowDbManager = uploadRowDbManager;
    }

    @Transaction
    public Result<String, String> delete(long accountId) {
        int transactionsDeleted = transactionDbManager.deleteForAccount(accountId);
        LOG.info("Deleted {} transactions", transactionsDeleted);

        List<Upload> uploads = uploadDbManager.getAllForAccount(accountId);
        LOG.info("Deleting {} uploads", uploads.size());

        for (Upload upload : uploads) {
            int rowsDeleted = uploadRowDbManager.deleteAllForUploadId(upload.getId());
            LOG.info("Deleted {} rows", rowsDeleted);

            int headersDeleted = uploadHeaderDbManager.deleteAllForUploadId(upload.getId());
            LOG.info("Deleted {} headers", headersDeleted);

            int uploadDeleted = uploadDbManager.delete(upload.getId());
            if (uploadDeleted > 0) {
                LOG.info("Deleted Upload {}", upload.getId());
            } else {
                LOG.error("Failed to Delete Upload {}", upload.getId());
            }
        }

        return accountDbManager.delete(accountId);
    }
}
