concern Transaction in transaction
{
  filtermodule transaction_advice
  {
    internals
      transaction : TransactionManagement;
    inputfilters
      meta : Meta = { [*.*] transaction.startTransaction }
  }
  
  superimposition
  {
    selectors
    	transactionSelector = { C | classHasAnnotationWithName(C, 'transaction.ApplyTransaction') };

    filtermodules
      transactionSelector <- transaction_advice;
  }
  
}