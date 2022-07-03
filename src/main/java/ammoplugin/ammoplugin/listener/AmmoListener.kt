package ammoplugin.ammoplugin.listener

import org.bukkit.Sound
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import ammoplugin.ammoplugin.AmmoAPI
import ammoplugin.ammoplugin.AmmoPlugin
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.sItem.SItem

class AmmoListener {

    init {
        val sEvent = SEvent(AmmoPlugin.plugin)
        sEvent.register(PlayerInteractEvent::class.java) { e ->
            val sItem = SItem(e.item?:return@register)
            if (sItem.getCustomData(AmmoPlugin.plugin,"ammopack", PersistentDataType.INTEGER) != null){
                val ammoPack = AmmoPlugin.api.getAmmoPack(sItem)?:return@register
                e.player.playSound(e.player.location,Sound.BLOCK_CHEST_OPEN,1f,1f)
                AmmoPlugin.api.openAmmoInv(e.player,e.item!!,ammoPack)
            }

        }
    }
}