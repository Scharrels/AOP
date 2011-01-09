package store;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import transaction.ApplyTransaction;
import transaction.Rollback;
import transaction.Transaction;

@ApplyTransaction
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
	
	@Rollback
	public void addStock(Product product, int amount){
		if(!stock.containsKey(product)){
			stock.put(product, amount);
		} else {
			stock.put(product, stock.get(product) + amount);
		}
	}
	
	@Rollback
	public void addStock(List<Product> products){
		for(Product product : products){
			addStock(product, 1);
		}
	}
	
	public int getStock(Product product){
		return stock.get(product);
	}
	
	@Rollback
	public void removeStock(Product product, int amount) throws ProductNotAvailableException {
		if(stock.get(product) < amount)
			throw new ProductNotAvailableException();
		stock.put(product, stock.get(product) - amount);
	}
	
	@Rollback
	private void removeStock(List<Product> products) throws ProductNotAvailableException{
		ListIterator<Product> productIterator = products.listIterator();
		try {
			// remove all products from the stock
			while(productIterator.hasNext()){
				Product product = productIterator.next();
				removeStock(product, 1);
			}
		} catch(ProductNotAvailableException e){
			productIterator.previous();
			// add the already removed products back to the stock
			while(productIterator.hasPrevious()){
				Product product = productIterator.previous();
				addStock(product,1);
			}
			throw e;
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
		List<Product> products = customer.getBasket();
		removeStock(products);
		try {
			bank.supplyPayment(customer, getPrice(products));
			deliveryService.deliver(customer);
		} catch(PaymentFailedException e){
			addStock(products);
			throw e;
		} catch (DeliveryFailedException e) {
			bank.deposit(customer, getPrice(products));
			addStock(products);
			throw e;
		}
	}
}
