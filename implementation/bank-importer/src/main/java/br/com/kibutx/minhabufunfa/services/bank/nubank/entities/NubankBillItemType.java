package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

public enum NubankBillItemType {
	tax, interest,
	/** pagamento de fatura
	 * 
	 */
	payment,
	/**
	 * Item de fatura
	 */
	charge,
	other;
}
