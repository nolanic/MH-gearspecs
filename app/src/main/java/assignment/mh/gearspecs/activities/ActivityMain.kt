package assignment.mh.gearspecs.activities

import android.os.Bundle
import assignment.mh.gearspecs.R
import assignment.mh.gearspecs.fragments.FragmentArmorList


class ActivityMain : BaseActivity(R.layout.container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(FragmentArmorList(), false)
    }
}