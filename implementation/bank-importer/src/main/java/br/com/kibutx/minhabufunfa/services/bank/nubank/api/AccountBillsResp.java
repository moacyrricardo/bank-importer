package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountBillsResp implements Serializable {

	private static final long serialVersionUID = -8564210258724620335L;
	
	List<NubankBillSummary> bills = new ArrayList<NubankBillSummary>();

	public List<NubankBillSummary> getBills() {
		return bills;
	}

	public void setBills(List<NubankBillSummary> bills) {
		this.bills = bills;
	}
	
	
}
