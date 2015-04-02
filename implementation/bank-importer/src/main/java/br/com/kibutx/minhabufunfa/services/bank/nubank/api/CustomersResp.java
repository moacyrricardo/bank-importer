package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankCustomer;

public class CustomersResp implements Serializable {
	private static final long serialVersionUID = -2389543304118601577L;
	private NubankCustomer customer;

	public NubankCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(NubankCustomer costumer) {
		this.customer = costumer;
	}

}
