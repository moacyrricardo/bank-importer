package br.com.kibutx.minhabufunfa.services.bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class BancoSyncer {

	Map<String, BancoImportador> engines = new LinkedHashMap<String,BancoImportador>();
	
	public void init() throws Throwable {
		engines.clear();
		
		Properties p = new Properties();
		String fileNamee = System.getProperty("user.home")+File.separatorChar+"bancosyncer.config";
		System.out.println(fileNamee);
		p.load(new FileInputStream(fileNamee));
		
		for(String eng : p.getProperty("engines").split(",")){
			String klassName = p.getProperty(eng+".class");
			String[] methodParams = p.getProperty(eng+".config").split(",");
			Class<? extends  BancoImportador> biKlass = (Class<? extends BancoImportador>) Class.forName((klassName.charAt(0) == '.'?"br.com.kibutx.minhabufunfa.services.bank":"")+klassName);
			for(Method mt: biKlass.getMethods()){
				if(mt.getName().equals(methodParams[0]) && methodParams.length-1 == mt.getParameterTypes().length){
					BancoImportador bi = biKlass.newInstance();
					String[] params = new String[methodParams.length-1];
					System.arraycopy(methodParams, 1, params, 0, params.length);
					mt.invoke(bi, params);
					engines.put(eng, bi);
				}
			}
		}
	}
	
	private void print(PrintStream out) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		for(Map.Entry<String, BancoImportador> ent:engines.entrySet()){
			out.println("Engine: "+ent.getKey());
			for(BancoRegistro lanc :ent.getValue().carregarLancamentosExtrato()){
				System.out.println(sdf.format(lanc.getData())+"\t"+lanc.getDescricao()+ "\t"+lanc.getValor());
			}
		}
		
	}
	
	public static void main(String[] args) throws Throwable {
		BancoSyncer bs = new BancoSyncer();
		
		bs.init();
		
		bs.print(System.out);
	}
}
