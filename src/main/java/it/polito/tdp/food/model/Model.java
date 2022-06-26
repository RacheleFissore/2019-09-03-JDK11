package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	private FoodDao dao;
	private Graph<String, DefaultWeightedEdge> grafo;
	private int calorie;
	private int pesoMax;
	private List<String> best;
	
	public Model() {
		dao = new FoodDao();
	}
	
	public List<String> getPortionName() {
		return dao.getVertici(calorie);
	}
	
	public void creaGrafo(int calorie) {
		this.calorie = calorie;
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(calorie));
		
		
		for(Adiacenza adiacenza : dao.getArchi()) {
			if(grafo.vertexSet().contains(adiacenza.getVertice1()) && grafo.vertexSet().contains(adiacenza.getVertice2())) {
				Graphs.addEdgeWithVertices(grafo, adiacenza.getVertice1(), adiacenza.getVertice2(), adiacenza.getPeso());
			}
		}
		
	}
	
	public int nVertici() {
		return grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return grafo.edgeSet().size();
	}
	
	public String getCorrelate(String porzione) {
		String stampa = "";
		for(String v : Graphs.neighborListOf(grafo, porzione)) {
			stampa += v + " - " + grafo.getEdgeWeight(grafo.getEdge(porzione, v)) + "\n";
		}
		
		return stampa;
	}
	
	public String trovaSequenza(int N, String partenza) {
		List<String> parziale = new ArrayList<>();
		String stampa = "";
		best = new ArrayList<>();
		pesoMax = 0;
		parziale.add(partenza);
		cerca(parziale, N);
		
		for(String v : best) {
			stampa += v + "\n";
		}
		
		stampa += "\nPeso totale: " + calcolaPeso(best);
		return stampa;
	}

	private void cerca(List<String> parziale, int n) {
		if(parziale.size() == n) {
			if(pesoMax < calcolaPeso(parziale)) {
				best = new ArrayList<>(parziale);
				pesoMax = calcolaPeso(parziale);
			}
		}
		
		for(String v : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(v)) {
				parziale.add(v);
				cerca(parziale, n);
				parziale.remove(v);
			}
		}
		
	}

	private int calcolaPeso(List<String> parziale) {
		int peso = 0;
		for(int i = 1; i < parziale.size(); i++) {
			peso += (int)grafo.getEdgeWeight(grafo.getEdge(parziale.get(i-1), parziale.get(i)));
		}
		return peso;
	}
}
