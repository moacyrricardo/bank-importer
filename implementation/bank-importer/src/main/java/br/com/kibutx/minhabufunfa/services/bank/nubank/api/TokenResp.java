package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;

public class TokenResp implements Serializable {

	private static final long serialVersionUID = -6535694552275626433L;

	public static final int SUCCESS_CODE = 200;

	private String access_token;
	private String token_type;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

}
