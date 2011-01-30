package store;
import java.util.HashMap;
import java.util.Map;

import transaction.Rollback;

public class Bank {
	
	public Map<Customer, Double> accounts;
	public Bank(){
		accounts = new HashMap<Customer, Double>();
	}
	
	public void addCustomer(Customer customer, Double initial){
		accounts.put(customer, initial);
	}
	
	public void withdraw(Customer customer, Double amount) throws InsufficientFundsException{
		if(amount > accounts.get(customer))
			throw new InsufficientFundsException();
		accounts.put(customer, accounts.get(customer) - amount);
	}
	
	public void deposit(Customer customer, Double amount) {
		if(accounts.get(customer) == null)
			addCustomer(customer, amount);
		else 
			accounts.put(customer, accounts.get(customer) + amount);
	}

	@Rollback
	public void supplyPayment(Customer customer, Double price) throws PaymentFailedException {
		System.out.println("a message in supplyPayment");
		if(accounts.get(customer) == null)
			throw new PaymentFailedException();
		try {
			withdraw(customer, price);
		} catch(InsufficientFundsException e){
			throw new PaymentFailedException();
		}
	}

	public Double getBalance(Customer customer) {
		return accounts.get(customer);
	}

}
