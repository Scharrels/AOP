package store;


public class StoreTest {
	private static Bank bank;
	private static Store store;
	private static Product product1, product2;
	
	public static void main(String[] args){
		
		// create the bank
		bank = new Bank();
		
		// create some customers
		Customer customer1 = new Customer("Joe");
		Customer customer2 = new Customer("Jill");
		
		// add a bank account for the customers		
		bank.addCustomer(customer1, 5.00);
		bank.addCustomer(customer2, 15.00);
		
		// create delivery service
		DeliveryService service = new DeliveryService();
		
		// create the store
		store = new Store(bank, service);
		
		// create some products
		product1 = new Product("Harry Potter and the Failed Payments", 4.99);
		product2 = new Product("Lord of the Rings: The two customers", 9.99);
		
		// add the products to the store
		store.addStock(product1, 1);
		store.addStock(product2, 2);
		
		System.out.println("Test: checkout with sufficient stock (2,1) and sufficient funds and known address");
		customer1.setAddress("Calslaan");
		customer1.addToBasket(product1);
		tryCheckout(customer1);
		customer1.clearBasket();
		
		System.out.println("Test: checkout with sufficient stock (2,0) and sufficient funds, but unknown address");
		customer2.addToBasket(product2);
		tryCheckout(customer2);
		customer1.clearBasket();		
		
		System.out.println("Test: checkout with insufficient stock (2,0) and sufficient funds and known address");
		customer1.setAddress("Campuslaan");
		customer2.addToBasket(product1);
		customer2.addToBasket(product2);
		tryCheckout(customer2);
		customer2.clearBasket();
		
		System.out.println("Test: checkout with sufficient stock (2,0) and insufficient funds and known address");
		customer1.addToBasket(product2);
		tryCheckout(customer1);
		customer1.clearBasket();
	}

	private static void tryCheckout(Customer customer) {
		try {
			store.checkout(customer);
			System.out.println("Checkout successful.");
		} catch(ProductNotAvailableException e){
			System.out.println("The product is not available");
		} catch (PaymentFailedException e) {
			System.out.println("Payment failed");
		} catch (DeliveryFailedException e) {
			System.out.println("Delivery failed");
		}
		System.out.println(customer.getName() + " has a bank balance of " + String.format("%.2f", bank.getBalance(customer)) + " euros.");
		System.out.println("The store has " + store.getStock(product1) + " copies of " + product1.getName() + " in stock.");
		System.out.println("The store has " + store.getStock(product2) + " copies of " + product2.getName() + " in stock.");
		System.out.println();
	}
}
