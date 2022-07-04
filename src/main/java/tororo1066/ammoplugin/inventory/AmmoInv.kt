package tororo1066.ammoplugin.inventory

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.ammoplugin.AmmoPlugin
import tororo1066.ammoplugin.data.AmmoPack
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sItem.SItem.Companion.toBase64Items

class AmmoInv(private val itemStack: ItemStack, private val ammoPack: AmmoPack): SInventory(AmmoPlugin.plugin,"弾薬メニュー",ammoPack.invSize) {
    override fun renderMenu(): Boolean {
        ammoPack.ammoList.forEachIndexed { index, itemStack ->
            setItem(index,itemStack)
        }

        setOnClick {
            val item = it.currentItem?:return@setOnClick
            val temp = AmmoPlugin.ammoTemplate[ammoPack.template]
            if (temp == null){
                it.isCancelled = true
                return@setOnClick
            }

            val data = temp.find { f ->
                val name = if (f.name == "_") item.itemMeta.displayName else f.name
                if (item.itemMeta.hasCustomModelData()){
                    f.type == item.type && name == item.itemMeta.displayName && f.cmd == item.itemMeta.customModelData
                } else {
                    f.type == item.type && name == item.itemMeta.displayName && f.cmd == 0
                }

            }

            if (data == null){
                it.isCancelled = true
                return@setOnClick
            }

        }

        setOnClose {
            val meta = itemStack.itemMeta
            val list = mutableListOf<ItemStack>()

            if (!it.inventory.isEmpty){
                for (i in 0 until ammoPack.invSize*9){
                    val item = it.inventory.getItem(i)?:continue
                    if (item.type.isAir)continue
                    list.add(item)
                }
                meta.persistentDataContainer.set(NamespacedKey(AmmoPlugin.plugin,"base64"), PersistentDataType.STRING,list.toBase64Items())
            }

            itemStack.itemMeta = meta
        }
        return true
    }
}