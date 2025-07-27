package pb.ajneb97.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pb.ajneb97.PaintballBattle;

public class ConexionDatabase {

	private Connection connection;
	private String host;
	private String database;
	private String username;
	private String password;
	private String tablePlayerdata;
	private String tablePlayerPerks;
	private String tablePlayerHats;
	private int port;
	
	public ConexionDatabase(FileConfiguration config){
		host = config.getString("mysql-database.host");
		port = Integer.valueOf(config.getString("mysql-database.port"));
		database = config.getString("mysql-database.database");		
		username = config.getString("mysql-database.username");
		password = config.getString("mysql-database.password");
		tablePlayerdata = "paintball_data";
		tablePlayerPerks = "paintball_perks";
		tablePlayerHats = "paintball_hats";
		mySqlAbrirConexion();
		MySQL.createTablePlayers(this);
		MySQL.createTablePerks(this);
		MySQL.createTableHats(this);
	}
	
	public String getTablePlayers(){
		return this.tablePlayerdata;
	}
	
	public String getTablePerks(){
		return this.tablePlayerPerks;
	}
	
	public String getTableHats(){
		return this.tablePlayerHats;
	}
	
	public String getDatabase() {
		return this.database;
	}
	
	private void mySqlAbrirConexion(){
		try {
			synchronized(this){
				if(getConnection() != null && !getConnection().isClosed()){
					Bukkit.getConsoleSender().sendMessage(PaintballBattle.prefix+ChatColor.RED + "Error while connecting to the Database.");
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.database,this.username,this.password));
				
				Bukkit.getConsoleSender().sendMessage(PaintballBattle.prefix+ChatColor.GREEN + "Successfully connected to the Database.");
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
