package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankExchange implements Serializable {
	private Integer amount_origin;
	private Integer amount_usd;
	@JsonProperty("exchange_rate")
	private BigDecimal exchangeRate;

	public Integer getAmount_origin() {
		return amount_origin;
	}

	public void setAmount_origin(Integer amount_origin) {
		this.amount_origin = amount_origin;
	}

	public Integer getAmount_usd() {
		return amount_usd;
	}

	public void setAmount_usd(Integer amount_usd) {
		this.amount_usd = amount_usd;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

}
