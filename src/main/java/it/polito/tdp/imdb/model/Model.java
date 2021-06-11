package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private SimpleWeightedGraph<Director, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer,Director> idMap;
	private boolean grafoCreato;
	
	public Model() {
		this.dao= new ImdbDAO();
		this.idMap= new HashMap<>();
		this.grafoCreato=false;
	}
	
	public void creaGrafo(int anno) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		dao.loadIdMap(idMap);
		Graphs.addAllVertices(grafo, dao.getVertici(idMap,anno));
		for (Adiacenza a : dao.getAdiacenze(idMap, anno)) {
			if (grafo.vertexSet().contains(a.getD1()) && grafo.vertexSet().contains(a.getD2())) {
				Graphs.addEdgeWithVertices(grafo, a.getD1(), a.getD2(), a.getPeso());
			}
		}
		this.grafoCreato=true;
	}

	public boolean isGrafoCreato() {
		return grafoCreato;
	}
	
	public int getNumVertici() {
		return grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return grafo.edgeSet().size();
	}
	
	public Set<Director> getVertici() {
		return grafo.vertexSet();
	}
	
	public List<RegistraAdiacente> getAdiacenti(Director director) {
		List<RegistraAdiacente> ris= new ArrayList<>();
		for (Director d : Graphs.neighborListOf(grafo, director)) {
			ris.add(new RegistraAdiacente(d, (int)grafo.getEdgeWeight(this.grafo.getEdge(d, director))));
		}
		Collections.sort(ris);
		return ris;
	}
}
