package pb.ajneb97.juego;


public class Killstreak {

	private String tipo;
	private int tiempo;
	
	public Killstreak(String tipo, int tiempo) {
		this.tipo = tipo;
		this.tiempo = tiempo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getTiempo() {
		return tiempo;
	}

	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	
}
