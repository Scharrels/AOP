package transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import Composestar.Java.FLIRT.Env.ReifiedMessage;

public class TransactionManagement
	{
		public Stack<List<ReifiedMessage>> actions;
		
	    public TransactionManagement()
		{
			actions = new Stack<List<ReifiedMessage>>();
		}
	
	    /**
	     * Starts the transaction. 
	     * This will add the transaction to the transaction stack.
	     * All method calls containing the @rollback annotation will
	     * be considered as actions in the transaction, until the
	     * transaction ends or an inner transaction starts.
	     * @param message
	     */
		public void startTransaction(ReifiedMessage message)
		{
			actions.push(new ArrayList<ReifiedMessage>());
		}
		
		/**
		 * Commits the transaction
		 * This will remove this transaction from the stack and considers it
		 * successfully completed.
		 */
		public void commitTransaction(){
			actions.pop();
		}
		
		/**
		 * Rolls back the transaction.
		 * This  will roll back all actions done in the transaction and removes
		 * the transaction itself from the stack.
		 * The intercepted exception thrown by the message will be rethrown.
		 * @param message
		 */
		public void rollBack(ReifiedMessage rollbackMessage){
			for(ReifiedMessage m : actions.peek()){
				Class<? extends ReifiedMessage> methodClass = m.getClass();
				ApplyTransaction transactionAnnotation = methodClass.getAnnotation(ApplyTransaction.class);
				String reverseMethodName = transactionAnnotation.reverseMethod();
				Class<?>[] parameterTypes = new Class<?>[m.getArguments().length];
				for(int i = 0; i < m.getArguments().length; i++){
					parameterTypes[i] = m.getArgument(i).getClass();
				}
				try {
					Method method = methodClass.getMethod(reverseMethodName, parameterTypes);
					method.invoke(m.getTarget(), m.getArguments());
				} catch (SecurityException e) { // TODO: better error handling
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			actions.pop();
		}
		
		/**
		 * Adds an action to the transaction stack
		 * @param message
		 */
		public void addAction(ReifiedMessage message){
			actions.peek().add(message);
		}
}
