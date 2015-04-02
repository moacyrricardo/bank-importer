package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankCharge implements Serializable {
	private static final long serialVersionUID = 7757214371180856502L;

	private static final SimpleDateFormat postDateFmt = new SimpleDateFormat("yyyy-MM-dd");

	private String id;
	private Integer amount;
	private String precise_amount;
	private String post_date;

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

	public String getPrecise_amount() {
		return precise_amount;
	}
	
	public BigDecimal getPreciseAmount(){
		return new BigDecimal(getPrecise_amount());
	}

	public void setPrecise_amount(String precise_amount) {
		this.precise_amount = precise_amount;
	}

	public String getPost_date() {
		return post_date;
	}
	
	public Date getPostDate(){
		try {
			return postDateFmt.parse(getPost_date());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setPost_date(String post_date) {
		this.post_date = post_date;
	}

}
