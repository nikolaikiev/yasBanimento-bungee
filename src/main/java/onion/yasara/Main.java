package onion.yasara;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import onion.yasara.database.MySQLManager;
import onion.yasara.commands.*;
import onion.yasara.listener.PlayerBanListener;

public class Main extends Plugin {
    private static Main instance;
    private MySQLManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        databaseManager = new MySQLManager();
        databaseManager.connect();
        getLogger().info("§aLigado com sucesso!");
        getProxy().getPluginManager().registerCommand(this, new BanCommand());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerBanListener());
    }

    @Override
    public void onDisable() {
        getLogger().info("§cDesligado com sucesso!");
        databaseManager.disconnect();
    }

    public static Main getInstance() {
        return instance;
    }

    public MySQLManager getDatabaseManager() {
        return databaseManager;
    }
}
