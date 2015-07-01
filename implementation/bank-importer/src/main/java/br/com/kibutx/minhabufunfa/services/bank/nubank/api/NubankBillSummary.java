package br.com.kibutx.minhabufunfa.services.bank.nubank.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NubankBillSummary implements Serializable {
	private static final long serialVersionUID = -2729550603168589542L;

	private List<NubankLink> _links = new ArrayList<NubankLink>();

	private String state;

	public List<NubankLink> get_links() {
		return _links;
	}

	public void set_links(List<NubankLink> _links) {
		this._links = _links;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
