package br.com.kibutx.minhabufunfa.services.bank;

import java.util.List;

public interface BancoImportador {
	public List<BancoRegistro> carregarLancamentosExtrato();
}
