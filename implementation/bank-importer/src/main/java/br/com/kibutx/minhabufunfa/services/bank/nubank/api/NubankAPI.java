package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientResponse;


@Path("/api")
public interface NubankAPI {

	@POST
	@Path("/registration")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<RegistrationResp> register(RegistrationReq req);
	
	@POST
	@Path("/token")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<TokenResp> token(TokenReq req);
	
	@GET
	@Path("/customers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<CustomersResp> customers(@HeaderParam("Authorization") String authParam);
	
	@GET
	@Path("/{id}/accounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<AccountsResp> accounts(@HeaderParam("Authorization") String authParam, @PathParam("id") String costumerId);

	@GET
	@Path("/accounts/{id}/transactions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<AccountTransactionsResp> transactions(@HeaderParam("Authorization") String authParam, @PathParam("id") String accountId);
	
	/**
	 * 
	 * @param authParam
	 * @param accountId
	 * @param since ex.: 2014-12-30T19:36:47.17Z
	 * @param version ex.: v1.3-orig_merc_name
	 * @return
	 */
	@GET
	@Path("/accounts/{id}/transactions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<AccountTransactionsResp> transactions(@HeaderParam("Authorization") String authParam, @PathParam("id") String accountId, @QueryParam("since") String since, @QueryParam("transactions-version") String version);

	@GET
	@Path("/accounts/{id}/bills")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ClientResponse<AccountBillsResp> bills(@HeaderParam("Authorization") String authBearer, @PathParam("id") String accountId);
	
}
