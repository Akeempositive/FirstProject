package Transfer;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Accounts {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Id
    private String accountnr;
    private int locked;
    private double balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountnr() {
        return accountnr;
    }

    public void setAccountnr(String accountnr) {
        this.accountnr = accountnr;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String toString(){
        return "{\n" +
                    " \"id\""+":\""+getId()+"\"\n"+
                    " \"accountnr\""+":\""+getAccountnr()+"\"\n"+
                    " \"balance\""+":\""+getBalance()+"\"\n"+
                    " \"locked\""+":\""+getLocked()+"\"\n"+
                "}";
    }
}
