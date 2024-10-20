package onion.yasara.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import onion.yasara.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerBanListener implements Listener {

    public PlayerBanListener() {
        // Registra o listener
        ProxyServer.getInstance().getPluginManager().registerListener(Main.getInstance(), this);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (connection == null) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent("Conexão inválida."));
            return;
        }

        String ip = connection.getAddress().getAddress().getHostAddress();

        // Verifica se o jogador está banido pelo IP
        if (isIpBanned(ip)) {
            event.setCancelReason(new TextComponent(getIpBanMessage(ip))); // Mensagem de banimento por IP
            event.setCancelled(true); // Cancela a conexão
        }
    }

    private boolean isIpBanned(String ip) {
        return isUserBanned(ip, "ip_bans");
    }

    private boolean isUserBanned(String identifier, String tableName) {
        try (Connection connection = Main.getInstance().getDatabaseManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE ip = ?")) {
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long banTime = rs.getLong("ban_time");
                long duration = rs.getLong("duration");

                // Se a duração for -1, o banimento é permanente
                if (duration == -1 || System.currentTimeMillis() < banTime + duration) {
                    return true; // O jogador está banido
                } else {
                    // O banimento expirou, remove do banco de dados
                    removeBan(identifier, tableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Jogador não está banido
    }

    private String getIpBanMessage(String ip) {
        return getMessage(ip, "ip_bans", "§4§lBANIDO POR");
    }

    private String getMessage(String identifier, String tableName, String title) {
        try (Connection connection = Main.getInstance().getDatabaseManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE ip = ?")) {
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String reason = rs.getString("reason");
                long duration = rs.getLong("duration");
                long banTime = rs.getLong("ban_time");
                // Calcule o tempo restante
                long remainingTime = (banTime + duration) - System.currentTimeMillis();
                return formatBanMessage(title, reason, duration, remainingTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Você foi banido!";
    }

    private String formatBanMessage(String title, String reason, long duration, long remainingTime) {
        return String.join("\n",
                "§b§lNome do Servidor",
                "",
                title + " " + reason,
                "§fMotivo: §c" + reason,
                "§fTempo restante: §c" + formatDuration(remainingTime),
                "",
                "§c§lINFO §fCompre §cuban §fem: §cseu-servidor.com/loja");
    }

    private String formatDuration(long duration) {
        if (duration < 0) {
            return "Expirado";
        } else if (duration == -1) {
            return "Permanente";
        }
        long seconds = duration / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format("%dd %dh %dm", days, hours, minutes);
    }

    private void removeBan(String identifier, String tableName) {
        try (Connection connection = Main.getInstance().getDatabaseManager().getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE ip = ?")) {
            stmt.setString(1, identifier);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
