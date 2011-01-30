package transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import Composestar.Java.FLIRT.Env.JoinPointContext;

public class TransactionManagement
	{
		public Stack<List<JoinPointContext>> actions;
		public static TransactionManagement tm = null;
		
	    public TransactionManagement()
		{
			actions = new Stack<List<JoinPointContext>>();
		}
	    
	    public static TransactionManagement getInstance(){
	    	if(tm == null){
	    		tm = new TransactionManagement();
	    	}
	    	return tm;
	    }
	
	    /**
	     * Starts the transaction. 
	     * This will add the transaction to the transaction stack.
	     * All method calls containing the @rollback annotation will
	     * be considered as actions in the transaction, until the
	     * transaction ends or an inner transaction starts.
	     * @param message
	     */
		public void startTransaction(JoinPointContext context)
		{
			System.out.println("Transaction started");
			actions.push(new ArrayList<JoinPointContext>());
		}
		
		/**
		 * Commits the transaction
		 * This will remove this transaction from the stack and considers it
		 * successfully completed.
		 */
		public void commitTransaction(JoinPointContext context){
			System.out.println("Transaction ended");
			actions.pop();
		}
		
		/**
		 * Rolls back the transaction.
		 * This  will roll back all actions done in the transaction and removes
		 * the transaction itself from the stack.
		 * The intercepted exception thrown by the message will be rethrown.
		 * @param message
		 */
		public void rollBack(JoinPointContext context){
			System.out.println("Rollback triggered");
//			for(ReifiedMessage m : actions.peek()){
//				Class<?> methodClass = m.getTarget().getClass();
//				System.out.println(methodClass);
////				Class<?>[] parameterTypes = new Class<?>[m.getArguments().length];
////				for(int i = 0; i < m.getArguments().length; i++){
////					parameterTypes[i] = m.getArgument(i).getClass();
////				}
//				Transaction transactionAnnotation = methodClass.getAnnotation(Transaction.class);
//				String reverseMethodName = "frop";
////				try {
////					Method method = methodClass.getMethod(reverseMethodName, parameterTypes);
////					method.invoke(m.getTarget(), m.getArguments());
////				} catch (SecurityException e) { // TODO: better error handling
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (NoSuchMethodException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (IllegalArgumentException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (IllegalAccessException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (InvocationTargetException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//			}
//			actions.pop();
		}
		
		/**
		 * Adds an action to the transaction stack
		 * @param message
		 */
		public void addAction(JoinPointContext context){
			if(!actions.isEmpty()){
				System.out.println("Rollbackaction added.");
				actions.peek().add(context);
			}
		}
}
