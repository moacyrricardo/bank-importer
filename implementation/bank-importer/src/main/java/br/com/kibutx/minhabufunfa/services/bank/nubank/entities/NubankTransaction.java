package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankTransaction implements Serializable {
	private static final SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

	private static final long serialVersionUID = 4392220138901590745L;
	private String id;
	private Integer amount;
	private String merchant_name;
	private String original_merchant_name;
	private String time;
	private String mcc;
	private List<NubankCharge> charges_list;
	private String country;
	private NubankTransactionType type;
	private NubankExchange fx;
	
	private Status status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Quantidade em centavos. Por ex. R$10 = 1000 centavos
	 * 
	 * @return
	 */
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public String getOriginal_merchant_name() {
		return original_merchant_name;
	}

	public void setOriginal_merchant_name(String original_merchant_name) {
		this.original_merchant_name = original_merchant_name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Date getTimeAsDate() {
		try {
			return iso.parse(getTime().replaceAll("[Zz]$","-0000"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Categoria?
	 * @return
	 */
	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mmc) {
		this.mcc = mmc;
	}

	public List<NubankCharge> getCharges_list() {
		return charges_list;
	}

	public void setCharges_list(List<NubankCharge> charges_list) {
		this.charges_list = charges_list;
	}

	/**
	 * Country code: BRA (Reais), USA (Dolares)
	 *
	 * @return
	 */
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public NubankTransactionType getType() {
		return type;
	}

	public void setType(NubankTransactionType type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public NubankExchange getFx() {
		return fx;
	}

	public void setFx(NubankExchange fx) {
		this.fx = fx;
	}


	public static enum Status {
		settled, unsettled, canceled
	}
}
