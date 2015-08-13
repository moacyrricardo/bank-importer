package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillItem;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillSummary;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NubankBill implements Serializable {
	private static final long serialVersionUID = -2729550603168589542L;

	private String id;
	
	private List<NubankBillItem> line_items;

	private String state;
	
	private NubankBillSummary summary;
	
	private String bar_code;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<NubankBillItem> getLine_items() {
		return line_items;
	}

	public void setLine_items(List<NubankBillItem> line_items) {
		this.line_items = line_items;
	}

	public NubankBillSummary getSummary() {
		return summary;
	}

	public void setSummary(NubankBillSummary summary) {
		this.summary = summary;
	}

	public String getBar_code() {
		return bar_code;
	}

	public void setBar_code(String bar_code) {
		this.bar_code = bar_code;
	}
}
