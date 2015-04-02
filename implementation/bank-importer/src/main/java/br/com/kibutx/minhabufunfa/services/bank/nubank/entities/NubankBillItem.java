package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankBillItem implements Serializable {
	private static final SimpleDateFormat postDateFmt = new SimpleDateFormat("yyyy-MM-dd");
	private static final long serialVersionUID = -8522409039531320734L;
	private String id;
	private Integer amount;
	private Integer paid;
	private String post_date;
	private String description;
	private NubankBillItemType type;
	private NubankTransaction transaction;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getPaid() {
		return paid;
	}

	public void setPaid(Integer paid) {
		this.paid = paid;
	}

	public String getPost_date() {
		return post_date;
	}

	public Date getPostDate() {
		try {
			return postDateFmt.parse(getPost_date());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setPost_date(String post_date) {
		this.post_date = post_date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public NubankBillItemType getType() {
		if(type == null){
			if(amount + paid == 0){
				type = NubankBillItemType.payment;
			} else {
				type = NubankBillItemType.charge;
			}
		}
		return type;
	}

	public void setType(NubankBillItemType type) {
		this.type = type;
	}

	public NubankTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(NubankTransaction transaction) {
		this.transaction = transaction;
	}

}
