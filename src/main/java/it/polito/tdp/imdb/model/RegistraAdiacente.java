package it.polito.tdp.imdb.model;

public class RegistraAdiacente implements Comparable<RegistraAdiacente>{

	private Director director;
	private Integer peso;
	public RegistraAdiacente(Director director, Integer peso) {
		super();
		this.director = director;
		this.peso = peso;
	}
	public Director getDirector() {
		return director;
	}
	public void setDirector(Director director) {
		this.director = director;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	@Override
	public int compareTo(RegistraAdiacente o) {
		return Integer.compare(o.peso, this.peso);
	}
	
}
