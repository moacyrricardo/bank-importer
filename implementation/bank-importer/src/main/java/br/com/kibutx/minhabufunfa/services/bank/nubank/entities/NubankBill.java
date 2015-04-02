package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankBill implements Serializable {

	private static final long serialVersionUID = -160103420699903748L;
	
	private String id;
	private String status;
	private Integer paid;
	private String bar_code;
	
	List<Map<String, NubankBillItem>> line_items;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPaid() {
		return paid;
	}

	public void setPaid(Integer paid) {
		this.paid = paid;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}

	public List<Map<String, NubankBillItem>> getLine_items() {
		return line_items;
	}

	public void setLine_items(List<Map<String, NubankBillItem>> line_items) {
		this.line_items = line_items;
	}
}
