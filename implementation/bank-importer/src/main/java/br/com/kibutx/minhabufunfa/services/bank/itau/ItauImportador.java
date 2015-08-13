package br.com.kibutx.minhabufunfa.services.bank.itau;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;
import br.com.quintoandar.consultasbr.core.SimpleHttpQuerier;

public class ItauImportador extends SimpleHttpQuerier implements BancoImportador {
	protected static final Logger log = LoggerFactory.getLogger(ItauImportador.class);
	protected static final String ENCODING = "UTF-8";

	protected static final String URL_UM = "https://ww70.itau.com.br/M/Institucional/IncentivoAplicativo.aspx";
	protected static final String URL_DOIS = "https://ww70.itau.com.br/M/LoginPF.aspx";
	protected static final String URL_MENU = "https://ww70.itau.com.br/M/Menu.aspx";

	protected static final Object ASPNET_SESSION_ID = "ASP.NET_SessionId";
	protected static final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

	protected String menuUrl = null;
	protected String extratoUrl;

	protected Set<String> descricoesIgnorar = new HashSet<String>(Arrays.asList("S A L D O","SALDO ANTERIOR","SDO CTA/APL AUTOMATICAS","(-) SALDO A LIBERAR","SALDO DO DIA","SALDO FINAL DEVEDOR"));

	@Override
	protected boolean shouldPersistCookie(BasicClientCookie ck) {
		if(ck.getName().equals(ASPNET_SESSION_ID)){
			return true;
		}
		return super.shouldPersistCookie(ck);
	}

	private String getUrlFormLogin(){
		HttpGet httpGet = new HttpGet(URL_UM);
		applyItauHeaders(httpGet);
		attachCookiesFromStore(httpGet);
		try {
			HttpResponse resp = client.execute(httpGet);
			if (resp.getStatusLine().getStatusCode() == 200) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				String html = new String(baos.toByteArray(), ENCODING);
				baos.close();
				baos = null;

				String param = html.replaceAll("(\n|\r)","").replaceAll(".*LoginPF\\.aspx?(.*?)\".*", "$1");
				return URL_DOIS+param;
			}
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			httpGet.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
		return null;
	}

	private void applyItauHeaders(HttpRequestBase hrb){
		hrb.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
		hrb.addHeader(HttpHeaders.ACCEPT, "text/html; charset="+ENCODING);
		hrb.addHeader(HttpHeaders.ACCEPT_CHARSET, ENCODING);
		hrb.addHeader(HttpHeaders.CONNECTION, "keep-alive");
		hrb.addHeader(HttpHeaders.HOST, "ww70.itau.com.br");
		hrb.addHeader("ORIGIN", "https://ww70.itau.com.br");

	}

	public void login (String agencia, String conta, String dvConta, String password){
		String urlFormLogin = getUrlFormLogin();
		Map<String,String> params = preparaParametrosLogin(urlFormLogin, agencia, conta, dvConta, password);
		String redirToMenu = fazerLogin(urlFormLogin,params);

		if(redirToMenu.contains(URL_MENU)){
			menuUrl = redirToMenu;
//			carregarOpcoesMenu();
		} else {
			throw new RuntimeException("Não foi possivel conectar ao ITAU: redir="+redirToMenu);
		}
	}

	public static String extrairUrl(String html, String comeco, String meio){
		return html.replaceAll("(\n|\r)","").replaceAll("^.*\"("+comeco+meio+"([^\"]+?))\".*$", "$1");
	}

	public static String extrairUrl(String html, String meio){
		return  extrairUrl(html, "http([^\"]+?)", meio);
	}

	protected void carregarOpcoesMenu() {
		String html = carregarHtml(menuUrl, 200);
		this.extratoUrl = html.replaceAll("(\n|\r)","").replaceAll("^.*\"(http([^\"]+?)M/SaldoExtratoLancamentos([^\"]+?))\".*$", "$1");
//		this.poupancaUrl = html.replaceAll("(\n|\r)","").replaceAll("^.*\"(http([^\"]+?)M/menuPoupanca([^\"]+?))\".*$", "$1");
	}

	public List<BancoRegistro> carregarLancamentosExtrato(){
		carregarOpcoesMenu();
		String html = carregarHtml(extratoUrl, 200);

		Document doc = carregarHtmlDeLink(html,"a[href^=SaldoExtratoLancamentos]","Últimos 30 dias");
//		Document doc = Jsoup.parse(html);
		Element tableExtrato = doc.getElementById("ctl00_ContentPlaceHolder1_FieldExtratoTouch");
		Iterator<Element> iterator = tableExtrato.select("div.rowPar, div.rowImpar").iterator();
		List<BancoRegistro> list = new ArrayList<BancoRegistro>();
		while(iterator.hasNext()){
			Element e = iterator.next();
			Elements children = e.select("td");
			String data = children.get(1).text();
			String desc = children.get(2).text().trim();
			String val = children.get(3).text();
			if(!descricoesIgnorar.contains(desc)){

				list.add(gerarRegistro(data,desc,val));
			}
		}
		return list;
	}
//
//	public List<BancoRegistro> carregarLancamentosExtratoPoupanca(){
//		carregarOpcoesMenu();
//		String html =  /*carregarHtml(poupancaUrl, 200);
//		html = */carregarHtml("https://ww70.itau.com.br/M/SaldoPoupanca.aspx", 200);
//		Document doc = carregarHtmlDeLink(html,"a[href^=SaldoPoupanca]","Últimos 30 dias");
//		Element tableExtrato = doc.getElementById("ctl00_ContentPlaceHolder1_Fieldset2");
//		Iterator<Element> iterator = tableExtrato.select("div.rowPar, div.rowImpar").iterator();
//		List<BancoRegistro> list = new ArrayList<BancoRegistro>();
//		while(iterator.hasNext()){
//			Element e = iterator.next();
//			Elements children = e.select("td");
//			String data = children.get(1).text();
//			String desc = children.get(2).text().trim();
//			String val = children.get(3).text();
//			if(!descricoesIgnorar.contains(desc)){
//
//				list.add(gerarRegistro(data,desc,val));
//			}
//		}
//		return list;
//	}

	/**
	 *
	 * @param html o html de onde será procurado o link
	 * @param linksSelector o seletor css dos possiveis links
	 * @param linkText o texto do link que se quer carregar. se for nulo usa o primeiro link que match linksSelector
	 * @return
	 */
	protected Document carregarHtmlDeLink(String html, String linksSelector, String linkText) {
		Document doc = Jsoup.parse(html);
		Elements asaldo = doc.select(linksSelector);
		Iterator<Element> itSaldos = asaldo.iterator();
		while(itSaldos.hasNext()){
			Element el = itSaldos.next();
			if(linkText == null || el.text().equalsIgnoreCase(linkText)){
				String loadUrl = el.attr("href").trim();
				if(! loadUrl.startsWith("http")){
					loadUrl = "https://ww70.itau.com.br/M/"+ loadUrl;
				}
				html = carregarHtml(loadUrl,200);
				doc = Jsoup.parse(html);
				break;
			}
			if(linkText == null){
				break;
			}
		}
		return doc;
	}

	protected BancoRegistro gerarRegistro(String data, String desc, String val) {
		BancoRegistro br = new BancoRegistro();
		br.setDescricao(desc);
		String signal = val.replaceAll("[^-]","");
		val = signal+val.replaceAll("[-]","");
		br.setValor(new BigDecimal(val.trim().replaceAll("[,.]([0-9]{2})$", ".$1").replaceAll("[,.]([0-9]{3})", "$1")));
		Calendar dataCal =  Calendar.getInstance();
		if(data.length() <= 5){
			data = data + "/" + dataCal.get(Calendar.YEAR);
		}
		try {
			dataCal.setTime(fmt.parse(data));
			if(dataCal.after(Calendar.getInstance())){
				dataCal.add(Calendar.YEAR, -1);
			}
		} catch (ParseException e) {
//			e.printStackTrace();
		}
		br.setData(dataCal.getTime());
		return br;
	}

	protected String carregarHtml(String url, Integer code) {
		return carregarHtml(url, Arrays.asList(code));
	}

	protected String carregarHtml(String url, List<Integer> codes) {
		HttpGet httpGet = new HttpGet(url);
		applyItauHeaders(httpGet);
		attachCookiesFromStore(httpGet);
		try {
			HttpResponse resp = client.execute(httpGet);
			if (codes.contains(resp.getStatusLine().getStatusCode())) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileOutputStream faos = new FileOutputStream(System.getProperty("user.home")+File.separatorChar+"itau.last.request.html");
				resp.getEntity().writeTo(baos);
				String html = new String(baos.toByteArray(), ENCODING);
				baos.close();
				baos = null;
				faos.write(html.getBytes("utf-8"));
				faos.close();

				if(html.contains(URL_MENU)){
					menuUrl = extrairUrl(html, URL_MENU, "");
				}
//				this.extratoUrl = html.replaceAll("(\n|\r)","").replaceAll("^.*\"(http([^\"]+?)M/SaldoExtratoLancamentos([^\"]+?)\").*$", "$1");

//				return URL_DOIS+param;
				return html;
			}
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			httpGet.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
		return null;
//		return null;
	}


	/**
	 * Se der certo o login (cod 302), retorna a url de "redirecionamento".
	 * @param urlFormLogin
	 * @param params
	 * @return
	 */
	protected String fazerLogin(String urlFormLogin, Map<String, String> params) {
		HttpPost httpPost = new HttpPost(urlFormLogin);
		applyItauHeaders(httpPost);
		attachCookiesFromStore(httpPost);
		httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		httpPost.setEntity(getMapEntity(ENCODING, params, null));
		try {
			HttpResponse resp = client.execute(httpPost);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode == 302) {
				String redirTo = resp.getFirstHeader(HttpHeaders.LOCATION).getValue();
					return redirTo;
			} else {
				throw new RuntimeException("Login issues ["+statusCode+"] "+httpPost.getFirstHeader("Location"));
			}
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			httpPost.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
		return null;
	}

	private Map<String,String> preparaParametrosLogin(String urlFormLogin, String agencia, String conta, String dvConta, String password){
		HttpGet httpGet = new HttpGet(urlFormLogin);
		applyItauHeaders(httpGet);
		attachCookiesFromStore(httpGet);
		try {
			HttpResponse resp = client.execute(httpGet);
			if (resp.getStatusLine().getStatusCode() == 200) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				String html = new String(baos.toByteArray(), ENCODING);
				baos.close();
				baos = null;

				//pega inputs da pagina
				Map<String,String> params = new HashMap<String,String>();
				for(Entry<String,List<String>> ent: SimpleHttpQuerier.getForm(html, "form",false).entrySet()){
					params.put(ent.getKey(), ent.getValue() != null && ent.getValue().size() > 0 ? ent.getValue().get(0):"");
				}

//				params.put("ctl00$ContentPlaceHolder1$txtAgenciaT",agencia);
//				params.put("ctl00$ContentPlaceHolder1$txtContaT",conta);
//				params.put("ctl00$ContentPlaceHolder1$txtDACT",dvConta);
//				params.put("ctl00$ContentPlaceHolder1$txtPassT",password);
				params.put("ctl00$ContentPlaceHolder1$btnLogInT.x","");
				params.put("ctl00$ContentPlaceHolder1$btnLogInT.y","");

				for(String key: new HashSet<String>(params.keySet())){
					if(key.contains("txtAgenciaT")){
						params.put(key,agencia);
					} else if(key.contains("txtContaT")){
						params.put(key,conta);
					} else if(key.contains("txtDACT")){
						params.put(key,dvConta);
					} else if(key.contains("txtPassT")){
						params.put(key,password);
					}else if(key.contains("btnLogInT.x")){
						params.put(key,(new Random().nextInt(5)+15)+"");
					}else if(key.contains("btnLogInT.y")){
						params.put(key,(new Random().nextInt(5)+20)+"");
					}
				}
				return params;
			}
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			httpGet.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
		return null;
	}
}
