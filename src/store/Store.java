package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	public Set<Product> getAllProducts(){
		return stock.keySet();
	}
	
	@Rollback(reverseMethod = "removeStock")
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
	
	@Rollback(reverseMethod = "addStock")
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
	
	private double getPrice(List<Product> products){
		double price = 0.0;
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
