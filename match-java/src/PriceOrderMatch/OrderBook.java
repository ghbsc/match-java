package PriceOrderMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class OrderBook implements IEngine {
	short _askMin;
	short _bidMax;
	int _curOrderID;
	
	// pricePoint: describes a single price point in the limit order book.	
	LinkedList<OrderBookEntry> _pricePoint;
	
	// An array of pricePoint structures representing the entire limit order book
	ArrayList<OrderBookEntry> _arenaBookEntries;
	ArrayList<LinkedList<OrderBookEntry>> _pricePoints;	
	
	public OrderBook() {
		final int MAX_ENTRY_SIZE = 101000;
		final int MAX_PRICE_SIZE = 100000;
		
		//_arenaBookEntries = new ArrayList<OrderBookEntry>(Collections.nCopies(MAX_ENTRY_SIZE, new OrderBookEntry()));
		_arenaBookEntries = new ArrayList<OrderBookEntry>(MAX_ENTRY_SIZE);
		while(_arenaBookEntries.size() < MAX_ENTRY_SIZE)
			_arenaBookEntries.add(new OrderBookEntry());
//		for(OrderBookEntry e : _arenaBookEntries)

		//_pricePoints = new ArrayList<LinkedList<OrderBookEntry>>(Collections.nCopies(MAX_PRICE_SIZE, new LinkedList<OrderBookEntry>()));
		_pricePoints = new ArrayList<LinkedList<OrderBookEntry>>(MAX_PRICE_SIZE);
		while(_pricePoints.size() < MAX_PRICE_SIZE)
			_pricePoints.add(new LinkedList<OrderBookEntry>());
			
		_curOrderID = 0;
		_askMin = Short.MAX_VALUE;
		_bidMax = 1;
	}
	
	public int Limit(Order order) {
		short price = order.price;
		long orderSize = order.size;
		
		if(order.side == 0) {
		    if (price >= _askMin) {
		    	LinkedList<OrderBookEntry> bookEntries = _pricePoints.get(_askMin);
		    	
		    	do { 
			    	ListIterator<OrderBookEntry> iterator = (ListIterator<OrderBookEntry>)bookEntries.iterator();
			    	
			    	while(iterator.hasNext()) {
			    		OrderBookEntry bookEntry = iterator.next();
			    		
			    		//Completely fill
			    		if(bookEntry.size < orderSize) {
			    			//Trade
			    			orderSize -= bookEntry.size;
			    			iterator.remove();
			    		}
			    		else {
			    			//Trade
			    			if(bookEntry.size > orderSize) {
			    				//Still have left over
				    			bookEntry.size -= orderSize;
				    			iterator.set(bookEntry);	
				    			order.size = 0;				    			
			    			}
			    			else 
			    				//if(iterator.hasNext()) iterator.next();
			    				iterator.remove();
			    			
			    			
			    			return ++_curOrderID;
			    		}
			    	}
			    	
			    	_askMin++;
			    	bookEntries = _pricePoints.get(_askMin);
		    	} while (price >= _askMin);
      		} //End price >= _askMin
		    
		    //Queue
		    OrderBookEntry entry = _arenaBookEntries.get(++_curOrderID);
		    entry.size = orderSize;
		    _pricePoints.get(price).addLast(entry);
		    
		    if (_bidMax < price) _bidMax = price;
		    return _curOrderID; 
		
		} //End order.side == 0
		else { //Sell
		    if (price <= _bidMax) {
		    	LinkedList<OrderBookEntry> bookEntries = _pricePoints.get(_bidMax);
		    	
		    	do { 
			    	ListIterator<OrderBookEntry> iterator = (ListIterator<OrderBookEntry>)bookEntries.iterator();
			    	
			    	while(iterator.hasNext()) {
			    		OrderBookEntry bookEntry = iterator.next();
			    		
			    		//Completely fill
			    		if(bookEntry.size < orderSize) {
			    			//Trade
			    			orderSize -= bookEntry.size;
			    			iterator.remove();
			    		}
			    		else {
			    			//Trade
			    			if(bookEntry.size > orderSize) {
				    			bookEntry.size -= orderSize;
				    			iterator.set(bookEntry);
				    			order.size = 0;
			    			}
			    			else
			    				iterator.remove();
			    			
			    			
			    			return ++_curOrderID;
			    		}
			    	}
			    	
			    	_bidMax--;
			    	bookEntries = _pricePoints.get(_bidMax);
		    	} while (price <= _bidMax);
      		} //End price >= _askMin			
			
		    //Queue
		    OrderBookEntry entry = _arenaBookEntries.get(++_curOrderID);
		    entry.size = orderSize;
		    _pricePoints.get(price).addLast(entry);
		    
		    if (_askMin > price) _askMin = price;
		    return _curOrderID; 		    
			
		} //End Sell
	} //End Limit

	public void Cancel(int orderID) {
		_arenaBookEntries.get(orderID).size = 0;
	}
	
} //End Class

