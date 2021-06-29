package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private SimpleWeightedGraph<Director, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer,Director> idMap;
	private boolean grafoCreato;
	
	private ArrayList<Director> best;
	private int max;
	private ConnectivityInspector<Director, DefaultWeightedEdge> ci;
	private Set<Director> raggiungibili;
	
	public Model() { 
		this.dao= new ImdbDAO();
		this.idMap= new HashMap<>();
		this.grafoCreato=false;
		this.best= new ArrayList<>();
		this.raggiungibili= new HashSet<>();
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
		this.ci= new ConnectivityInspector<>(grafo);
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
	
	public List<Director> percorsoMax(int c, Director partenza){ 
		this.best=null;
		this.max=0;
		this.raggiungibili= ci.connectedSetOf(partenza); //per sapere quanli director posso raggiungere dalla mia partenza
		List<Director> parziale= new ArrayList<>();
		parziale.add(partenza);
		ricorsione(parziale , c);
		return this.best;
	}
	
	private void ricorsione(List<Director> parziale, int c){
		Director ultimo = parziale.get(parziale.size()-1);
		int massimo= this.calcolaMax(parziale);
		
		//if (this.raggiungibili.isEmpty()){ 
			//qui ci sarebbe la if messa in fondo
			//else //ho trovato una soluzione ma non è la migliore
				//return;
		//}
		//da qui faccio la ricorsione:
		for(DefaultWeightedEdge e : grafo.edgesOf(ultimo)){
			Director prossimo= Graphs.getOppositeVertex(grafo, e, ultimo); 
			if (!parziale.contains(prossimo)){ //per evitare i cicli 
				parziale.add(prossimo);
				if (this.calcolaMax(parziale)>c) {
					//se supero il max permesso allora non testo la ricorsione con questo director
					parziale.remove(parziale.size()-1);
					this.raggiungibili.remove(prossimo);
				}
				else {
					ricorsione(parziale,c);
					parziale.remove(parziale.size()-1); //backtracking
					this.raggiungibili.remove(prossimo);
				}
			}
		}
		if(this.best==null || massimo>this.max){ //prima iterazione o ho trovato una soluzione migliore
			System.out.print("jewf");
			this.max=massimo;
			this.best= new ArrayList<>(parziale);
			return;
		}
	}

	private int calcolaMax(List<Director> parziale) {
		int peso=0;
		int i=0; //indice che mi serve per prendere il director successivo in parziale
		for (Director d : parziale) {
			if (i==(parziale.size()-1)) 
				break;
			DefaultWeightedEdge e = grafo.getEdge(d, parziale.get(i+1));
			i++;
			peso += grafo.getEdgeWeight(e);
		}
		return peso;
	}
	
	public int pesoMax() {
		return this.max;
	}
	
	
	
}
