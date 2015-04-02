package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

public class TokenReq extends NubankReq {
	private static final long serialVersionUID = 6864313882621481125L;
	private String username;
	private String password;
	private String grant_type;
	
	public TokenReq(RegistrationResp resp, String username, String password) {
		super(resp);
		this.grant_type = "password";
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

}
