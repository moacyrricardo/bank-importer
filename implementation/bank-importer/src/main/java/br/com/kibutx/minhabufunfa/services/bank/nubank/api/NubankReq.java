package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

public abstract class NubankReq implements Serializable {
	private static final long serialVersionUID = 3896265166680750087L;
	private String client_secret;
	private String client_id;
	private String nonce = "NOT-RANDOM-YET";

	public NubankReq(RegistrationResp resp) {
		this.setClient_id(resp.getClient_id());
		this.setClient_secret(resp.getClient_secret());
	}

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

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

}
