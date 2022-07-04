package tororo1066.ammoplugin

import org.bukkit.Material
import tororo1066.ammoplugin.command.AmmoCommand
import tororo1066.ammoplugin.data.TemplateData
import tororo1066.ammoplugin.listener.AmmoListener
import tororo1066.tororopluginapi.SJavaPlugin

class AmmoPlugin: SJavaPlugin() {

    companion object{
        lateinit var plugin: AmmoPlugin
        lateinit var api: AmmoAPI
        val ammoTemplate = HashMap<String,ArrayList<TemplateData>>()
        const val prefix = "§f[§b§lAmmo§e§lPlugin§f]§r"
    }

    override fun onEnable() {
        plugin = this
        saveDefaultConfig()
        reload()
        api = AmmoAPI()
        AmmoListener()
    }

    fun reload(){
        reloadConfig()

        val keys = config.getConfigurationSection("templateData")
        keys?.getKeys(false)?.forEach {
            ammoTemplate[it] = arrayListOf()
            val dataKeys = keys.getConfigurationSection(it)?:return@forEach
            dataKeys.getKeys(false).forEach second@{  s ->
                val typeKeys = dataKeys.getConfigurationSection(s)?:return@second
                typeKeys.getKeys(false).forEach { s2 ->
                    val data = TemplateData()
                    data.type = Material.getMaterial(s)?:Material.STONE
                    data.name = s2
                    data.cmd = typeKeys.getInt(s2)
                    ammoTemplate[it]!!.add(data)
                }
            }
        }

        AmmoCommand()
    }

}