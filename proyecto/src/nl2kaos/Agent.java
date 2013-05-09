package nl2kaos;

import java.util.*;

public class Agent {
	//Un agente realiza operaciones
	//Un agente tiene nombre, definición y una o varias operaciones
	private String name;
	private String lemma;
	private String def;
	private int category;
	private int freq;
	
	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	private ArrayList<Operation> op=new ArrayList<Operation>();
	
	public Agent(){
		def="";
	}
	
	public Agent(String name, int freq) {
		super();
		this.name = name;
		category=-1;
		this.freq=freq;
	}
	public Agent(String name, String lemma) {
		super();
		this.name = name;
		this.lemma=lemma;
		category=-1;
	}
	public Agent(String name, Operation op) {
		super();
		this.name = name;
		this.op.add(new Operation(op.getVerbo(),op.getObjeto()));
		category=-1;
		freq=1;
	}
	public Agent(String name) {
		super();
		this.name = name;
		category=-1;
		freq=1;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDef() {
		return def;
	}
	public void setDef(String def) {
		this.def = def;
	}
	public Operation getOp(int n) {
		return op.get(n);
	}
	public void setOp(Operation op) {
		this.op.add(op);
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
}
