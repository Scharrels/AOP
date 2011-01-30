package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import transaction.Rollback;
import transaction.Transaction;

public class Store {
	Map<Product, Integer> stock;
	private Bank bank;
	private DeliveryService deliveryService;
	
	public Store(Bank bank, DeliveryService service){
		this.bank = bank;
		this.deliveryService = service;
		stock = new HashMap<Product, Integer>();
	}
	
	/**
	 * Returns the reverse method of a certain method
	 * Needed for composestar
	 * @param name
	 * @return the name of the reverse method
	 */
	public String getReverseName(String name){
		if(name.equals("addStock")){
			return "removeStock";
		} else if (name.equals("removeStock")){
			return "addStock";
		} else {
			return null;
		}
	}
	
	public Set<Product> getAllProducts(){
		return stock.keySet();
	}
	
	@Rollback
	public void addStock(Product product, Integer amount){
		if(!stock.containsKey(product)){
			stock.put(product, amount);
		} else {
			stock.put(product, stock.get(product) + amount);
		}
	}
	
	public void addStock(ArrayList<Product> products){
		for(Product product : products){
			addStock(product, 1);
		}
	}
	
	public int getStock(Product product){
		return stock.get(product);
	}
	
	@Rollback
	public void removeStock(Product product, Integer amount) throws ProductNotAvailableException {
		if(stock.get(product) < amount)
			throw new ProductNotAvailableException();
		stock.put(product, stock.get(product) - amount);
	}
	
	public void removeStock(ArrayList<Product> products) throws ProductNotAvailableException{
		ListIterator<Product> productIterator = products.listIterator();
		// remove all products from the stock
		while(productIterator.hasNext()){
			Product product = productIterator.next();
			removeStock(product, 1);
		}
	}
	
	public Double getPrice(ArrayList<Product> products){
		Double price = 0.0;
		for(Product product : products){
			price += product.getPrice();
		}
		return price;
	}
	
	@Transaction
	public void checkout(Customer customer) throws PaymentFailedException, ProductNotAvailableException, DeliveryFailedException {
		ArrayList<Product> products = customer.getBasket();
		removeStock(products);
		bank.supplyPayment(customer, getPrice(products));
		deliveryService.deliver(customer);
	}
}
