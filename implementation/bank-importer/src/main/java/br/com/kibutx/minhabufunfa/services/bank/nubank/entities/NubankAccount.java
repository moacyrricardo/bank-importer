package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankAccount implements Serializable {
	private static final long serialVersionUID = 7613876208148180495L;

	private String id;

	private String customer_id;

	private String next_due_date;

	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getNext_due_date() {
		return next_due_date;
	}

	public void setNext_due_date(String next_due_date) {
		this.next_due_date = next_due_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
