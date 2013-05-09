package nl2kaos;

public class Operation {
	//Cada operación tienen un verbo y un objeto
	private String verbo;
	private String objeto;
	
	public Operation(String verbo, String objeto) {
		super();
		this.verbo = verbo;
		this.objeto = objeto;
	}
	public String getVerbo() {
		return verbo;
	}
	public void setVerbo(String verbo) {
		this.verbo = verbo;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

}
