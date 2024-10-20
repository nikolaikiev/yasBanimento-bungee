package onion.yasara.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLManager {
    private HikariDataSource dataSource;

    public void connect() {
        HikariConfig config = new HikariConfig();
        // Endereço até o banco de dados
        config.setJdbcUrl("jdbc:mysql://localhost:3306/ban");
        // Nome do usuário
        config.setUsername("root");
        // Senha do usuário
        config.setPassword("vertrigo");
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);  // 30 segundos
        config.setIdleTimeout(600000);        // 10 minutos
        config.setMaxLifetime(1800000);       // 30 minutos

        dataSource = new HikariDataSource(config);
        createTables();  // Cria as tabelas
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    private void createTables() {
        // Apenas a tabela de banimento por IP
        String createIpBansTableSQL = "CREATE TABLE IF NOT EXISTS ip_bans ("
                + "ip VARCHAR(45) NOT NULL,"
                + "reason VARCHAR(255),"
                + "duration BIGINT,"
                + "ban_time BIGINT,"
                + "PRIMARY KEY (ip))";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // Executa SQL para criar a tabela de ip_bans
            statement.execute(createIpBansTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveIpBan(String ip, String reason, long duration) {
        String sql = "INSERT INTO ip_bans (ip, reason, duration, ban_time) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE reason = ?, duration = ?, ban_time = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ip);
            stmt.setString(2, reason);
            stmt.setLong(3, duration);
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setString(5, reason);  // Atualiza o motivo
            stmt.setLong(6, duration);   // Atualiza a duração
            stmt.setLong(7, System.currentTimeMillis()); // Atualiza o tempo do banimento
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
