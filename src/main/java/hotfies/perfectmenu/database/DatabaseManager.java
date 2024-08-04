package hotfies.perfectmenu.database;

import hotfies.perfectmenu.PerfectMenu;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

public class DatabaseManager {

    private final PerfectMenu plugin;
    private Connection connection;
    private final Logger logger;

    public DatabaseManager(PerfectMenu plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public void connectToDatabase() {
        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String database = plugin.getConfig().getString("database.name");
        String username = plugin.getConfig().getString("database.username");
        String password = plugin.getConfig().getString("database.password");

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            logger.info("Connected to MySQL database!");
        } catch (SQLException e) {
            logger.severe("Could not connect to MySQL database!");
            e.printStackTrace();
        }
    }

    public void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPlayerLanguage(UUID playerUUID) {
        String query = "SELECT lang FROM player_settings WHERE player_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("lang");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "En_en"; // default language
    }
}