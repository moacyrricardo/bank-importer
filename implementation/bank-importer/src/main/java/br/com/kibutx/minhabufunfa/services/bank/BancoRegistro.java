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
}
