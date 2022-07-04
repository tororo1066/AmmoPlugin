package tororo1066.ammoplugin.command

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType
import tororo1066.ammoplugin.AmmoPlugin
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class AmmoCommand: SCommand("ammo","","ammo.op") {

    init {
        clearCommands()
        addCommand(SCommandObject()
            .addArg(SCommandArg().addAllowString("create"))
            .addArg(SCommandArg().addAllowString(Material.values().map { it.name.toLowerCase() }.toTypedArray()))
            .addArg(SCommandArg().addAllowType(SCommandArgType.STRING).addAlias("アイテム名"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.STRING).addAlias("ロール(\\nで改行できます)"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("cmd"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("invの大きさ(1~6)"))
            .addArg(SCommandArg().addAllowType(SCommandArgType.STRING).addAlias("テンプレート").addAllowString(AmmoPlugin.ammoTemplate.keys.toTypedArray()))
            .setExecutor(Consumer<SCommandOnlyPlayerData> {
                val type = Material.getMaterial(it.args[1].toUpperCase())!!
                val name = it.args[2]
                val lore = it.args[3].split("\\n")
                val cmd = it.args[4].toInt()
                val row = it.args[5].toInt()
                val template = it.args[6]

                if (row !in 1..6){
                    it.sender.sendMessage(AmmoPlugin.prefix + "§c大きさは1から6でしてください")
                    return@Consumer
                }

                val sItem = SItem(type)
                sItem.setDisplayName(name)
                sItem.setLore(lore)
                sItem.setCustomModelData(cmd)
                sItem.setCustomData(AmmoPlugin.plugin,"ammopack", PersistentDataType.INTEGER,1)
                sItem.setCustomData(AmmoPlugin.plugin,"invsize", PersistentDataType.INTEGER,row)
                sItem.setCustomData(AmmoPlugin.plugin,"template", PersistentDataType.STRING,template)

                it.sender.inventory.setItemInMainHand(sItem)

                it.sender.sendMessage(AmmoPlugin.prefix + "giveしました")
            }))

        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("reload"))
            .setExecutor(Consumer<SCommandData> {
                AmmoPlugin.plugin.reload()
                it.sender.sendMessage(AmmoPlugin.prefix + "§aリロードしました")
            }))
    }
}