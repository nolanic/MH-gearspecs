package assignment.mh.gearspecs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import assignment.mh.gearspecs.theme.Colors

class Composables {
    private constructor()

    companion object {
        @Composable
        fun StandardDialog(title:String = "", content:String ="", cancelOnOutsideTouch:Boolean = true, vararg buttons:String,
                onCancel:()->Unit = {}, onButtonClick:(buttonIndex:Int)->Unit = {}) {
            AlertDialog(onDismissRequest = { onCancel() },
                title = {
                    Box(modifier = Modifier.fillMaxWidth(1f)) {
                        Text(text = title, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                    }
                },
                text = {
                    Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center){
                        Text(text = content, fontSize = 15.sp)
                    }
                },
                buttons = {
                    Row(modifier = Modifier.fillMaxWidth(1f).padding(bottom = 10.dp), horizontalArrangement = Arrangement.Center) {
                        for (i in buttons.indices) {
                            Text(text = buttons[i],
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .padding(end = 10.dp, start = 10.dp)
                                    .background(Colors.color1, CircleShape)
                                    .padding(end = 10.dp, start = 10.dp)
                                    .clickable { onButtonClick(i) }
                            )
                        }
                    }
                },
                properties = DialogProperties(dismissOnClickOutside = cancelOnOutsideTouch)
            )
        }
    }
}