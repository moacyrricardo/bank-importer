package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

public class NubankLink implements Serializable {
	private static final long serialVersionUID = -7228168267414416307L;

	String link;
	
	String type;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
