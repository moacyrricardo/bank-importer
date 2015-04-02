package br.com.kibutx.minhabufunfa.services.bank.nubank
;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.NubankAPI;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.RegistrationReq;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.RegistrationResp;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.TokenReq;
import br.com.kibutx.minhabufunfa.services.bank.nubank.api.TokenResp;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankAccount;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBill;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillItem;
import br.com.kibutx.minhabufunfa.services.bank.nubank.entities.NubankBillItemType;
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
				if(o1.getData() != null && o2.getData() != null){
					return o1.getData().compareTo(o2.getData());
				}
				return 0;
			}
		});
		loadCostumer();
		for(NubankAccount acc:accountsApi.accounts(authBearer, costumer.getId()).getEntity().getAccounts()){
			for(NubankTransaction trans : accountsApi.transactions(authBearer, acc.getId()).getEntity().getTransactions()){
				BancoRegistro br = new BancoRegistro();
				br.setData(trans.getTimeAsDate());
				br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name());
				br.setValor(new BigDecimal(""+(-1.0*trans.getAmount().doubleValue()/100.0)));
				regs.add(br);
			}
			for(NubankBill bill : accountsApi.bills(authBearer, acc.getId()).getEntity().getBills()){
				for(Map<String,NubankBillItem> map : bill.getLine_items()){
					for(String ent:map.keySet()){
						NubankBillItem it = map.get(ent);
						BancoRegistro br = new BancoRegistro();
						br.setData(it.getPostDate());
						if(it.getTransaction() == null){
//							NubankTransaction trans = it.getTransaction();
//							br.setDescricao(trans.getMcc()+" - "+trans.getOriginal_merchant_name());
//							br.setValor(new BigDecimal(""+(trans.getAmount().doubleValue()/100.0)));
//						} else {
							br.setDescricao(it.getType()+" - "+it.getDescription());
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
		}
		return new ArrayList<BancoRegistro>(regs);
	}

	private NubankCustomer loadCostumer() {
		if(costumer == null){
			costumer = costumersApi.customers(authBearer).getEntity().getCustomer();
		}
		return costumer;
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		NubankImportador ii = new NubankImportador();
		ii.login("cpf","password");
		for(BancoRegistro lanc: ii.carregarLancamentosExtrato()){
			System.out.println(sdf.format(lanc.getData())+"\t"+lanc.getDescricao()+ "\t"+lanc.getValor());
		}
	}
}
