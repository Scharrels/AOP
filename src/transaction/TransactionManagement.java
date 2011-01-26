package transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import org.aspectj.lang.JoinPoint;
public class TransactionManagement
	{
		public Deque<List<JoinPoint>> actions;
		
	    public TransactionManagement()
		{
			actions = new LinkedBlockingDeque<List<JoinPoint>>();
		}
	
	    /**
	     * Starts the transaction. 
	     * This will add the transaction to the transaction stack.
	     * All method calls containing the @rollback annotation will
	     * be considered as actions in the transaction, until the
	     * transaction ends or an inner transaction starts.
	     * @param message
	     */
		public void startTransaction(JoinPoint message)
		{
			actions.push(new CopyOnWriteArrayList<JoinPoint>());
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
		public void rollBack(JoinPoint rollbackMessage){
			for(JoinPoint m : actions.peek()){
				Class<?> methodClass = m.getThis().getClass();
				String methodName = m.getSignature().getName();
				try {
					Class<?>[] parameterTypes = new Class<?>[m.getArgs().length];
					for(int i = 0; i < m.getArgs().length; i++){
						parameterTypes[i] = m.getArgs()[i].getClass();
					}					
					
					Method method = methodClass.getMethod(methodName, parameterTypes);
					
					Rollback transactionAnnotation = method.getAnnotation(Rollback.class);
					String reverseMethodName = transactionAnnotation.reverseMethod();

					Method reverseMethod = methodClass.getMethod(reverseMethodName, parameterTypes);
					reverseMethod.invoke(m.getTarget(), m.getArgs());
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
		public void addAction(JoinPoint message){
			if(!actions.isEmpty())
			actions.peek().add(message);
		}
}
