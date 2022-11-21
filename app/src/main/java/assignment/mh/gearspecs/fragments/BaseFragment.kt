package assignment.mh.gearspecs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import assignment.mh.gearspecs.R
import assignment.mh.gearspecs.activities.BaseActivity

//Common fragment behaviour should go here, all fragments will implement this class
abstract class BaseFragment : Fragment() {

    @Composable
    protected abstract fun setContent()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frm_base, container, false) as ComposeView
        rootView.setContent {
            setContent()
        }
        return rootView
    }
}