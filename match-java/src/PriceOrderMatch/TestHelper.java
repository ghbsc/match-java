package PriceOrderMatch;

public class TestHelper {
	Engine _engine;
	
	Order oa101x100 = new Order("JPM", "MAX", 1, (short)101, 100);	
	Order ob101x25x = new Order("JPM", "XAM", 0, (short)101, 25);	
	
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
		Order[] orders = {oa101x100};
		test(orders, orders.length);
		
		Order[] queueThenTrade = {ob101x25x, ob101x25x};
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
	
}
