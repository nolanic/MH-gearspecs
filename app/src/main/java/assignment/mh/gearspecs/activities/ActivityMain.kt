package assignment.mh.gearspecs.activities

import android.os.Bundle
import assignment.mh.gearspecs.InAppMessageDispatcher
import assignment.mh.gearspecs.R
import assignment.mh.gearspecs.fragments.FragmentArmorList


class ActivityMain : BaseActivity(R.layout.container) {

    private val dispatcherCallback = MessageDispatcherCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(FragmentArmorList(), false)

        InAppMessageDispatcher.register(dispatcherCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppMessageDispatcher.unregister(dispatcherCallback)
    }

    private inner class MessageDispatcherCallback : InAppMessageDispatcher.Callback {

        override fun onMessage(senderId: String?, message: Any) {
            if (message is FragmentArmorList.Message) {
                when(message) {
                    FragmentArmorList.Message.CLOSE_REQUESTED -> finish()
                    //More message types could go down here
                }
            }
        }
    }
}