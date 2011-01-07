concern Transaction
{
  filtermodule transaction_advice
  {
    internals
      transaction : transaction.TransactionManagement;
    inputfilters
      meta : Meta = { [*.*] transaction.startTransaction }
  }
  
  superimposition
  {
    selectors
    	transactionSelector = { C | isClassWithName(C, 'store.Store') };

    filtermodules
      transactionSelector <- transaction_advice;
  }
  
}