package assignment.mh.gearspecs.fragments

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import assignment.mh.gearspecs.Composables
import assignment.mh.gearspecs.InAppMessageDispatcher
import assignment.mh.gearspecs.R
import assignment.mh.gearspecs.rest.ImageLoader
import assignment.mh.gearspecs.rest.models.ArmorUnit
import assignment.mh.gearspecs.theme.Colors
import assignment.mh.gearspecs.viewModels.ViewModelArmorList

class FragmentArmorList : BaseFragment() {
    private lateinit var viewModel : ViewModelArmorList
    private val painterProvider = mutableMapOf<Int, Painter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModelArmorList::class.java)
    }

    @Composable
    private fun getPainter(resourceId : Int) : Painter {
        var result : Painter? = painterProvider.get(resourceId)
        if (result == null) {
            result = painterResource(id = resourceId)
            painterProvider.put(resourceId, result)
        }
        return result
    }

    @Composable
    override fun setContent() {
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

        Box(modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
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
                    items(viewModel.filteredArmorUnits.size) { index ->
                        val armorUnit = viewModel.filteredArmorUnits[index]
                        ArmorItemUi(armorUnit)
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
                LoadableImage(imageUrl = armorUnit.getImageUrl(), size = 60.dp)
                Column(modifier = Modifier.fillMaxWidth(1f)) {
                    Row {
                        Image(
                            painter = getPainter(armorUnit.getTypeIconResourceId()),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 5.dp)
                                .align(Alignment.CenterVertically)
                        )
                        //Armor unit name
                        Text(text = armorUnit.name,
                            modifier = Modifier.padding(bottom = 3.dp),
                            fontSize = 25.sp,
                            color = textColor)    
                    }
                    //Rank, defense and decor slots
                    Row(modifier = Modifier.fillMaxWidth(1f)) {
                        Text(text = armorUnit.rank.uppercase(),
                            modifier = Modifier.fillMaxWidth(0.33f),
                            color = textColor)
                        Row(modifier = Modifier.fillMaxWidth(0.5f)) {
                            Image(painter = getPainter(R.drawable.ic_shield),
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
                                Image(painter = getPainter(R.drawable.ic_deco),
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

    @Composable
    private fun LoadableImage(imageUrl:String?, size: Dp) {
        Box(modifier = Modifier
            .size(size)
            .padding(5.dp)) {
            if (imageUrl != null) {
                val uiRefresher = remember { mutableStateOf(0) }
                uiRefresher.value // Pretending that we read the value for a very important thing here
                val image = ImageLoader.get(imageUrl, ImageCallback(uiRefresher))
                if (image != null) {
                    Image(
                        bitmap = image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(1f)
                    )
                }
            } else {
                Text(text = "Null\nUrl",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    maxLines = 2,
                    modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Preview
    @Composable
    fun ArmourItemUiPreview() {
        ArmorItemUi(ArmorUnit())
    }

    private inner class ImageCallback(val refreshTrigger:MutableState<Int>) : ImageLoader.Callback {

        override fun onResult(url: String, image: ImageBitmap?, httpCode: Int) {
            refreshTrigger.value++ //A hack to force a UI refresh. This value has no practical purpose
        }
    }

    enum class Message {
        CLOSE_REQUESTED
    }
}