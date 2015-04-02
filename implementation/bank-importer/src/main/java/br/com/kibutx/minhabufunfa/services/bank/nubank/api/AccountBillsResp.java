package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.List;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBill;

public class AccountBillsResp implements Serializable {

	private static final long serialVersionUID = -8564210258724620335L;
	
	List<NubankBill> bills;

	public List<NubankBill> getBills() {
		return bills;
	}

	public void setBills(List<NubankBill> bills) {
		this.bills = bills;
	}
	
	
}
