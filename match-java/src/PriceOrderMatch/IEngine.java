package PriceOrderMatch;

public interface IEngine {
	int Limit(Order order);
	void Cancel(int orderID);	
}
