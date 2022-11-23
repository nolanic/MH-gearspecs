package assignment.mh.gearspecs

/** This is class is designed to provide communication between different elements of this App (Fragments, Activities, objects... whatever),
 * in a loosed coupled way*/
object InAppMessageDispatcher {
    private val callbacks  = mutableSetOf<Callback>()

    /**
     * @return true if the callback successfully registered, false if callback already exists
     */
    fun register(callback: Callback) : Boolean {
        return callbacks.add(callback)
    }

    /**
     * @return true if callback successfully unregistered, false if callback doesn't exist
     */
    fun unregister(callback: Callback) : Boolean {
        return callbacks.remove(callback)
    }

    fun broadcastMessage(message:Any, senderId:String? = null) {
        for (callback in callbacks) {
            callback.onMessage(senderId, message)
        }
    }

    interface Callback {
        fun onMessage(senderId: String?, message:Any)
    }
}