package PriceOrderMatch;

public class TestHelper {
	Engine _engine;
	
	Order Initoa101x100() {
		return new Order("JPM", "MAX", 1, (short)101, 100);
	}	
	
	Order Initob110x100() {
		return new Order("JPM", "MAX", 0, (short)110, 100);
	}		
	
	Order Initob110x50() {
		return new Order("JPM", "MAX", 0, (short)110, 50);
	}		
	
	Order Initob110x25() { 
		return new Order("JPM", "MAX", 0, (short)110, 25);	
	}	
	
	Order Initob110x25x() { 
		return new Order("JPM", "XAM", 0, (short)110, 25);	
	}
	
	int orderid;
	
	void set_globals() {
	  _engine = new Engine();		
	  orderid = 0;
//		  totaltests++;
//		  exec_overflow = 0;
//		  execs_out_iter = execs_out;
//		  execs_out_len = 0;
		}
		
	void Run()
	{
		Order[] ask = { Initoa101x100() };
		test(ask, ask.length);
		
		//sell, bid -> Cross sell
		Order[] inOrder = { Initoa101x100(), Initob110x100() };
		test(inOrder, inOrder.length);
		
		//Bid, sell -> Cross bid
		Order[] reOrder = { Initob110x100(), Initoa101x100() };
		test(reOrder, reOrder.length);		
		
		//Partial fill
		Order[] partialFill = { Initoa101x100(), Initob110x50() };
		test(partialFill, partialFill.length);
		
		//Incremental fill
		Order[] incrementalFill = { Initoa101x100(), Initob110x25(), Initob110x25(), Initob110x25(), Initob110x25(), Initob110x25() };
		test(incrementalFill, incrementalFill.length);
		
		Order[] queueThenTrade = { Initob110x25x(), Initob110x25x()};
		test(queueThenTrade, queueThenTrade.length);
		//TEST(3, {oa101x100 X ob101x100}, {xa101x100 X xb101x100}); 		
		
	}	
	
	int feed_orders(Order[] orders, int orders_len) {
	  int id;
	  int i;
	  
	  for(i = 0; i < orders_len; i++) {
	    id = _engine.Limit(orders[i]);
	    orderid++;
	    if (id != orderid) {
	      System.out.println(String.format("orderid returned was %u, should have been %u.\n", 
		     id, i+1));
	      return 0;
	    }
	  }
	  return 1;		
	}
	
	int feed_cancels(int[] cancels, int cancels_len) {
		  int i;
		  for(i = 0; i < cancels_len; i++) {
			_engine.Cancel(cancels[i]); 
		  }
		  return 1;
		}	
	
	/* IN: orders: sequence of orders
	   OUT: points received on test */
	int test(Order[] orders, int orders_len) {
	  int ok = 1;
	  set_globals();
	  
	  ok = feed_orders(orders, orders_len);
//	  ok = ok && assert_exec_count(execs_len);
//	  ok = ok && assert_execs(execs, execs_len);
//	  _engine.Destroy();
	  
	  //if (!ok) printf("test %i failed.\n\n", totaltests);
	  return ok;
	}		
	
	int test_cancel(Order[] orders1, int orders_len1, int[] cancels, int cancel_len, 
			Order[] orders2, int orders_len2) {
		int ok = 1;
		set_globals();
		
		ok = feed_orders(orders1, orders_len1); 
		feed_cancels(cancels, cancel_len);
		ok = feed_orders(orders2, orders_len2); 
		
		return ok;
	}
}
