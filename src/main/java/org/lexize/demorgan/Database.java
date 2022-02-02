package org.lexize.demorgan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.sql.*;

public class Database {
    Connection connection;
    Statement statement;
    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));

        statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS 'demorgan' (" +
                "uuid TEXT," +
                "reason TEXT," +
                "until NUMBER)");
    }

    public boolean IsAnyPlayerInDemorgan() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT (count(*) > 0) FROM demorgan");
            if (resultSet.next()) {
                boolean anyPlayerInDemorgan = resultSet.getBoolean(1);
                resultSet.close();
                return anyPlayerInDemorgan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean IsPlayerInDemorgan(String uuid) {
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT (count(*) > 0) FROM demorgan WHERE uuid = '%s'", uuid));
            if (resultSet.next()) {
                boolean isPlayerInDemorgan = resultSet.getBoolean(1);
                resultSet.close();
                return isPlayerInDemorgan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public long GetPlayersUntil(String uuid) {
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT until FROM demorgan WHERE uuid = '%s'", uuid));
            if (resultSet.next()) {
                long time = resultSet.getLong("until");
                resultSet.close();
                return time;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String GetDemorganReason(String uuid) {
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT reason FROM demorgan WHERE uuid = '%s'", uuid));
            if (resultSet.next()) {
                String reason = resultSet.getString("until");
                resultSet.close();
                return reason;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void PutPlayerInDemorgan(String uuid, String reason, long timeout) {
        try {
            statement.execute(String.format("INSERT INTO demorgan (uuid, reason, until) VALUES ('%s', '%s', %s)", uuid, reason, timeout));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void TakeoutPlayerFromDemorgan(String uuid) {
        try {
            statement.execute(String.format("DELETE FROM demorgan WHERE uuid = '%s'", uuid));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
