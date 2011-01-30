package transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import Composestar.Java.FLIRT.Env.JoinPointContext;
import Composestar.Java.FLIRT.Env.ReifiedMessage;

public class TransactionManagement
	{
		public Deque<List<JoinPointContext>> actions;
		public static TransactionManagement tm = null;
		public boolean performingRollback;
		public Exception lastException;
		
	    public TransactionManagement()
		{
			actions = new LinkedBlockingDeque<List<JoinPointContext>>();
			performingRollback = false;
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
		public void startTransaction()
		{
			actions.push(new CopyOnWriteArrayList<JoinPointContext>());
		}
		
		/**
		 * Commits the transaction
		 * This will remove this transaction from the stack and considers it
		 * successfully completed.
		 */
		public void commitTransaction(){
			actions.pop();
		}
		
		public void handleTransaction(ReifiedMessage message) throws Exception {
			Class<?> methodClass = message.getTarget().getClass();
			Class<?>[] parameterTypes = new Class<?>[message.getArguments().length];
			for(int i = 0; i < message.getArguments().length; i++){
				parameterTypes[i] = message.getArgument(i).getClass();
			}
			
			try {
				startTransaction();
				Method method = methodClass.getMethod(message.getSelector(), parameterTypes);
				Object returnValue = method.invoke(message.getTarget(), message.getArguments());
				commitTransaction();
				message.setReturnValue(returnValue);
				message.reply();
			} catch(Exception e){
				rollBack();
				lastException = e;
			}
		}
		
		public void dispatchException(Object...params) throws Exception{
			throw lastException;
		}
		
		/**
		 * Rolls back the transaction.
		 * This  will roll back all actions done in the transaction and removes
		 * the transaction itself from the stack.
		 * The intercepted exception thrown by the message will be rethrown.
		 * @param message
		 */
		public void rollBack(){
			performingRollback = true;
			for(JoinPointContext m : actions.peek()){
				Class<?> methodClass = m.getTarget().getClass();
				System.out.println(methodClass);
				Class<?>[] parameterTypes = new Class<?>[m.getArguments().length];
				for(int i = 0; i < m.getArguments().length; i++){
					parameterTypes[i] = m.getArgument(i).getClass();
				}
				try {
					Method reverseNameMethod = methodClass.getMethod("getReverseName", String.class);
					String reverseMethodName = (String)(reverseNameMethod.invoke(m.getTarget(), m.getSelector()));
					System.out.println("method asked: "+ m.getSelector() +" method returned: " + reverseMethodName);
					Method method = methodClass.getMethod(reverseMethodName, parameterTypes);
					method.invoke(m.getTarget(), m.getArguments());
				} catch (SecurityException e) { // TODO: better error handling
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			actions.pop();
			performingRollback = false;
		}
		
		/**
		 * Adds an action to the transaction stack
		 * @param message
		 */
		public void addAction(JoinPointContext context){
			if(!actions.isEmpty() && !performingRollback){
				actions.peek().add(context);
			}
		}
}
