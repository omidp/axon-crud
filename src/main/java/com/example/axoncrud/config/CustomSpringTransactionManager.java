package com.example.axoncrud.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.axonframework.common.Assert;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

public class CustomSpringTransactionManager implements TransactionManager {

	private final PlatformTransactionManager transactionManager;
	private final TransactionDefinition transactionDefinition;
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	/**
	 * @param transactionManager    The transaction manager to use
	 * @param transactionDefinition The definition for transactions to create
	 */
	public CustomSpringTransactionManager(PlatformTransactionManager transactionManager,
										  TransactionDefinition transactionDefinition, DataSource dataSource,
										  EntityManagerFactory entityManagerFactory) {
		Assert.notNull(transactionManager, () -> "transactionManager may not be null");
		this.transactionManager = transactionManager;
		this.transactionDefinition = transactionDefinition;
		this.dataSource = dataSource;
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * Initializes the SpringTransactionManager with the given {@code transactionManager} and the default
	 * transaction definition.
	 *
	 * @param transactionManager the transaction manager to use
	 */
	public CustomSpringTransactionManager(PlatformTransactionManager transactionManager, DataSource dataSource,
										  EntityManagerFactory entityManagerFactory) {
		this(transactionManager, new DefaultTransactionDefinition(), dataSource, entityManagerFactory);
	}


	@Override
	public Transaction startTransaction() {
		TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
		return new Transaction() {
			@Override
			public void commit() {
				commitTransaction(status);
			}

			@Override
			public void rollback() {
				rollbackTransaction(status);
			}
		};
	}

	/**
	 * Commits the transaction with given {@code status} if the transaction is new and not completed.
	 *
	 * @param status The status of the transaction to commit
	 */
	protected void commitTransaction(TransactionStatus status) {
		if (status.isNewTransaction() && !status.isCompleted()) {
			transactionManager.commit(status);
		}
	}

	/**
	 * Rolls back the transaction with given {@code status} if the transaction is new and not completed.
	 *
	 * @param status The status of the transaction to roll back
	 */
	protected void rollbackTransaction(TransactionStatus status) {
		if (status.isNewTransaction() && !status.isCompleted()) {
			transactionManager.rollback(status);
		}
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}