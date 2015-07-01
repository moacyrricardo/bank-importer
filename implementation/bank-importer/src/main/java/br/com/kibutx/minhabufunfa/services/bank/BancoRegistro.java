package br.com.kibutx.minhabufunfa.services.bank;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BancoRegistro implements Serializable {
	private static final long serialVersionUID = 6922072443981360581L;

	private Date data;

	private BigDecimal valor;

	private String descricao;

	private boolean entrada = false;
	
	private BigDecimal exchangeRate = new BigDecimal("1.0");
	
	private String currency = "BRL";

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isEntrada() {
		return entrada;
	}

	public void setEntrada(boolean entrada) {
		this.entrada = entrada;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
