concern TransactionControl in transaction
{
  filtermodule transaction_advice(??transactions)
  {
    externals
      transaction: transaction.Transaction = transaction.TransactionManagement.getInstance();
    inputfilters
       start : Before = (selector == ??transactions) { 
         filter.target = transaction;
         filter.selector = "startTransaction"; 
       };
       commit : After = (selector == ??transactions) { 
         filter.target = transaction;
         filter.selector = "commitTransaction"; 
       }
  }
  
  filtermodule rollback_advice(??rollbackactions)
  {
    externals
      transaction: transaction.Transaction = transaction.TransactionManagement.getInstance();
    inputfilters
       start : After = (selector == ??rollbackactions) { 
         filter.target = transaction;
         filter.selector = "addAction"; 
       }
  }  
  
  superimposition
  {
    selectors
    	transactionClasses = { C | methodHasAnnotationWithName(M, 'transaction.Transaction'), classHasMethod(C, M)};
    	transactions = { M | methodHasAnnotationWithName(M, 'transaction.Transaction')};
    	rollbackClasses = { C | methodHasAnnotationWithName(M, 'transaction.Rollback'), classHasMethod(C, M)};
    	rollbackactions = { M | methodHasAnnotationWithName(M, 'transaction.Rollback')};

    filtermodules
      transactionClasses <- transaction_advice(transactions);
      rollbackClasses <- rollback_advice(rollbackactions);
  }
  
}