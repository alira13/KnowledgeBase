package print_lifecycle

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fragments.R
import print_lifecycle.PrintLifecycleActivity2.Companion.LIFECYCLE_DEBUG

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PrintLifecycleFragment1 : Fragment() {
    // TODO: переименовать параметры и поменять их типы
    private var param1: String? = null
    private var param2: String? = null

    override fun onAttach(context: Context) {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onAttach")
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onCreate")
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onCreateView")
        // Создаём разметку фрагмента
        return inflater.inflate(R.layout.activity_print_fragment_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(LIFECYCLE_DEBUG, ">>> PrintLifecycleFragment1 onDetach")
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrintLifecycleFragment1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}