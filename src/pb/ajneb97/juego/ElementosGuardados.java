package pb.ajneb97.juego;


import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;


public class ElementosGuardados {

	private ItemStack[] inventarioGuardado;
	private ItemStack[] equipamientoGuardado;
	private GameMode gamemodeGuardado;
	private float experienciaGuardada;
	private int levelGuardado;
	private int hambreGuardada;
	private double vidaGuardada;
	private double maxVidaGuardada;
	private boolean allowFlight;
	private boolean isFlying;
	
	public ElementosGuardados(ItemStack[] inventarioGuardado,ItemStack[] equipamientoGuardado,GameMode gamemodeGuardado,float experienciaGuardada,int levelGuardado,int hambreGuardada,
			double vidaGuardada,double maxVidaGuardada,boolean allowFlight,boolean isFlying) {
		this.inventarioGuardado = inventarioGuardado;
		this.equipamientoGuardado = equipamientoGuardado;
		this.gamemodeGuardado = gamemodeGuardado;
		this.experienciaGuardada = experienciaGuardada;
		this.levelGuardado = levelGuardado;
		this.hambreGuardada = hambreGuardada;
		this.vidaGuardada = vidaGuardada;
		this.maxVidaGuardada = maxVidaGuardada;
		this.allowFlight = allowFlight;
		this.isFlying = isFlying;
	}

	public boolean isAllowFlight() {
		return allowFlight;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public ItemStack[] getInventarioGuardado() {
		return inventarioGuardado;
	}
	
	public ItemStack[] getEquipamientoGuardado() {
		return equipamientoGuardado;
	}

	public GameMode getGamemodeGuardado() {
		return gamemodeGuardado;
	}
	
	public float getXPGuardada() {
		return experienciaGuardada;
	}
	
	public int getLevelGuardado() {
		return this.levelGuardado;
	}
	
	public int getHambreGuardada() {
		return this.hambreGuardada;
	}

	public double getVidaGuardada() {
		return vidaGuardada;
	}

	public double getMaxVidaGuardada() {
		return maxVidaGuardada;
	}
}
