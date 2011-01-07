package store;

import java.util.ArrayList;
import java.util.List;

public class Customer {
	private String name;
	private List<Product> basket;
	
	public Customer(String name){
		this.name = name;
		this.basket = new ArrayList<Product>();
	}
	
	public void addToBasket(Product product){
		basket.add(product);
	}
	
	public void removeFromBasket(Product product){
		basket.remove(product);
	}
	
	public List<Product> getBasket(){
		return basket;
	}
	
	public void clearBasket(){
		basket.clear();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
