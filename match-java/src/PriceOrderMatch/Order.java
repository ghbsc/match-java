package PriceOrderMatch;

public class Order {
	short price;
	long size;
	int side;
	String symbol;
	String trader;
	
	public Order(String symbol, String trader, int side, short price, long size) {
		this.symbol = symbol;
		this.trader = trader;
		//Ask=1, Bid=0		
		this.side = side;
		this.price = price;
		this.size = size;
	}
}
