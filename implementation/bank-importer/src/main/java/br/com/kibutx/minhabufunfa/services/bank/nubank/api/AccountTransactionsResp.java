package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankTransaction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountTransactionsResp implements Serializable {
	private static final long serialVersionUID = 7524505152531482764L;
	List<NubankTransaction> transactions;

	public List<NubankTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<NubankTransaction> transactions) {
		this.transactions = transactions;
	}
	
	
}
