package PriceOrderMatch;

import java.util.ListIterator;

public class Engine {
	DoublyLinkedList<Order> _asks;
	DoublyLinkedList<Order> _bids;
	int _nextid;
	
	public Engine() {
	  _nextid = 1;
	  _bids = new DoublyLinkedList<Order>();
	  _asks = new DoublyLinkedList<Order>();		
	}
		
	public interface ICrossAsk {
		boolean hit(short bid_price, short ask_price);
	}
	
	public interface ICrossBid {
		boolean hit(short ask_price, short bid_price);
	}	
	
	public interface IQueueAsk {
		boolean Prioritze(short ask_new, short ask_old);
	}
	
	public interface IQueueBid {
		boolean Prioritze(short bid_new, short bid_old);
	}		
	
	
	class QueueAsk implements IQueueAsk {
		@Override
		public boolean Prioritze(short ask_new, short ask_old) {
			return ask_new < ask_old;
		}
	}
	
	class QueueBid implements IQueueBid {
		@Override
		public boolean Prioritze(short bid_new, short bid_old) {
			return bid_new > bid_old;
		}
	}
	
	class CrossAsk implements ICrossAsk {
		@Override
		public boolean hit(short bid_price, short ask_price) {
			return bid_price >= ask_price;
		}
	}
	
	class CrossBid implements ICrossBid {
		@Override
		public boolean hit(short ask_price, short bid_price) {
			return ask_price <= bid_price;
		}
	}	
	
//	public static void main(String[] args) {
//	}

	public boolean Cross(Order order) {
		DoublyLinkedList<Order> book = order.side == 1? _bids : _asks;
		ListIterator<Order> iterator = book.iterator();		
		
		/*if(order.side == 1) {
			ask = new CrossAsk() {
				public boolean hit(short bid_price, short ask_price) {
					return bid_price >= ask_price;	
				}			
			};
		}
		else {
			bid = new CrossBid() {
				public boolean hit(short ask_price, short bid_price) {
					return ask_price <= bid_price;
				}			
			};			
		}*/
		
		while(iterator.hasNext()) {	//&& Cross_Test(order, iterator.next().price)
			Order bookIteratedOrder = iterator.next();
			
			if(Cross_Test(order, bookIteratedOrder.price)) {
				//Trade
				// new completely fills old	
				if(order.size >= bookIteratedOrder.size) {
					order.size -= bookIteratedOrder.size;
					iterator.remove();
				}
				// new partially fills old				
				else {
					bookIteratedOrder.size -= order.size;
					iterator.set(bookIteratedOrder);
					order.size = 0;
				}
			}
			
			if(order.size == 0) return true;
		}
		
		return false;
	}
	
	void Queue(Order order) {
		DoublyLinkedList<Order> book = order.side == 1? _asks : _bids;
		ListIterator<Order> iterator = book.iterator();
		
		while(iterator.hasNext() && !Priority_Test(order, iterator.next().price)) {
			if(iterator.hasNext())
				iterator.next();
		}
		iterator.add(order);
	}
	
	public void Destroy() {
		ListIterator<Order> askIterator = _asks.iterator();		
		while(askIterator.hasNext())
			askIterator.remove();
		
		ListIterator<Order> bidIterator = _bids.iterator();		
		while(bidIterator.hasNext())
			bidIterator.remove();		
	}
	
	public int Limit(Order order) {
	  if (!Cross(order)) Queue(order);
	  return _nextid++;
	}	
	
	
	private boolean Cross_Test(Order order, short bookPrice)
	{
		//Ask=1, Bid=0
		if(order.side == 1) 
			return new CrossBid().hit(order.price, bookPrice);			
		else
			return new CrossAsk().hit(order.price, bookPrice);			
	}
	
	private boolean Priority_Test(Order order, short bookPrice)
	{
		if(order.side == 1) 
			return new QueueAsk().Prioritze(order.price, bookPrice);
		else
			return new QueueBid().Prioritze(order.price, bookPrice);
	}
	
	
	
	
	
}
