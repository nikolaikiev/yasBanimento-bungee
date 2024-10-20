package onion.yasara.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import onion.yasara.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Permissão para tá usando o comando
        if (!sender.hasPermission("yasbanimento.ban")) {
            sender.sendMessage(new TextComponent("§cVocê não tem permissão para usar esse comando."));
            return;
        }

        if (args.length < 3) {
            // Correção caso o player escreva errado o comando
            sender.sendMessage(new TextComponent("§cUso correto: /ban <jogador> <motivo> <tempo>"));
            return;
        }

        String targetName = args[0];
        String reason = args[1];
        String duration = args[2];

        long banTime = parseBanTime(duration);
        if (banTime == -1) {
            sender.sendMessage(new TextComponent("§cFormato de tempo inválido."));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetName);
        if (target != null) {
            String targetIp = target.getAddress().getAddress().getHostAddress();
            target.disconnect(new TextComponent(formatBanMessage(target.getName(), sender.getName(), reason, duration)));
            saveIpBan(targetIp, reason, banTime);
            sender.sendMessage(new TextComponent("§aJogador " + targetName + " banido com sucesso."));
        } else {
            sender.sendMessage(new TextComponent("§cJogador não encontrado."));
        }
    }

    private long parseBanTime(String time) {
        long duration;
        try {
            if (time.endsWith("d")) {
                duration = Long.parseLong(time.replace("d", "")) * 24 * 60 * 60 * 1000;
            } else if (time.endsWith("w")) {
                duration = Long.parseLong(time.replace("s", "")) * 7 * 24 * 60 * 60 * 1000;
            } else if (time.endsWith("m")) {
                duration = Long.parseLong(time.replace("m", "")) * 30 * 24 * 60 * 60 * 1000;
            } else if (time.equalsIgnoreCase("permanente")) {
                duration = -1; // Banimento permanente
            } else {
                return -1; // Formato inválido
            }
        } catch (NumberFormatException e) {
            return -1; // Formato inválido
        }
        return duration;
    }
    // Mensagem que vai aparecer quando o player for banindo
    private String formatBanMessage(String playerName, String staffName, String reason, String duration) {
        return String.join("\n",
                "§b§l NOME DO SERVIDOR",
                "",
                "§4§l BANIDO POR §4" + staffName,
                "",
                "§fMotivo §c: " + reason,
                "§fTempo §c: " + duration,
                "",
                "§c§lINFO §fCompre §cuban §fem: §cseu-servidor.com/loja");
    }

    private void saveIpBan(String ip, String reason, long duration) {
        long banTime = System.currentTimeMillis();

        try (Connection connection = Main.getInstance().getDatabaseManager().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO ip_bans (ip, reason, duration, ban_time) VALUES (?, ?, ?, ?)");
            stmt.setString(1, ip);
            stmt.setString(2, reason);
            stmt.setLong(3, duration);
            stmt.setLong(4, banTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
