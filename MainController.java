package Transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    TransactionsRepository transactionRepository;

    @GetMapping(path="/createAccount")
    public @ResponseBody String create(@RequestParam String amount){
        try{
            int acct = 1000000000+ (int)(Math.random() * 300000000);
            String acctnr= Integer.toString(acct);
            return createAccount(acctnr,Double.parseDouble(amount));
        }catch (Exception ex){
            return ex.getCause().getLocalizedMessage();
        }
    }

    @GetMapping (path="/deleteAccounts")
    public @ResponseBody String deleteAccounts(){
        accountsRepository.deleteAll();
        return "All Deleted";
    }

    @GetMapping(path="/transfer")
    public @ResponseBody String transfer(@RequestParam String sender, @RequestParam String receiver, @RequestParam double amount){
        try{

            sender.compareTo(receiver);
            String result =  send(sender, receiver, amount);
            send(sender, "Bank", 20);
            return result;
        }catch(Exception e){
            return e.getCause().getLocalizedMessage();
        }
    }

    @GetMapping(path ="/account")
    public @ResponseBody String getAcccount(@RequestParam String acctnr){
        Optional<Accounts> acc = accountsRepository.findById(acctnr);
        if(acc.isPresent())return acc.get().toString();
        else return "Account does not exists";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @GetMapping(path="/allaccounts")
    public @ResponseBody Iterable<Accounts> getAllAccount(){
        return accountsRepository.findAll();
    }

    @GetMapping(path="/alltransactions")
    public @ResponseBody Iterable<Transactions> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public String createAccount(String acctNr, double amount){
        if(!minAllowed(amount))return "Minimum amount to open account is 2000.0";
        try {
            Accounts acc = new Accounts();
            //bal.setId(0);
            acc.setBalance(amount);
            acc.setAccountnr(acctNr);
            acc.setLocked(0);
            accountsRepository.save(acc);
            return "Account Successfully Created!!!\n\nAccount Number :" + acctNr + " Balance : " + amount;
        }catch(Exception e){
            return e.getCause().getLocalizedMessage();
        }
    }

    public String createUser(String name, String email){
        try {
            User hello = new User();
            hello.setEmail("M");
            hello.setName("name");
            hello.setId(3);
            userRepository.save(hello);
            return "Successful";
        }catch(Exception ex){
            return ex.getCause().getLocalizedMessage();
        }
    }


    private boolean minAllowed(double amount){
        return amount>=2000;
    }

    private boolean hasMinimum(String acct, double amount){
        Optional<Accounts> a = accountsRepository.findById(acct);
        if(a.isPresent()) {
            return a.get().getBalance()>=amount+2020;
        }return false;
    }

    public void bankCharge(String from, int trans_id){
        recordTransaction(from, trans_id, "Bank", 20, 5);
        recordTransaction("Bank", trans_id, from, 20, 1);
    }

    public String send(String from, String to, double amount){
        String result = "";
        try {
            if (isExist(from)) {
                //if(checkForIdentity(from, to, amount))return "You are trying to execute one transaction twice";
                if (from.equals(to)) return "Error: You can not transfer to yourself";
                result +=1;
                if (!minAllowed(amount) || to.equals("Bank"))
                    return "Error: You can not transfer less than 2000 in a transaction";
                result+=2;
                if (!hasMinimum(from, amount) || to.equals("Bank"))
                    return "You have no sufficient money to transfer " + amount;
                result+=3;
                if (isExist(to)) {
                    result+=4;
                    if (!isLock(from)) {
                        result+=5;
                        lockAccount(from, 1);
                        result+=6;
                        int trans_id = setTransId();
                        int level = recordTransaction(from, trans_id, to, amount, 3);
                        result+=7;
                        if (level == 2) {
                            int level2 = recordTransaction(to, trans_id, from, amount, 1);
                            result+=8;
                            if (level2 == 2) {
                                completeTransaction(trans_id);
                                result+=9;
                                bankCharge(from, trans_id);
                                lockAccount(from, 0);
                                result+=10;
                            } else if (level2 == 1) {
                                rollBack1(trans_id);
                                result+=11;
                                rollBack2(1, from, amount);
                                result+=12;
                                lockAccount(from, 0);
                                result+=13;
                                return result+="Transaction roll back due to poor network";
                            }
                        } else if (level == 1) {
                            rollBack1(trans_id);
                            result+=14;
                            lockAccount(from, 0);
                            return result+= "Transaction roll back due to poor network";
                        }
                        return result + from + " has successfully transfer " + amount + " to " + to;
                    } else return result+= "You can not create two simultaneous transfers";
                } else return result+="You are trying to do transfer to an invalid account";
            } else return result+="You are trying to send money with an invalid account";
        }catch(Exception e){
            return result + e.getCause().getLocalizedMessage();
        }
    }

    private void completeTransaction(int trans_id){
        try {
            Iterable<Transactions> all = transactionRepository.findAll();
            for (Transactions transaction : all) {
                if (transaction.getTransact_id() == trans_id) {
                    transaction.setCompleted(1);
                    transactionRepository.save(transaction);
                }
            }
        }catch(Exception e){}
    }

    private void rollBack1(int trans_id){
        try {
            Iterable<Transactions> all = transactionRepository.findAll();

            for (Transactions transaction : all) {
                if (transaction.getTransact_id() == trans_id) {
                    transactionRepository.delete(transaction);
                }
            }
        }catch(Exception e){}

    }

    private boolean checkForIdentity(String from, String to, double amount){
        try {
            Iterable<Transactions> all = transactionRepository.findAll();
            for (Transactions transaction : all) {
                if (transaction.getTrans_owner().equals(from)) {
                    if (transaction.getTrans_party().equals(to)) {
                        if (transaction.getTrans_amount() == amount) {
                            if (transaction.getCompleted() == 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }catch(Exception e){}
        return false;
    }

    private void rollBack2(int type, String from, double amount){
        try {
            updateAccount(1, from, amount);
        }catch(Exception e){}
    }
    private int setTransId(){
        return (int)(Math.random() * 1000000000);
    }

    private boolean isLock(String account) {
        Iterable<Accounts> all = accountsRepository.findAll();
        for (Accounts acc : all){
            if(acc.getAccountnr().equals(account)){
                return acc.getLocked()==1;
            }
        }
        return true;
    }

    private boolean isExist(String from) {
        Iterable<Accounts> all = accountsRepository.findAll();
        for (Accounts acc : all) {
            if (acc.getAccountnr().equals(from)) return true;
        }
        return false;
    }

    private void lockAccount(String from, int value){
        Iterable<Accounts> all = accountsRepository.findAll();
        for (Accounts acc : all) {
            if (acc.getAccountnr().equals(from)){
                acc.setLocked(value);
                accountsRepository.save(acc);
                return;
            }
        }
    }

    private int recordTransaction(String acct,int trans_id ,String with, double amt, int type ){
        int i=0;
        try {
            Transactions t = new Transactions();
            t.setTransact_id(trans_id);
            t.setTrans_party(with);
            t.setTrans_owner(acct);
            t.setTrans_type_id(type);
            t.setTrans_amount(amt);
            t.setCompleted(0);
            transactionRepository.save(t);
            i++;
            updateAccount(type, acct, amt);
            i++;
            return i;
        }catch (Exception ex){
            return i;
        }
    }

    private void updateAccount(int type, String acct, double amount){
        Accounts accounts =null;
        Optional<Accounts> acc = accountsRepository.findById(acct);
        if(acc.isPresent())accounts= acc.get();
        if(accounts!=null) {
            amount = type == 1 ? amount : -amount;
            accounts.setBalance(accounts.getBalance() + amount);
            accountsRepository.save(accounts);
        }
    }

}