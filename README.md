# bank-importer
Brazilian bank statements reader.
Depends on authentication through id (agency+account or cpf) + password

# Supported Banks
 1. [x] Itau
 2. [x] Itau Poupança (Itau Savings Account)
 3. [x] Nubank
 4. [ ] Santander
 5. [ ] Itau Cartão de Crédito
 
# future
 1. [ ] transaction currency
 2. [ ] Nubank: grouped Related transactions

BancoSyncer Usage
====
Create a file on your home directory named *bancosyncer.config*.
```
####engines
engines=itau,itaup

itau.class=.itau.ItauImportador
##method,agency,account,verify digit,eletronic password
itau.config=login,0000,00000,0,000000

itaup.class=.itau.ItauPoupancaImportador
##method,agency,account,verify digit,eletronic password
itaup.config=login,0000,00000,0,000000

nubank.class=.nubank.NubankImportador
##method,cpf,eletronic password
nubank.config=login,cpf,password
```
Run the class: 
```java
br.com.kibutx.minhabufunfa.services.bank.BancoSyncer
```
