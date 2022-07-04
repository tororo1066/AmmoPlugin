package tororo1066.ammoplugin

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.ammoplugin.data.AmmoPack
import tororo1066.ammoplugin.inventory.AmmoInv
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.tororopluginapi.sItem.SItem.Companion.toBase64Items
import tororo1066.tororopluginapi.sItem.SItem.Companion.toItems

class AmmoAPI {

    fun getAmmoPacks(p: Player): List<AmmoPack> {
        val contents = p.inventory.contents.filterNotNull().filter { it.itemMeta.persistentDataContainer.has(NamespacedKey(AmmoPlugin.plugin,"ammopack"),
            PersistentDataType.INTEGER) }
        if (contents.isEmpty())return listOf()
        val packs = ArrayList<AmmoPack>()
        contents.forEach {
            packs.add(getAmmoPack(it)?:return@forEach)
        }

        return packs
    }

    fun getAmmoPack(item: ItemStack): AmmoPack? {
        val sItem = SItem(item)
        if (sItem.getCustomData(AmmoPlugin.plugin,"ammopack", PersistentDataType.INTEGER) == null)return null
        val packData = AmmoPack()
        packData.invSize = sItem.getCustomData(AmmoPlugin.plugin,"invsize", PersistentDataType.INTEGER)?:return null
        val items = sItem.getCustomData(AmmoPlugin.plugin,"base64", PersistentDataType.STRING)?.toItems()
        if (items != null){
            packData.ammoList.addAll(items)
        }
        packData.template = sItem.getCustomData(AmmoPlugin.plugin,"template", PersistentDataType.STRING)?:return null
        packData.itemStack = item

        return packData
    }

    fun getHandAmmoPack(p: Player, hand: EquipmentSlot): AmmoPack? {
        return getAmmoPack(p.inventory.getItem(hand)?:return null)
    }

    fun openAmmoInv(p: Player, item: ItemStack,ammoPack: AmmoPack){
        AmmoInv(item,ammoPack).open(p)
    }

    fun removeAmmo(p: Player, type: Material, name: String, cmd: Int, amount: Int): Boolean {
        var changeableAmount = amount
        val packs = getAmmoPacks(p)

        for (ammoPack in packs){

            for (it in ammoPack.ammoList){
                val sItem = SItem(it)
                val itemCmd = if (!sItem.itemMeta.hasCustomModelData()){
                    0
                } else {
                    sItem.getCustomModelData()
                }
                val ignoreItemName = name == "_"
                if (sItem.type == type && itemCmd == cmd){
                    if (!ignoreItemName){
                        if (sItem.getDisplayName() != name)continue
                    }
                    if (changeableAmount < sItem.amount){
                        changeableAmount = 0
                        break
                    } else {
                        changeableAmount -= it.amount
                    }
                }
            }
        }

        if (changeableAmount != 0){
            return false
        }

        changeableAmount = amount

        for (ammoPack in packs){

            for (it in ammoPack.ammoList){
                val sItem = SItem(it)
                val itemCmd = if (!sItem.itemMeta.hasCustomModelData()){
                    0
                } else {
                    sItem.getCustomModelData()
                }
                val ignoreItemName = name == "_"
                if (sItem.type == type && itemCmd == cmd){
                    if (!ignoreItemName){
                        if (sItem.getDisplayName() != name)continue
                    }
                    if (changeableAmount < sItem.amount){
                        it.amount -= changeableAmount
                        changeableAmount = 0
                        break
                    } else {
                        changeableAmount -= it.amount
                        it.amount = 0
                    }
                }
            }
            ammoPack.ammoList.removeIf { it.type.isAir }

            val meta = ammoPack.itemStack.itemMeta
            meta.persistentDataContainer.set(NamespacedKey(AmmoPlugin.plugin,"base64"), PersistentDataType.STRING,ammoPack.ammoList.toBase64Items())
            ammoPack.itemStack.itemMeta = meta

            if (changeableAmount == 0)break

        }

        return true
    }

    fun removeAmmo(item: ItemStack, ammoPack: AmmoPack, type: Material, name: String, cmd: Int, amount: Int): Boolean {

        var changeableAmount = amount

        for (it in ammoPack.ammoList){
            val sItem = SItem(it)
            val itemCmd = if (!sItem.itemMeta.hasCustomModelData()){
                0
            } else {
                sItem.getCustomModelData()
            }
            val ignoreItemName = name == "_"
            if (sItem.type == type && itemCmd == cmd){
                if (!ignoreItemName){
                    if (sItem.getDisplayName() != name)continue
                }
                if (changeableAmount < sItem.amount){
                    changeableAmount = 0
                    break
                } else {
                    changeableAmount -= it.amount
                }
            }
        }

        if (changeableAmount != 0){
            return false
        }

        changeableAmount = amount

        for (it in ammoPack.ammoList){
            val sItem = SItem(it)
            val itemCmd = if (!sItem.itemMeta.hasCustomModelData()){
                0
            } else {
                sItem.getCustomModelData()
            }
            val ignoreItemName = name == "_"
            if (sItem.type == type && itemCmd == cmd){
                if (!ignoreItemName){
                    if (sItem.getDisplayName() != name)continue
                }
                if (changeableAmount < sItem.amount){
                    it.amount -= changeableAmount
                    break
                } else {
                    changeableAmount -= it.amount
                    it.amount = 0
                }
            }
        }

        val meta = item.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(AmmoPlugin.plugin,"base64"), PersistentDataType.STRING,ammoPack.ammoList.toBase64Items())
        item.itemMeta = meta

        return true

    }




}