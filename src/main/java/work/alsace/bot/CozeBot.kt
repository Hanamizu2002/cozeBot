package work.alsace.bot

import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import work.alsace.bot.api.CozeAPIClient
import work.alsace.bot.listeners.PlayerChatListener
import java.io.IOException
import java.util.*

class CozeBot : JavaPlugin() {
    val cozeAPIClient = config.getString("coze.token")?.let { CozeAPIClient(it) }

    override fun onEnable() {
        loadConfig()
        logger.info("CozeBot is enabled!")
        registerListeners(PlayerChatListener(this))
    }

    override fun onDisable() {
        logger.info("CozeBot is disabled!")
    }

    private fun registerCommand(cmd: String, executor: CommandExecutor) {
        cmd.let { getCommand(it)?.setExecutor(executor) }
    }

    private fun registerCommand(cmd: String, executor: TabExecutor) {
        Objects.requireNonNull(getCommand(cmd))?.setExecutor(executor)
        Objects.requireNonNull(getCommand(cmd))?.tabCompleter = executor
    }

    private fun registerListeners(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    @Throws(IOException::class)
    private fun loadConfig() {
        saveDefaultConfig()
        reloadConfig()
    }
}
