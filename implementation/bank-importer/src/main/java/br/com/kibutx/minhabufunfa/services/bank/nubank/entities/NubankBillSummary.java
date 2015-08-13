package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mozilla.javascript.annotations.JSFunction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankBillSummary implements Serializable {

	private static final long serialVersionUID = -160103420699903748L;
	
	private String id;
	private String closed_date;
	private String open_date;
	private Integer paid;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClosed_date() {
		return closed_date;
	}

	public void setClosed_date(String closed_date) {
		this.closed_date = closed_date;
	}

	public String getOpen_date() {
		return open_date;
	}

	public void setOpen_date(String open_date) {
		this.open_date = open_date;
	}

	public Integer getPaid() {
		return paid;
	}

	public void setPaid(Integer paid) {
		this.paid = paid;
	}
}
