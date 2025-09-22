package kardex.PEPS.InventoryPEPS.domain.model;

import java.util.LinkedList;
import java.util.Queue;

public class QueueBalance {
    private Queue<Balance> balances= new LinkedList<>();
    
    
    public void addBalance(Balance balance){
        balances.offer(balance);
    }

    public void takeOutBalance(){
        Balance balance = balances.poll();
        if (balance!= null) {
            System.out.println("product" +balance.getAmount());
        } else {
            System.out.println("there are not products");
        }
    }
}
