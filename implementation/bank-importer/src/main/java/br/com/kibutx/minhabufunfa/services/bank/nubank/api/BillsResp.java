package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBill;

public class BillsResp implements Serializable {
	NubankBill bill;

	public NubankBill getBill() {
		return bill;
	}

	public void setBill(NubankBill bill) {
		this.bill = bill;
	}
}
