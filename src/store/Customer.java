package store;


import java.util.ArrayList;

public class Customer {
	private String name;
	private ArrayList<Product> basket;
	private String address;
	
	public Customer(String name){
		this.name = name;
		this.address = null;
		this.basket = new ArrayList<Product>();
	}
	
	public void addToBasket(Product product){
		basket.add(product);
	}
	
	public void removeFromBasket(Product product){
		basket.remove(product);
	}
	
	public ArrayList<Product> getBasket(){
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

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
}
