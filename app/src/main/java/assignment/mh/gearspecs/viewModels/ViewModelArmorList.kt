package assignment.mh.gearspecs.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import assignment.mh.gearspecs.rest.RestHelper
import assignment.mh.gearspecs.rest.models.ArmorUnit

class ViewModelArmorList : ViewModel() {
    private val armorUnitsCallback = ArmorUnitsCallback()

    var allArmorUnits by mutableStateOf(listOf<ArmorUnit>())
    var filteredArmorUnits by mutableStateOf(listOf<ArmorUnit>())
    var filterText by mutableStateOf("")
    var dataRequestError : Throwable? by mutableStateOf(Throwable(""))
    var isLoading : Boolean by mutableStateOf(false)

    init {
        Log.d("atf", "View model created")
        requestFullList()
    }

    fun applyFilter(filterText:String) {
        this.filterText = filterText
        val filterTextLower = filterText.lowercase()
        filteredArmorUnits = allArmorUnits.filter { armorUnit ->
            armorUnit.name.lowercase().contains(filterTextLower)
        }
    }

    fun requestFullList() {
        dataRequestError = null
        isLoading = true
        RestHelper.getArmorUnits(armorUnitsCallback)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("atf", "ViewModel onCleared called")
    }

    protected fun finalize() {
        Log.d("atf", "ViewModel garbage collected")
    }

    private inner class ArmorUnitsCallback : RestHelper.Callback<List<ArmorUnit>> {

        override fun onResponse(result: List<ArmorUnit>?, error: Throwable?) {
            Log.d("atf", "Response received")
            isLoading = false
            if (result != null) {
                allArmorUnits = result
                applyFilter(filterText)
            } else {
                dataRequestError = error
            }
        }
    }
}