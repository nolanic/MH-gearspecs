package assignment.mh.gearspecs.rest.models

import assignment.mh.gearspecs.R

class ArmorUnit {
    val id = -1
    val name = ""
    val type = ""
    val rank = ""
    val rarity = -1
    val slots = listOf<Slot>()
    val defense = Defense()
    val resistances = Resistances()
    val skills = listOf<Skill>()
    val assets = Assets()
    val crafting = Crafting()

    fun getTypeIconResourceId() : Int {
        when(type) {
            "head" -> return R.drawable.ic_head
            "deco" -> return R.drawable.ic_deco
            "gloves" -> return R.drawable.ic_gloves
            "legs" -> return R.drawable.ic_legs
            "shield" -> return R.drawable.ic_shield
            "waist" -> return R.drawable.ic_waist
            "chest" -> return R.drawable.ic_chest
        }
        return R.drawable.ic_deco
    }
}