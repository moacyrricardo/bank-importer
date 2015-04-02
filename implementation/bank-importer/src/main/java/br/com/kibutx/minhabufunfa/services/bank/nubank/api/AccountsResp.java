package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.List;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankAccount;

public class AccountsResp implements Serializable {

	private static final long serialVersionUID = 7757844597613002659L;
	
	List<NubankAccount> accounts;

	public List<NubankAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<NubankAccount> accounts) {
		this.accounts = accounts;
	}

}
