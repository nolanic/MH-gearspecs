package assignment.mh.gearspecs.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import assignment.mh.gearspecs.R

//Common Activity behaviour will go here, all activities should be an implementation of this class
abstract class BaseActivity : AppCompatActivity {
    constructor():super()
    constructor(layoutId:Int) : super(layoutId)

    protected fun showFragment(fragment: Fragment, addToBackStack:Boolean = true, containerId:Int = R.id.frm_rootContainer) {
        val currentFragment = supportFragmentManager.findFragmentById(containerId)
        if (currentFragment != null) {
            if (currentFragment::class == fragment::class) {
                return
            }
        }

        val transaction = supportFragmentManager.beginTransaction()

        if (currentFragment != null) {
           transaction.remove(currentFragment)
        }
        transaction.add(containerId, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment::class.qualifiedName)
        }
        transaction.commit()
    }
}