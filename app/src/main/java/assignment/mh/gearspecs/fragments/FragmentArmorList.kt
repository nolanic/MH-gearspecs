package assignment.mh.gearspecs.fragments

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import assignment.mh.gearspecs.Composables
import assignment.mh.gearspecs.InAppMessageDispatcher
import assignment.mh.gearspecs.R
import assignment.mh.gearspecs.rest.models.ArmorUnit
import assignment.mh.gearspecs.theme.Colors
import assignment.mh.gearspecs.viewModels.ViewModelArmorList

class FragmentArmorList : BaseFragment() {
    private lateinit var viewModel : ViewModelArmorList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModelArmorList::class.java)
    }

    @Composable
    override fun setContent() {
        Log.d("atf", "Content rendering")
        if (viewModel.dataRequestError != null) {
            //Retry data request dialog
            Composables.StandardDialog(title = "Data Request Error", content = viewModel.dataRequestError!!.message!!,
                cancelOnOutsideTouch = false,
                "Retry",
                onCancel = {
                    InAppMessageDispatcher.broadcastMessage(Message.CLOSE_REQUESTED)
                },
                onButtonClick = { index ->
                    viewModel.requestFullList()
                }
            )
        }

        Box(modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp).align(Alignment.Center),
                    color = Colors.color2
                )
            }

            Column {
                Text(
                    text = "Armor List",
                    fontSize = 30.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Colors.color2)
                        .padding(10.dp)
                        .fillMaxWidth(1f)
                )
                //Search bar
                TextField(value = viewModel.filterText,
                    onValueChange = { viewModel.applyFilter(it) },
                    modifier = Modifier.fillMaxWidth(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Unspecified,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    placeholder = {
                        Text(text = "Filter Armor", fontSize = 20.sp)
                    })
                //Armor list
                LazyColumn {
                    Log.d("atf", "Rendering list items")
                    items(viewModel.filteredArmorUnits.size) { index ->
                        ArmorItemUi(armorUnit = viewModel.filteredArmorUnits[index])
                    }
                }
            }
        }
    }
    
    @Composable
    private fun ArmorItemUi(armorUnit : ArmorUnit) {
        val textColor = Colors.color3
        Surface(color = Colors.color1,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(5.dp)) {
            Row(modifier = Modifier
                .fillMaxWidth(1f)
                .padding(bottom = 5.dp)) {
                Image(painter = painterResource(id = armorUnit.getTypeIconResourceId()),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp))
                Column(modifier = Modifier.fillMaxWidth(1f)) {
                    //Armor unit name
                    Text(text = armorUnit.name,
                        modifier = Modifier.padding(bottom = 3.dp),
                        fontSize = 25.sp,
                        color = textColor)
                    //Rank, defense and decor slots
                    Row(modifier = Modifier.fillMaxWidth(1f)) {
                        Text(text = armorUnit.rank.uppercase(),
                            modifier = Modifier.fillMaxWidth(0.33f),
                            color = textColor)
                        Row(modifier = Modifier.fillMaxWidth(0.5f)) {
                            Image(painter = painterResource(id = R.drawable.ic_shield),
                                contentDescription = null,
                                modifier = Modifier.size(15.dp))
                            Text(text = armorUnit.defense.base.toString(),
                                modifier = Modifier
                                    .padding(start = 3.dp)
                                    .fillMaxWidth(0.5f),
                                color = textColor)
                        }
                        for(slot in armorUnit.slots) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(end = 5.dp) ) {
                                Image(painter = painterResource(id = R.drawable.ic_deco),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp))
                                Text(text = slot.rank.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    enum class Message {
        CLOSE_REQUESTED
    }
}