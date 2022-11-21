package assignment.mh.gearspecs.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import assignment.mh.gearspecs.rest.RestHelper
import assignment.mh.gearspecs.rest.models.ArmorUnit

class ViewModelArmorList : ViewModel() {
    var allArmorUnits by mutableStateOf(listOf<ArmorUnit>())
    var filteredArmorUnits by mutableStateOf(listOf<ArmorUnit>())
    var filterText by mutableStateOf("")

    private val armorUnitsCallback = ArmorUnitsCallback()

    init {
        Log.d("atf", "View model created")
        RestHelper.getArmorUnits(armorUnitsCallback)
    }

    fun applyFilter(filterText:String) {
        this.filterText = filterText
        val filterTextLower = filterText.lowercase()
        filteredArmorUnits = allArmorUnits.filter { armorUnit ->
            armorUnit.name.lowercase().contains(filterTextLower)
        }
    }

    protected fun finalize() {
        Log.d("atf", "ViewModel garbage collected")
    }

    private inner class ArmorUnitsCallback : RestHelper.Callback<List<ArmorUnit>> {

        override fun onResponse(result: List<ArmorUnit>?, error: Throwable?) {
            Log.d("atf", "Response received")
            if (result != null) {
                allArmorUnits = result
                applyFilter(filterText)
            }
        }
    }
}