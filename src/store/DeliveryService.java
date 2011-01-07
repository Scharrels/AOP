package store;


public class DeliveryService {
	public DeliveryService(){
		
	}
	
	public void deliver(Customer customer) throws DeliveryFailedException{
		if(customer.getAddress() == null){
			throw new DeliveryFailedException();
		}
	}
}
