package br.com.kibutx.minhabufunfa.services.bank.itau;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;

public class ItauPoupancaImportador extends ItauImportador implements BancoImportador {
	private static final Logger log = LoggerFactory.getLogger(ItauPoupancaImportador.class);

	@Override
	public List<BancoRegistro> carregarLancamentosExtrato(){
		carregarOpcoesMenu();
		String html =  /*carregarHtml(poupancaUrl, 200);
		html = */carregarHtml("https://ww70.itau.com.br/M/SaldoPoupanca.aspx", 200);
		Document doc = carregarHtmlDeLink(html,"a[href^=SaldoPoupanca]","Últimos 30 dias");
		Element tableExtrato = doc.getElementById("ctl00_ContentPlaceHolder1_Fieldset2");
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

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		System.getProperties().put( "proxySet", "true" );
//		System.getProperties().put( "socksProxyHost", "localhost" );
//		System.getProperties().put( "socksProxyPort", "10002" );
//		System.setProperty("http.proxyHost", "localhost");
//		System.setProperty("http.proxyPort", "10002");
//		System.setProperty("https.proxyHost", "localhost");
//		System.setProperty("https.proxyPort", "10002");

		ItauPoupancaImportador ii = new ItauPoupancaImportador();
//		System.out.println(ii.getUrlFormLogin());
		System.out.println("Poupança");
				ii.login("agencia", "conta", "dv", "password");
		for(BancoRegistro lanc: ii.carregarLancamentosExtrato()){
			System.out.println(sdf.format(lanc.getData())+"\t"+lanc.getDescricao()+ "\t"+lanc.getValor());
		}
	}
}
