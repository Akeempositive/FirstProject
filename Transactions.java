package Transfer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Transactions {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private Integer transact_id;
    private Integer trans_type_id;
    private String trans_owner;
    private String trans_party;
    private Double trans_amount;
    private Integer completed;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransact_id() {
        return transact_id;
    }

    public void setTransact_id(Integer transact_id) {
        this.transact_id = transact_id;
    }

    public Integer getTrans_type_id() {
        return trans_type_id;
    }

    public void setTrans_type_id(Integer trans_type_id) {
        this.trans_type_id = trans_type_id;
    }

    public String getTrans_owner() {
        return trans_owner;
    }

    public void setTrans_owner(String trans_owner) {
        this.trans_owner = trans_owner;
    }

    public String getTrans_party() {
        return trans_party;
    }

    public void setTrans_party(String trans_party) {
        this.trans_party = trans_party;
    }

    public Double getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(Double trans_amount) {
        this.trans_amount = trans_amount;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }
}