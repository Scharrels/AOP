package transaction;

public aspect TransactionControl {
	
	TransactionManagement tm = new TransactionManagement();
	
	public pointcut transactionalMethods() : execution(@Transaction * *.*(..));
	public pointcut rollbackMethods() : execution(@Rollback * *.*(..));
	
	before(): transactionalMethods() {
		tm.startTransaction(thisJoinPoint);
	}
	
	after() returning: transactionalMethods() {
		tm.commitTransaction();
	}
	
	after() throwing: transactionalMethods() {
		tm.rollBack(thisJoinPoint);
	}
	
	after() returning: rollbackMethods(){
		tm.addAction(thisJoinPoint);
	}
}