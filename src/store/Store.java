package store;

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
	
	@Rollback
	public void addStock(Product product, Integer amount){
		if(!stock.containsKey(product)){
			stock.put(product, amount);
		} else {
			stock.put(product, stock.get(product) + amount);
		}
	}
	
	public void addStock(List<Product> products){
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
	
	private void removeStock(List<Product> products) throws ProductNotAvailableException{
		ListIterator<Product> productIterator = products.listIterator();
		while(productIterator.hasNext()){
			Product product = productIterator.next();
			removeStock(product, 1);
		}
	}
	
	private Double getPrice(List<Product> products){
		Double price = 0.0;
		for(Product product : products){
			price += product.getPrice();
		}
		return price;
	}
	
	@Transaction
	public void checkout(Customer customer) throws PaymentFailedException, ProductNotAvailableException, DeliveryFailedException {
		List<Product> products = customer.getBasket();
		removeStock(products);
		System.out.println("Before supplyPayment");
		Double price = getPrice(products);
		bank.supplyPayment(customer, price);
		System.out.println("After supplyPayment");
		deliveryService.deliver(customer);
	}
}
