package pb.ajneb97.utils;

import pb.ajneb97.PaintballBattle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class UtilidadesOtros {

	public static boolean isChatNew() {
        ServerVersion serverVersion = PaintballBattle.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_19_R1)){
            return true;
        }
        return false;
    }

    public static boolean isNew() {
        ServerVersion serverVersion = PaintballBattle.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_16_R1)){
            return true;
        }
        return false;
    }

    public static boolean isLegacy() {
        ServerVersion serverVersion = PaintballBattle.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_13_R1)){
            return false;
        }else {
            return true;
        }
    }
    
	public static String getTiempo(int tiempo) {
		int minutos = tiempo/60;
		int segundos = tiempo - (minutos*60);
		String segundosMsg = "";
		String minutosMsg = "";
		if(segundos >= 0 && segundos <= 9) {
			segundosMsg = "0"+segundos;
		}else {
			segundosMsg = segundos+"";
		}
		
		if(minutos >= 0 && minutos <= 9) {
			minutosMsg = "0"+minutos;
		}else {
			minutosMsg = minutos+"";
		}
		
		return minutosMsg+":"+segundosMsg;
	}	
	
	public static int coinsGanados(Player jugador,FileConfiguration config) {
		String coinsString = config.getString("coins_per_kill");
		if(coinsString.contains("-")) {
			String[] separados = coinsString.split("-");
			int num1 = Integer.valueOf(separados[0]);
			int num2 = Integer.valueOf(separados[1]);
			return getNumeroAleatorio(num1,num2);
		}else {
			return Integer.valueOf(coinsString);
		}
	}
	
	public static int getNumeroAleatorio(int min, int max) {
		Random r = new Random();
		int numero = r.nextInt((max - min) + 1) + min;
		return numero;
	}
	
	public static void generarParticula(String particle, Location l, float xOffset, float yOffset, float zOffset, float speed, int count) {
		if(UtilidadesOtros.isLegacy()) {
			float red = 0;
			float green = 0;
			float blue = 0;
			boolean redstone = false;
			if(particle.startsWith("REDSTONE;")) {
				redstone = true;
				String[] sep = particle.split(";");
				int rgb = Integer.valueOf(sep[1]);
				particle = sep[0];
				Color color = Color.fromRGB(rgb);
				red = (float) color.getRed()/255;
				green = (float) color.getGreen()/255;
				blue = (float) color.getBlue()/255;
			}
			try {
				//Revisar el particle de REDSTONE
				Class<?> packetEnumParticle = getNMSClass("EnumParticle");
				Method packetEnumMethod = packetEnumParticle.getMethod("valueOf", String.class);
				Object enumParticle = packetEnumMethod.invoke(null,particle);
				Class<?> packetClass = getNMSClass("PacketPlayOutWorldParticles");
				
				Constructor<?> packetConstructor = null;
				for(Constructor<?> c : packetClass.getConstructors()) {
					if(c.toGenericString().contains("EnumParticle")) {
						packetConstructor = c;
					}
				}
//				Constructor<?> packetConstructor = packetClass.getConstructor(packetEnumParticle, boolean.class, float.class, float.class, float.class, float.class, float.class, float.class,
//						float.class, int.class, int.class);
				Object packet = null;
				if(redstone) {
					packet = packetConstructor.newInstance(enumParticle, true, (float)l.getX(), (float)l.getY(), (float)l.getZ(), red, green, blue, count, 0, null);
				}else {
					packet = packetConstructor.newInstance(enumParticle, false, (float)l.getX(), (float)l.getY(), (float)l.getZ(), (float)xOffset, (float)yOffset, (float)yOffset, speed, count, null);
				}
		        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
		        for(Player player : Bukkit.getOnlinePlayers()) {
		        	sendPacket.invoke(getConnection(player), packet);
		        }
		        
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SecurityException | NoSuchMethodException | NoSuchFieldException | InstantiationException e) {
				
			}
		}else {
			l.getWorld().spawnParticle(Particle.valueOf(particle),l,count,xOffset,yOffset,zOffset,speed);
		}
	}
	
	private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
	
    private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }
	
	public static boolean pasaConfigInventario(Player jugador,FileConfiguration config) {
		if(config.getString("empty_inventory_to_join").equals("true")) {
			PlayerInventory inv = jugador.getInventory();
			for(ItemStack item : inv.getContents()) {
				if(item != null && !item.getType().equals(Material.AIR)) {
					return false;
				}
			}
			for(ItemStack item : inv.getArmorContents()) {
				if(item != null && !item.getType().equals(Material.AIR)) {
					return false;
				}
			}
			return true;
		}else {
			return true;
		}
	}
	
	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}
