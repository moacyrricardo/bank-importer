package br.com.kibutx.minhabufunfa.services.bank.nubank
;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.AccountBillsResp;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.NubankAPI;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.NubankBill;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.RegistrationReq;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.RegistrationResp;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.TokenReq;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.TokenResp;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankAccount;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillItem;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillItemType;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankCharge;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankCharge.Extra;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankCustomer;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankTransaction;
import br.com.quintoandar.consultasbr.core.SimpleHttpQuerier;

public class NubankImportador extends SimpleHttpQuerier implements BancoImportador {

	private NubankAPI authApi;
	private NubankAPI costumersApi;
	private NubankAPI accountsApi;
	private RegistrationResp regResp;
	private TokenResp token;
	private NubankCustomer costumer;
	private String authBearer;

	public NubankImportador(){
		ClientConnectionManager cm = new ThreadSafeClientConnManager();
		DefaultHttpClient httpClient = new DefaultHttpClient(cm);

		ClientExecutor executor = new ApacheHttpClient4Executor(httpClient);

		authApi = ProxyFactory.create(NubankAPI.class, "https://prod-auth.nubank.com.br", executor);
		costumersApi = ProxyFactory.create(NubankAPI.class, "https://prod-customers.nubank.com.br", executor);
		accountsApi = ProxyFactory.create(NubankAPI.class, "https://prod-accounts.nubank.com.br", executor);

		ClientResponse<RegistrationResp> resp = authApi.register(RegistrationReq.asNubank());
		if(resp.getStatus() != RegistrationResp.SUCCESS_CODE){
			throw new RuntimeException("Erro registrando api. Status code = "+resp.getStatus());
//			System.out.println(resp.getEntity().getClient_id());
//			System.out.println(resp.getEntity().getClient_secret());
		}
		this.regResp = resp.getEntity();
//		api.token(TokenReq.create(resp.getEntity()));
	}

	public boolean login(String username, String password) {
		ClientResponse<TokenResp> resp = authApi.token(new TokenReq(regResp,username,password));
		if(resp.getStatus() == 403){
			return false;
		}
		if(resp.getStatus() != TokenResp.SUCCESS_CODE){
			throw new RuntimeException("Erro fazendo login. Status code = "+resp.getStatus());
		}
		this.token = resp.getEntity();
		this.authBearer = token.getToken_type().substring(0, 1).toUpperCase()+token.getToken_type().substring(1).toLowerCase()+" "+token.getAccess_token();
		this.costumer = null;
		return true;
	}

	public List<BancoRegistro> carregarLancamentosExtrato() {
		Set<BancoRegistro> regs = new TreeSet<BancoRegistro>(new Comparator<BancoRegistro>() {
			public int compare(BancoRegistro o1, BancoRegistro o2) {
				int ret = 0;
				if(o1.getData() != null && o2.getData() != null){
					ret = o1.getData().compareTo(o2.getData());
				}
				if(ret == 0){
					ret = o1.getDescricao().compareTo(o2.getDescricao());
				}
				if(ret == 0){
					ret = o1.getValor().compareTo(o2.getValor());
				}
				return ret;
			}
		});
		loadCostumer();
		for(NubankAccount acc:accountsApi.accounts(authBearer, costumer.getId()).getEntity().getAccounts()){
			for(NubankTransaction trans : accountsApi.transactions(authBearer, acc.getId()).getEntity().getTransactions()){
//				if("BRA".equals(trans.getCountry())){
//					BancoRegistro br = new BancoRegistro();
//					br.setData(trans.getTimeAsDate());
//					br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name());
//					br.setValor(new BigDecimal(""+(-1.0*trans.getAmount().doubleValue()/100.0)));
//					regs.add(br);
//				} else {
					int qtdParcs = trans.getCharges_list().size();
					for(int idx=1;idx<=qtdParcs;idx++){
						NubankCharge cg = trans.getCharges_list().get(idx-1);
						BancoRegistro br = new BancoRegistro();
						br.setData(cg.getPostDate());
						if(qtdParcs > 1){
							br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name()+" ["+idx+"/"+qtdParcs+"]");
						} else {
							br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name());
						}
						br.setValor(new BigDecimal(""+(-1.0*cg.getAmount().doubleValue()/100.0)));
						regs.add(br);
						
						if(!"BRA".equals(trans.getCountry())){
							br.setExchangeRate(trans.getFx().getExchangeRate());
							br.setCurrency("USD");
							for(Extra xt : cg.getExtras()){
								BancoRegistro ebr = new BancoRegistro();
								ebr.setData(trans.getTimeAsDate());
								ebr.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name()+" - "+xt.getName());
								ebr.setValor(new BigDecimal(""+(-1.0*xt.getAmount().doubleValue()/100.0)));
								regs.add(ebr);
							}
						}
					}
//				}
			}
			
//			 System.out.println(accountsApi.billsSummary(authBearer, acc.getId(),"identity").getEntity());
//			if("ao".equals("bla"))
			List<NubankBill> billsSumm = accountsApi.billsSummary(authBearer, acc.getId()).getEntity().getBills();
			for(NubankBill bSumm : billsSumm){
				if(bSumm.getId() == null && !"open".equals(bSumm.getState()))
					continue;
				NubankBill bill = null;
				if(bSumm.getId() == null){
					bill = accountsApi.billByState(authBearer,acc.getId(),bSumm.getState()).getEntity().getBill() ;
				} else {
					bill = accountsApi.bill(authBearer,bSumm.getId()).getEntity().getBill() ;
				}
				for(NubankBillItem it : bill.getLine_items()){
						BancoRegistro br = new BancoRegistro();
						br.setData(it.getPostDate());//ja tem: veio nos transactions
						if(it.getHref() == null){
//							NubankTransaction trans = it.getTransaction();
//							br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name());
//							br.setValor(new BigDecimal(""+(trans.getAmount().doubleValue()/100.0)));
//						} else {
							br.setDescricao(it.getType()+" - "+it.getTitle());
							br.setValor(new BigDecimal(""+(it.getAmount().doubleValue()/100.0)));
							if(it.getType() == NubankBillItemType.payment || it.getType() == NubankBillItemType.tax || it.getType() == NubankBillItemType.interest){
								br.setValor(br.getValor().negate());
								regs.add(br);
//							} else if(it.getType() == NubankBillItemType.tax || it.getType() == NubankBillItemType.interest) {
//								regs.add(br);
							}
					}
				}
			}
		}
		return new ArrayList<BancoRegistro>(regs);
	}

	private NubankCustomer loadCostumer() {
		if(costumer == null){
			costumer = costumersApi.customers(authBearer).getEntity().getCustomer();
		}
		return costumer;
	}

//	public static void main(String[] args) {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		NubankImportador ii = new NubankImportador();
//		ii.login("cpf","password");
//		for(BancoRegistro lanc: ii.carregarLancamentosExtrato()){
//			System.out.println(sdf.format(lanc.getData())+"\t"+lanc.getDescricao()+ "\t"+lanc.getValor());
//		}
//		
//	}
	public static void main(String[] args) {
		   ObjectMapper mapper = new ObjectMapper();
		try {
			String json = "{\"bills\":[{\"state\":\"future\",\"summary\":{\"due_date\":\"2015-11-25\",\"precise_minimum_payment\":\"37.7580\",\"close_date\":\"2015-11-12\",\"past_balance\":0,\"total_prior_bill\":\"0.0\",\"precise_total_balance\":\"251.72\",\"total_balance\":25172,\"interest\":0,\"total_cumulative\":25172,\"paid\":0,\"minimum_payment\":3776,\"open_date\":\"2015-10-09\"}},{\"state\":\"future\",\"summary\":{\"due_date\":\"2015-10-25\",\"precise_minimum_payment\":\"37.7580\",\"close_date\":\"2015-10-09\",\"past_balance\":0,\"total_prior_bill\":\"0.0\",\"precise_total_balance\":\"251.72\",\"total_balance\":25172,\"interest\":0,\"total_cumulative\":25172,\"paid\":0,\"minimum_payment\":3776,\"open_date\":\"2015-09-11\"}},{\"state\":\"future\",\"summary\":{\"due_date\":\"2015-09-25\",\"precise_minimum_payment\":\"101.670000\",\"close_date\":\"2015-09-11\",\"past_balance\":0,\"total_prior_bill\":\"0.0\",\"precise_total_balance\":\"677.8000\",\"total_balance\":67780,\"interest\":0,\"total_cumulative\":67780,\"paid\":0,\"minimum_payment\":10167,\"open_date\":\"2015-08-12\"}},{\"state\":\"open\",\"summary\":{\"due_date\":\"2015-08-25\",\"precise_minimum_payment\":\"661.190070\",\"close_date\":\"2015-08-12\",\"past_balance\":0,\"total_prior_bill\":\"2900.5738\",\"precise_total_balance\":\"4407.9338\",\"total_balance\":440793,\"interest\":0,\"total_cumulative\":440793,\"paid\":0,\"minimum_payment\":66119,\"open_date\":\"2015-07-10\"}},{\"state\":\"overdue\",\"id\":\"55a3f366-6ca1-4e8b-bd15-b6315dbd4690\",\"summary\":{\"due_date\":\"2015-07-25\",\"precise_minimum_payment\":\"435.0900\",\"close_date\":\"2015-07-10\",\"past_balance\":0,\"precise_total_balance\":\"2900.5738\",\"total_balance\":290057,\"interest\":149,\"total_cumulative\":289909,\"paid\":290057,\"minimum_payment\":43509,\"open_date\":\"2015-06-12\"},\"href\":\"nuapp://bill/55a3f366-6ca1-4e8b-bd15-b6315dbd4690\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/55a3f366-6ca1-4e8b-bd15-b6315dbd4690\"}}},{\"state\":\"overdue\",\"id\":\"557c3641-2e63-414d-903c-87794e452783\",\"summary\":{\"due_date\":\"2015-06-25\",\"precise_minimum_payment\":\"86.5600\",\"close_date\":\"2015-06-12\",\"past_balance\":0,\"precise_total_balance\":\"577.0700\",\"total_balance\":57707,\"interest\":0,\"total_cumulative\":57707,\"paid\":57707,\"minimum_payment\":8656,\"open_date\":\"2015-05-12\"},\"href\":\"nuapp://bill/557c3641-2e63-414d-903c-87794e452783\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/557c3641-2e63-414d-903c-87794e452783\"}}},{\"state\":\"overdue\",\"id\":\"55535cb5-d431-49c9-9751-f01fc75e049c\",\"summary\":{\"due_date\":\"2015-05-25\",\"precise_minimum_payment\":\"67.4900\",\"close_date\":\"2015-05-12\",\"past_balance\":-74527,\"precise_total_balance\":\"449.9000\",\"total_balance\":44990,\"interest\":0,\"total_cumulative\":119517,\"paid\":44990,\"minimum_payment\":6749,\"open_date\":\"2015-04-10\"},\"href\":\"nuapp://bill/55535cb5-d431-49c9-9751-f01fc75e049c\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/55535cb5-d431-49c9-9751-f01fc75e049c\"}}},{\"state\":\"overdue\",\"id\":\"552973c3-15d8-4616-a711-dbc842861aff\",\"summary\":{\"due_date\":\"2015-04-25\",\"precise_minimum_payment\":\"0.0400\",\"close_date\":\"2015-04-10\",\"past_balance\":0,\"precise_total_balance\":\"0.2500\",\"total_balance\":25,\"interest\":10,\"total_cumulative\":15,\"paid\":74552,\"minimum_payment\":4,\"open_date\":\"2015-03-12\"},\"href\":\"nuapp://bill/552973c3-15d8-4616-a711-dbc842861aff\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/552973c3-15d8-4616-a711-dbc842861aff\"}}},{\"state\":\"overdue\",\"id\":\"550301fc-dccb-4210-b127-2a18a25de2da\",\"summary\":{\"due_date\":\"2015-03-25\",\"precise_minimum_payment\":\"5.9700\",\"close_date\":\"2015-03-12\",\"past_balance\":0,\"precise_total_balance\":\"39.7800\",\"total_balance\":3978,\"interest\":0,\"total_cumulative\":3978,\"paid\":3978,\"minimum_payment\":597,\"open_date\":\"2015-02-10\"},\"href\":\"nuapp://bill/550301fc-dccb-4210-b127-2a18a25de2da\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/550301fc-dccb-4210-b127-2a18a25de2da\"}}},{\"state\":\"overdue\",\"id\":\"54db593f-f6ad-447c-815e-f3f840a57af2\",\"summary\":{\"due_date\":\"2015-02-25\",\"precise_minimum_payment\":\"130.8500\",\"close_date\":\"2015-02-10\",\"past_balance\":0,\"precise_total_balance\":\"872.3100\",\"total_balance\":87231,\"interest\":0,\"total_cumulative\":87231,\"paid\":87231,\"minimum_payment\":13085,\"open_date\":\"2015-01-12\"},\"href\":\"nuapp://bill/54db593f-f6ad-447c-815e-f3f840a57af2\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/54db593f-f6ad-447c-815e-f3f840a57af2\"}}},{\"state\":\"overdue\",\"id\":\"54b504b9-d8b4-47a4-81b4-3ebdc41728bd\",\"summary\":{\"due_date\":\"2015-01-25\",\"precise_minimum_payment\":\"25.4000\",\"close_date\":\"2015-01-12\",\"past_balance\":0,\"precise_total_balance\":\"169.3600\",\"total_balance\":16936,\"interest\":0,\"total_cumulative\":16936,\"paid\":16936,\"minimum_payment\":2540,\"open_date\":\"2014-12-15\"},\"href\":\"nuapp://bill/54b504b9-d8b4-47a4-81b4-3ebdc41728bd\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/54b504b9-d8b4-47a4-81b4-3ebdc41728bd\"}}},{\"state\":\"overdue\",\"id\":\"54902e89-449c-4e65-93e7-a3db3cc3ebfe\",\"summary\":{\"due_date\":\"2014-12-25\",\"precise_minimum_payment\":\"83.0800\",\"close_date\":\"2014-12-15\",\"past_balance\":0,\"precise_total_balance\":\"553.8400\",\"total_balance\":55384,\"interest\":84,\"total_cumulative\":55300,\"paid\":55384,\"minimum_payment\":8308,\"open_date\":\"2014-11-14\"},\"href\":\"nuapp://bill/54902e89-449c-4e65-93e7-a3db3cc3ebfe\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/54902e89-449c-4e65-93e7-a3db3cc3ebfe\"}}},{\"state\":\"overdue\",\"id\":\"54673e65-9ada-4820-8d79-b304f2203bb9\",\"summary\":{\"due_date\":\"2014-11-25\",\"precise_minimum_payment\":\"48.9300\",\"close_date\":\"2014-11-14\",\"past_balance\":0,\"precise_total_balance\":\"326.1800\",\"total_balance\":32618,\"interest\":0,\"total_cumulative\":32618,\"paid\":32618,\"minimum_payment\":4893,\"open_date\":\"2014-10-13\"},\"href\":\"nuapp://bill/54673e65-9ada-4820-8d79-b304f2203bb9\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/54673e65-9ada-4820-8d79-b304f2203bb9\"}}},{\"state\":\"overdue\",\"id\":\"543d15f4-9812-4ec4-9f57-5c347db6f54a\",\"summary\":{\"due_date\":\"2014-10-25\",\"precise_minimum_payment\":\"44.9500\",\"close_date\":\"2014-10-13\",\"past_balance\":0,\"precise_total_balance\":\"299.6900\",\"total_balance\":29969,\"interest\":0,\"total_cumulative\":29969,\"paid\":29969,\"minimum_payment\":4495,\"open_date\":\"2014-09-22\"},\"href\":\"nuapp://bill/543d15f4-9812-4ec4-9f57-5c347db6f54a\",\"_links\":{\"self\":{\"href\":\"https://prod-accounts.nubank.com.br/api/bills/543d15f4-9812-4ec4-9f57-5c347db6f54a\"}}}],\"_links\":{\"open\":{\"href\":\"https://prod-accounts.nubank.com.br/api/accounts/5420d0bb-671e-4f38-a554-f86299ae9e3f/bills/open\"},\"future\":{\"href\":\"https://prod-accounts.nubank.com.br/api/accounts/5420d0bb-671e-4f38-a554-f86299ae9e3f/bills/future\"}}}";
			AccountBillsResp userFromJSON = mapper.readValue(json, AccountBillsResp.class);
		        System.out.println(userFromJSON);
		    } catch (JsonGenerationException e) {
		        System.out.println(e);
		        } catch (JsonMappingException e) {
		       System.out.println(e);
		    } catch (IOException e) {
		    System.out.println(e);
		    } 
		}
}
