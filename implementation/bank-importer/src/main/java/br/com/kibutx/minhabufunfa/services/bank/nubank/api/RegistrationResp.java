package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

public class RegistrationResp implements Serializable {
	public static final int SUCCESS_CODE=201;
			
	private static final long serialVersionUID = 6124314541115323524L;
	private String client_secret;
	private String client_id;
	private String name;
	private String url;

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
