package store;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import transaction.Rollback;

public class Bank {
	
	private Map<Customer, Double> accounts;
	public Bank(){
		accounts = new ConcurrentHashMap<Customer, Double>();
	}
	
	/**
	 * Returns the reverse method of a certain method
	 * Needed for composestar
	 * @param name
	 * @return the name of the reverse method
	 */
	public String getReverseName(String name){
		if(name.equals("supplyPayment")){
			return "deposit";
		} else {
			return null;
		}
	}	
	
	public void addCustomer(Customer customer, double initial){
		accounts.put(customer, initial);
	}
	
	public void withdraw(Customer customer, Double amount) throws InsufficientFundsException{
		if(amount > accounts.get(customer))
			throw new InsufficientFundsException();
		accounts.put(customer, accounts.get(customer) - amount);
	}
	
	public void deposit(Customer customer, Double amount) {
		System.out.println("Performing deposit of " + amount + " for " + customer.getName());
		if(accounts.get(customer) == null)
			addCustomer(customer, amount);
		else 
			accounts.put(customer, accounts.get(customer) + amount);
		
		System.out.println("Money for " + customer.getName() + ": " + accounts.get(customer));
	}

	@Rollback
	public void supplyPayment(Customer customer, Double price) throws PaymentFailedException {
		if(accounts.get(customer) == null)
			throw new PaymentFailedException();
		try {
			withdraw(customer, price);
		} catch(InsufficientFundsException e){
			throw new PaymentFailedException();
		}
	}

	public double getBalance(Customer customer) {
		return accounts.get(customer);
	}

}
