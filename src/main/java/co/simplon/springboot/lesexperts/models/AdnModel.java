package co.simplon.springboot.lesexperts.models;

import java.util.ArrayList;

public class AdnModel {
	
	private int id_ADN;
	private String ADN;
	private PersonneModel personneAdn;
	private ArrayList<ArmeModel> listArmeAdn;
	private ArrayList<VehiculeModel> listVehiculeAdn;
	
	public AdnModel() {
		super();
	}

	public int getId_ADN() {
		return id_ADN;
	}

	public void setId_ADN(int id_ADN) {
		this.id_ADN = id_ADN;
	}

	public String getADN() {
		return ADN;
	}

	public void setADN(String aDN) {
		ADN = aDN;
	}

	public PersonneModel getPersonneAdn() {
		return personneAdn;
	}

	public void setPersonneAdn(PersonneModel personneAdn) {
		this.personneAdn = personneAdn;
	}

	public ArrayList<ArmeModel> getListArmeAdn() {
		return listArmeAdn;
	}

	public void setListArmeAdn(ArrayList<ArmeModel> listArmeAdn) {
		this.listArmeAdn = listArmeAdn;
	}

	public ArrayList<VehiculeModel> getListVehiculeAdn() {
		return listVehiculeAdn;
	}

	public void setListVehiculeAdn(ArrayList<VehiculeModel> listVehiculeAdn) {
		this.listVehiculeAdn = listVehiculeAdn;
	}

	
}
