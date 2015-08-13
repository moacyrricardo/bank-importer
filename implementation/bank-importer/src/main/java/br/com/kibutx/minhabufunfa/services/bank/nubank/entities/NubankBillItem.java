package br.com.kibutx.minhabufunfa.services.bank.nubank.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NubankBillItem implements Serializable {
	private static final SimpleDateFormat postDateFmt = new SimpleDateFormat("yyyy-MM-dd");
	private static final long serialVersionUID = -8522409039531320734L;
	private String id;
	private Integer amount;
	private String post_date;
	private String title;
	private NubankBillItemType type;
	private String href;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getPost_date() {
		return post_date;
	}

	public Date getPostDate() {
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String description) {
		this.title = description;
	}

	public NubankBillItemType getType() {
		if(type == null){
			type = NubankBillItemType.other;	
			if(amount < 0){
				if("Pagamento recebido".equals(getTitle())){
					type = NubankBillItemType.payment;
				} else {
					type = NubankBillItemType.reversal;
				}
			} else if(amount > 0){
				type = NubankBillItemType.charge;
			}
		}
		return type;
	}

	public void setType(NubankBillItemType type) {
		this.type = type;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
