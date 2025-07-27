package pb.ajneb97.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import pb.ajneb97.PaintballBattle;
import pb.ajneb97.api.Hat;
import pb.ajneb97.api.Perk;

public class MySQL {

	public static boolean isEnabled(FileConfiguration config){
		if(config.getString("mysql-database.enabled").equals("true")){
			return true;
		}else{
			return false;
		}
	}
	
		//Cada usuario tendra un registro en donde se guardaran sus registros global, que se especifica con el INTEGER Global = 1
		//Si el atributo Global = 0 significa que este registro sera usado para los tops mensuales y semanales
		public static void createTablePlayers(ConexionDatabase conexion) {
	        try {
	        	PreparedStatement statement = conexion.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+conexion.getTablePlayers()+" (`UUID` varchar(200), `Name` varchar(40), `Date` varchar(100), `Year` INT(10), `Month` INT(5), `Week` INT(5), `Day` INT(5), `Arena` varchar(40), `Win` INT(2), `Tie` INT(2), `Lose` INT(2), `Kills` INT(5), `Coins` INT(10), `Global_Data` INT(2) )");
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
		
		public static void createTablePerks(ConexionDatabase conexion) {
	        try {
	        	PreparedStatement statement = conexion.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+conexion.getTablePerks()+" (`UUID` varchar(200), `Name` varchar(40), `Perk` varchar(40), `Level` INT(2) )");
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
		
		public static void createTableHats(ConexionDatabase conexion) {
	        try {
	        	PreparedStatement statement = conexion.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+conexion.getTableHats()+" (`UUID` varchar(200), `Name` varchar(40), `Hat` varchar(40), `Selected` INT(2) )");
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
		
		public static int getStatsTotales(PaintballBattle plugin, String name, String tipo){
			int cantidad = 0;
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Name=? AND Global_Data=1)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();
				
				while(resultado.next()){
					cantidad = resultado.getInt(tipo);
				}
				
				return cantidad;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cantidad;
		}
		
		//Comprueba solo el dato global
		public static boolean jugadorExiste(PaintballBattle plugin, String player){
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Name=? AND Global_Data=1)");
				statement.setString(1, player);
				ResultSet resultado = statement.executeQuery();
				if(resultado.next()){
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		public static void actualizarJugadorPartidaAsync(final PaintballBattle plugin,final String uuid,final String player,final int wins,final int loses,final int ties,final int kills){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTablePlayers()+" SET Win=?, Tie=?, Lose=?, Kills=? WHERE (Name=? AND Global_Data=1)");
	    				statement.setInt(1, wins);
	    				statement.setInt(2, ties);
	    				statement.setInt(3, loses);
	    				statement.setInt(4, kills);
	    				statement.setString(5, player);
	    				statement.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});	
		}
		
		public static void agregarCoinsJugadorAsync(final PaintballBattle plugin,final String player,final int coins){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTablePlayers()+" SET Coins=`Coins`+? WHERE (Name=? AND Global_Data=1)");
	    				statement.setInt(1, coins);
	    				statement.setString(2, player);
	    				statement.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});	
		}
		
		public static void removerCoinsJugadorAsync(final PaintballBattle plugin,final String player,final int coins){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTablePlayers()+" SET Coins=`Coins`-? WHERE (Name=? AND Global_Data=1)");
	    				statement.setInt(1, coins);
	    				statement.setString(2, player);
	    				statement.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});	
		}
		
		public static void crearJugadorPartidaAsync(final PaintballBattle plugin, final String uuid,final String name,final String arena,final int win,final int tie,final int lose,final int kills,final int coins,final int global){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try{
	            		Calendar calendar = Calendar.getInstance();
	            		Date date = new Date();
	            		calendar.setTime(date);
	            		int mes = calendar.get(Calendar.MONTH);
	            		int año = calendar.get(Calendar.YEAR);
	            		int dia = calendar.get(Calendar.DAY_OF_MONTH);
	            		int dia_semana = calendar.get(Calendar.WEEK_OF_MONTH);
	            		
	        			PreparedStatement insert = plugin.getConexionDatabase().getConnection()
	        					.prepareStatement("INSERT INTO "+plugin.getConexionDatabase().getTablePlayers()+" (UUID,Name,Date,Year,Month,Week,Day,Arena,Win,Tie,Lose,Kills,Coins,Global_Data) VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        			insert.setString(1, uuid);
	        			insert.setString(2, name);
	        			insert.setString(3, date.getTime()+"");
	        			insert.setInt(4, año);
	        			insert.setInt(5, mes);
	        			insert.setInt(6, dia_semana);
	        			insert.setInt(7, dia);
	        			insert.setString(8, arena);
	        			insert.setInt(9, win);
	        			insert.setInt(10, tie);
	        			insert.setInt(11, lose);
	        			insert.setInt(12, kills);
	        			insert.setInt(13, coins);
	        			insert.setInt(14, global);
	        			insert.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});
			
		}
		
		public static boolean jugadorTieneHat(PaintballBattle plugin, String player, String hat){
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTableHats()+" WHERE (Name=? AND Hat=?)");
				statement.setString(1, player);
				statement.setString(2, hat);
				ResultSet resultado = statement.executeQuery();
				if(resultado.next()){
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		public static void agregarJugadorHatAsync(final PaintballBattle plugin, final String uuid,final String name,final String hat){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try{
	        			PreparedStatement insert = plugin.getConexionDatabase().getConnection()
	        					.prepareStatement("INSERT INTO "+plugin.getConexionDatabase().getTableHats()+" (UUID,Name,Hat,Selected) VALUE (?,?,?,?)");
	        			insert.setString(1, uuid);
	        			insert.setString(2, name);
	        			insert.setString(3, hat);
	        			insert.setInt(4, 0);
	        			insert.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});
		}
		
		public static boolean jugadorTieneHatSeleccionado(PaintballBattle plugin, String player, String hat){
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTableHats()+" WHERE (Name=? AND Hat=? AND Selected=1)");
				statement.setString(1, player);
				statement.setString(2, hat);
				ResultSet resultado = statement.executeQuery();
				if(resultado.next()){
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		public static ArrayList<Hat> getHatsJugador(PaintballBattle plugin,String name){
			ArrayList<Hat> hats = new ArrayList<Hat>();
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTableHats()+" WHERE (Name=?)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){			
					String hat = resultado.getString("Hat");
					int selected = resultado.getInt("Selected");
					boolean selectedB = false;
					if(selected == 1) {
						selectedB = true;
					}
					hats.add(new Hat(hat,selectedB));	
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return hats;
		}
		
		public static void deseleccionarHats(final PaintballBattle plugin,final String player){
			try {
        		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTableHats()+" SET Selected=0 WHERE (Name=? AND Selected=1)");
				statement.setString(1, player);
				statement.executeUpdate();
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}	
		}
		
		public static void seleccionarHatAsync(final PaintballBattle plugin,final String player,final String hat){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		deseleccionarHats(plugin,player);
	    				
	            		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTableHats()+" SET Selected=1 WHERE (Name=? AND Hat=?)");
	            		statement.setString(1, player);
	    				statement.setString(2, hat);
	    				statement.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});	
		}
		
		public static void crearJugadorPerkAsync(final PaintballBattle plugin, final String uuid,final String name,final String perk){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	try{
	        			PreparedStatement insert = plugin.getConexionDatabase().getConnection()
	        					.prepareStatement("INSERT INTO "+plugin.getConexionDatabase().getTablePerks()+" (UUID,Name,Perk,Level) VALUE (?,?,?,?)");
	        			insert.setString(1, uuid);
	        			insert.setString(2, name);
	        			insert.setString(3, perk);
	        			insert.setInt(4, 1);
	        			insert.executeUpdate();
	        		} catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	            }
			});
			
		}	
		
		public static int getNivelPerk(PaintballBattle plugin, String name, String perk){
			int level = 0;
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePerks()+" WHERE (Name=? AND Perk=?)");
				statement.setString(1, name);
				statement.setString(2, perk);
				ResultSet resultado = statement.executeQuery();
				while(resultado.next()){	
					level = resultado.getInt("Level");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return level;
		}
		
		public static boolean jugadorPerkExiste(PaintballBattle plugin, String player, String perk){
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePerks()+" WHERE (Name=? AND Perk=?)");
				statement.setString(1, player);
				statement.setString(2, perk);
				ResultSet resultado = statement.executeQuery();
				if(resultado.next()){
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		public static ArrayList<Perk> getPerksJugador(PaintballBattle plugin,String name){
			ArrayList<Perk> perks = new ArrayList<Perk>();
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePerks()+" WHERE (Name=?)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){			
					String perk = resultado.getString("Perk");
					int level = resultado.getInt("Level");
					perks.add(new Perk(perk,level));	
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return perks;
		}
		
		public static void setPerkJugadorAsync(final PaintballBattle plugin,final String uuid,final String player,final String perk,final int level){
			if(jugadorPerkExiste(plugin,player,perk)) {
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
		            @Override
		            public void run() {
		            	try {
		            		PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("UPDATE "+plugin.getConexionDatabase().getTablePerks()+" SET Level=? WHERE (Name=? AND Perk=?)");
		    				statement.setInt(1, level);
		    				statement.setString(2, player);
		    				statement.setString(3, perk);
		    				statement.executeUpdate();
		        		} catch (SQLException e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		}
		            }
				});
			}else {
				crearJugadorPerkAsync(plugin, uuid, player, perk);
			}
				
		}
		
		public static JugadorDatos getJugador(PaintballBattle plugin,String name){
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Global_Data=1 AND Name=?)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){			
					int wins = resultado.getInt("Win");
					int loses = resultado.getInt("Lose");
					int ties = resultado.getInt("Tie");
					int kills = resultado.getInt("Kills");
					int coins = resultado.getInt("Coins");
					JugadorDatos p = new JugadorDatos(name,"",wins,loses,ties,kills,coins,null,null);
					return p;
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		public static ArrayList<JugadorDatos> getPlayerDataMonthly(PaintballBattle plugin){
			
			ArrayList<JugadorDatos> players = new ArrayList<JugadorDatos>();
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);
			int mes = calendar.get(Calendar.MONTH);
			int año = calendar.get(Calendar.YEAR);
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Year="+año+" AND Month="+mes+" AND Global_Data=0)");
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){
					String name = resultado.getString("Name");
					if(!yaContieneJugador(players,name)) {
						int[] stats = getStatsTotalesMonthly(plugin,name,mes,año);
						JugadorDatos p = new JugadorDatos(name,"",stats[0],stats[1],stats[2],stats[3],0,null,null);
						players.add(p);
					}	
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return players;
		}
		
		public static ArrayList<JugadorDatos> getPlayerDataWeekly(PaintballBattle plugin){
			
			ArrayList<JugadorDatos> players = new ArrayList<JugadorDatos>();
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);
			int mes = calendar.get(Calendar.MONTH);
			int año = calendar.get(Calendar.YEAR);
			int semana = calendar.get(Calendar.WEEK_OF_MONTH);
			
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Year="+año+" AND Month="+mes+" AND Week="+semana+" AND Global_Data=0)");
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){
					String name = resultado.getString("Name");
					if(!yaContieneJugador(players,name)) {
						int[] stats = getStatsTotalesWeekly(plugin,name,mes,año,semana);
						JugadorDatos p = new JugadorDatos(name,"",stats[0],stats[1],stats[2],stats[3],0,null,null);
						players.add(p);
					}	
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return players;
		}
		
		private static boolean yaContieneJugador(ArrayList<JugadorDatos> players,String player) {
			for(JugadorDatos p : players) {
				if(p.getName().equals(player)) {
					return true;
				}
			}
			return false;
		}
		
		public static int[] getStatsTotalesWeekly(PaintballBattle plugin, String name, int mes, int año, int semana){
			int[] cantidades = {0,0,0,0}; //Wins,Loses,Ties,Kills
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Name=? AND Year="+año+" AND Month="+mes+" AND Week="+semana+" AND Global_Data=0)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();
				
				while(resultado.next()){	
					cantidades[0] = cantidades[0]+resultado.getInt("Win");
					cantidades[1] = cantidades[2]+resultado.getInt("Lose");
					cantidades[2] = cantidades[2]+resultado.getInt("Tie");
					cantidades[3] = cantidades[3]+resultado.getInt("Kills");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cantidades;
		}
		
		public static int[] getStatsTotalesMonthly(PaintballBattle plugin, String name, int mes, int año){
			int[] cantidades = {0,0,0,0}; //Wins,Loses,Ties,Kills
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE (Name=? AND Year="+año+" AND Month="+mes+" AND Global_Data=0)");
				statement.setString(1, name);
				ResultSet resultado = statement.executeQuery();
				
				while(resultado.next()){	
					cantidades[0] = cantidades[0]+resultado.getInt("Win");
					cantidades[1] = cantidades[2]+resultado.getInt("Lose");
					cantidades[2] = cantidades[2]+resultado.getInt("Tie");
					cantidades[3] = cantidades[3]+resultado.getInt("Kills");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cantidades;
		}
		
		//Se cargan solo las globales
		public static ArrayList<JugadorDatos> getPlayerData(PaintballBattle plugin){
			ArrayList<JugadorDatos> players = new ArrayList<JugadorDatos>();
			try {
				PreparedStatement statement = plugin.getConexionDatabase().getConnection().prepareStatement("SELECT * FROM "+plugin.getConexionDatabase().getTablePlayers()+" WHERE Global_Data=1");
				ResultSet resultado = statement.executeQuery();	
				while(resultado.next()){			
					String name = resultado.getString("Name");
					if(!yaContieneJugador(players,name)) {
						int wins = resultado.getInt("Win");
						int loses = resultado.getInt("Lose");
						int ties = resultado.getInt("Tie");
						int kills = resultado.getInt("Kills");
						int coins = resultado.getInt("Coins");
						JugadorDatos p = new JugadorDatos(name,"",wins,loses,ties,kills,coins,null,null);
						players.add(p);
					}	
				}		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return players;
		}
}
