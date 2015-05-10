package br.com.kibutx.minhabufunfa.services.bank.santander;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;
import br.com.quintoandar.consultasbr.core.SimpleHttpQuerier;

public class SantanderImportador extends SimpleHttpQuerier implements BancoImportador {
	protected static final Logger log = LoggerFactory.getLogger(SantanderImportador.class);

	protected static final String ENCODING = "UTF-8";

	String begining = "http://m.santander.com.br/santandermovel/";

	@Override
	protected boolean shouldPersistCookie(BasicClientCookie ck) {
		return true;
	}

	public void login(String agencia, String cc, String dv, String password) {
		HttpGet httpGet = new HttpGet(begining);
		try {
			HttpResponse resp = client.execute(httpGet);
			// if (codes.contains(resp.getStatusLine().getStatusCode())) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileOutputStream faos = new FileOutputStream("C:/Users/Moacyr/Desktop/bla.html");
			resp.getEntity().writeTo(baos);
			String html = new String(baos.toByteArray(), ENCODING);
			baos.close();
			baos = null;
			faos.write(html.getBytes(ENCODING));
			faos.close();

			// if(html.contains(URL_MENU)){
			// menuUrl = extrairUrl(html, URL_MENU, "");
			// }
			// this.extratoUrl =
			// html.replaceAll("(\n|\r)","").replaceAll("^.*\"(http([^\"]+?)M/SaldoExtratoLancamentos([^\"]+?)\").*$",
			// "$1");

			// return URL_DOIS+param;
			// return html;
			// }
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			httpGet.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
		// return null;
	}

	public List<BancoRegistro> carregarLancamentosExtrato() {
		return new ArrayList<BancoRegistro>();
	}

	public static void main(String[] args) throws Throwable {
		SantanderImportador si = new SantanderImportador();
		si.login("ag", "cc", "dv", "password");

		Context ctx = Context.enter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = SantanderImportador.class.getResourceAsStream("/jcrypto.js");
		copy(is, baos, 1);
		String source = new String(baos.toByteArray(),"utf-8");
		Script script = ctx.compileString(source, "sharedScript", 1, null);
//		String javaScriptExpression = "sayHello(name);";
//		Reader javaScriptFile = new StringReader("function sayHello(name) {\n" + "    println('Hello, '+name+'!');\n" + "}");

//		ScriptEngineManager factory = new ScriptEngineManager();
//		ScriptEngine engine = factory.getEngineByName("JavaScript");
//		ScriptContext context = engine.getContext();
//		context.setAttribute("name", "JavaScript", ScriptContext.ENGINE_SCOPE);

//		engine.eval(javaScriptFile);
//		engine.eval(javaScriptExpression);
	}
}
