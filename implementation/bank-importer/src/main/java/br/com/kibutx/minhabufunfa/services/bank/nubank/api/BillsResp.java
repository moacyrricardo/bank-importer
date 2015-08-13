package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillSummary;

public class BillsResp implements Serializable {
	private static final long serialVersionUID = -6918107627315292780L;
	private NubankBill bill;

	public NubankBill getBill() {
		return bill;
	}

	public void setBill(NubankBill bill) {
		this.bill = bill;
	}
}
