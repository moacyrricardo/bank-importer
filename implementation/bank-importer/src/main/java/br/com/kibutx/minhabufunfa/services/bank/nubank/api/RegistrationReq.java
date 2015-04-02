package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

public class RegistrationReq implements Serializable {
	private static final long serialVersionUID = 8156523101616498000L;
	
	private String name;
	
	private String uri;
	
	public RegistrationReq(String name, String uri) {
		super();
		this.name = name;
		this.uri = uri;
	}
	
	public static RegistrationReq asNubank(){
		return new RegistrationReq("Nubank", "https://www.nubank.com.br");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
