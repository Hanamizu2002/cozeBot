package work.alsace.bot.listeners

import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import work.alsace.bot.CozeBot
import java.util.regex.Pattern

class PlayerChatListener(plugin: CozeBot) : Listener {
    private val config = plugin.config
    private val cozeAPIClient = plugin.cozeAPIClient
    private val pluginPrefix = config.getString("coze.pluginPrefix")?.let { translateHexColorCodes(it) }

    @EventHandler
    fun onPlayerAsyncChat(e: AsyncPlayerChatEvent) {
        var prefix = config.getString("coze.prefix")
        if (prefix.isNullOrEmpty()) {
            prefix = "?"
        }
        if (e.message.startsWith(prefix)) {
            val question = e.message.substringAfter(prefix).trim()
            if (question != "") {
                e.isCancelled = true
                e.player.sendMessage(pluginPrefix + translateHexColorCodes("#FFFFFF正在思考..."))
                val result = coze(e, question)
                e.player.sendMessage(translateHexColorCodes("#9AE7A1$result"))
            } else {
                e.player.sendMessage("$pluginPrefix 用法：`? [问题]` - 提问, 如`? 如何打开夜视模式`")
            }
        }
    }

    private fun coze(event: PlayerEvent, msg: String): String {
        try {
            val response = cozeAPIClient?.sendMessage(
                config.getString("coze.botid"),
                event.player.name,
                msg,
                false,
                null
            )
            return response?.let { cozeAPIClient?.getAnswerContent(it) } ?: "未获取到回复，请联系管理员解决！"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "未获取到回复，请联系管理员解决！"
    }

    private fun translateHexColorCodes(message: String): String {
        val hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})")
        val matcher = hexPattern.matcher(message)
        val buffer = StringBuilder(message.length + 32)
        while (matcher.find()) {
            val group = matcher.group(1)
            matcher.appendReplacement(
                buffer,
                "§x§" + group[0] + "§" + group[1] + "§" + group[2] + "§" + group[3] + "§" + group[4] + "§" + group[5]
            )
        }
        return matcher.appendTail(buffer).toString()
    }

}
