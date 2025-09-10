package com.bank.service;

import com.bank.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    /**
     * 创建交易
     *
     * @param transaction 交易对象
     * @return 创建的交易对象
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * 根据编号获取交易信息
     *
     * @param reference 编号
     * @return 交易对象信息
     */
    Transaction getTransactionByReference(String reference);

    /**
     * 获取交易信息
     *
     * @param pageable 分页参数
     * @return 交易信息
     */
    Page<Transaction> getAllTransactions(Pageable pageable);

    /**
     * 更新交易信息
     *
     * @param reference 编号
     * @param transactionDetails 要更新的交易信息
     * @return 更新后交易信息
     */
    Transaction updateTransaction(String reference, Transaction transactionDetails);

    /**
     * 删除交易信息
     * @param id key
     */
    void deleteTransaction(Long id);
}
