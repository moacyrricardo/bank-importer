package br.com.kibutx.minhabufunfa.services.bank.santander;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.kibutx.minhabufunfa.services.bank.BancoImportador;
import br.com.kibutx.minhabufunfa.services.bank.BancoRegistro;
import br.com.quintoandar.consultasbr.core.SimpleHttpQuerier;

public class SantanderImportador extends SimpleHttpQuerier implements
		BancoImportador {
	private static final String MCPCUSTOMATTRIBUTES = "__MCPCUSTOMATTRIBUTES";

	protected static final Logger log = LoggerFactory
			.getLogger(SantanderImportador.class);

	protected static final String ENCODING = "UTF-8";

	public static final String PUBLIC_KEY = "xI0kSkh1P6rwZMe+QUbxHSzX7utrxw+/UnLaF0V9ufa+abO89iVhDGPyp83WdRRg27NtXSElw5AuD0IpuFe1cz6kxTxG67Du2ZCwzHbrCLUEguaDRKXGn0/tn6tB7IUfQvdxQ6DERLSrNbw5rwqkCaPbfrRn1zFVLOvI/phrcJc=";

	public static final String MODULUS = "10001";

	String begining = "http://m.santander.com.br/santandermovel/";
	
	String tempFileRequest = System.getProperty("user.home")+File.separatorChar+"santander.last.request.html";

	@Override
	protected boolean shouldPersistCookie(BasicClientCookie ck) {
		return true;
	}

	public void login(String agencia, String cc, String dv, String password) {
		HttpPost hp = new HttpPost(getLoginUrl());
		hp.setEntity(asKeyValueEntity(ENCODING, "txtAgency", agencia,
				"txtAccount", cc + dv, MCPCUSTOMATTRIBUTES,
				getMCPCripted(agencia, cc + dv)));
		try {
			HttpResponse resp = client.execute(hp);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileOutputStream faos = new FileOutputStream( tempFileRequest);
			resp.getEntity().writeTo(baos);
			String html = new String(baos.toByteArray(), ENCODING);
			baos.close();
			baos = null;
			faos.write(html.getBytes(ENCODING));
			faos.close();
		} catch (Throwable e) {
			log.error("Erro", e);
			// throw new
			// ServicoExternoException("Error chamarUrlESeguirParaLocationSeHouver",
			// e);
		} finally {
			hp.abort();
			connMan.closeIdleConnections(1, TimeUnit.MILLISECONDS);
		}
	}

	private String getMCPCripted(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		try {
			return URLEncoder.encode("=undefined&vh=" + encrypt(sb.toString()),
					"utf-8");
		} catch (Throwable t) {
			throw new RuntimeException("Error", t);
		}
	}

	private String getLoginUrl() {
		HttpGet httpGet = new HttpGet(begining);
		try {
			RedirectStrategy redirStrategy = client.getRedirectStrategy();
			final URI[] uri = new URI[1];
			client.setRedirectStrategy(new DefaultRedirectStrategy() {
				@Override
				public boolean isRedirected(HttpRequest request,
						HttpResponse response, HttpContext context)
						throws ProtocolException {
					// System.out.println("\tCookies: " +
					// client.getCookieStore().toString());
					for (Header c : response.getAllHeaders()) {
						// forcaAtualizacaoDeCookies(c);
						if (c.getName().equals("Location")) {
							log.debug("Location: " + c.getValue());
						}
						// log.info(c.getName()+": " + c.getValue());
					}
					boolean isRedirect = false;
					try {
						isRedirect = super.isRedirected(request, response,
								context);
					} catch (ProtocolException e) {
						e.printStackTrace();
					}
					// if (!isRedirect) {
					// int responseCode =
					// response.getStatusLine().getStatusCode();
					// if (responseCode == 301 || responseCode == 302) {
					// return true;
					// }
					// }
					return isRedirect;
				}

				@Override
				public URI getLocationURI(HttpRequest request,
						HttpResponse response, HttpContext context)
						throws ProtocolException {
					uri[0] = super.getLocationURI(request, response, context);
					return uri[0];
				}

			});
			HttpResponse resp = client.execute(httpGet);
			client.setRedirectStrategy(redirStrategy);
			
			// for(Header h : resp.getAllHeaders()){
			// System.out.println(h.getName()+"="+h.getValue());
			// }
			// if (codes.contains(resp.getStatusLine().getStatusCode())) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileOutputStream faos = new FileOutputStream(
					tempFileRequest);
			resp.getEntity().writeTo(baos);
			String html = new String(baos.toByteArray(), ENCODING);
			baos.close();
			baos = null;
			faos.write(html.getBytes(ENCODING));
			faos.close();
			String url = html.replaceAll("(\n|\r)", "").replaceAll(
					"^.*action=\"(default.aspx[^\"]*?)\".*$", "$1");
			
			
			
			return uri[0].toString().replaceAll("/default[^/]*$", "/"+url);

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
		return null;
	}

	public List<BancoRegistro> carregarLancamentosExtrato() {
		return new ArrayList<BancoRegistro>();
	}

	public static void main(String[] args) throws Throwable {
		SantanderImportador si = new SantanderImportador();
		si.login("ag", "cc", "dv", "password");
		if ("aa".equalsIgnoreCase("AA")) {
			return;
		}
		//
		// Context ctx = Context.enter();
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// InputStream is =
		// SantanderImportador.class.getResourceAsStream("/jcrypto.js");
		// copy(is, baos, 1);
		// String source = new String(baos.toByteArray(),"utf-8");
		// Script script = ctx.compileString(source, "sharedScript", 1, null);
		// String javaScriptExpression = "sayHello(name);";
		// Reader javaScriptFile = new
		// StringReader("function sayHello(name) {\n" +
		// "    println('Hello, '+name+'!');\n" + "}");

		// ScriptEngineManager factory = new ScriptEngineManager();
		// ScriptEngine engine = factory.getEngineByName("JavaScript");
		// ScriptContext context = engine.getContext();
		// context.setAttribute("name", "JavaScript",
		// ScriptContext.ENGINE_SCOPE);

		// engine.eval(javaScriptFile);
		// engine.eval(javaScriptExpression);
		String value = "xxxxxxxx";

		String asstr = encrypt(value);
		System.out.println(asstr);
		//
		// Cipher cipher2 = getCipher();
		// cipher2.init(Cipher.DECRYPT_MODE, key);
		//
		// cipherData = cipher2.doFinal(cipherData);
		// System.out.println(toHex(cipherData));
	}

	private static String encrypt(String value)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchProviderException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");

		byte[] digestValue = md.digest(value.getBytes());
		String valueSha = toHex(digestValue);

		byte[] keyAsBytes = Base64.decodeBase64(PUBLIC_KEY);

		BigInteger modulus = new BigInteger(toHex(keyAsBytes), 16);
		BigInteger pubExp = new BigInteger(MODULUS, 16);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, key);

//		System.out.println(valueSha);

		byte[] cipherData = cipher.doFinal(digestValue);
		String asstr = toHex(cipherData);
		System.out.println(asstr);
		return asstr;
	}

	private static Cipher getCipher() throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException {
		 return Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
//		return Cipher.getInstance("RSA/ECB/NoPadding");
	}

	private static String toHex(byte[] array) {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0)
			return String.format("%0" + paddingLength + "d", 0) + hex;
		else
			return hex;
	}
}
