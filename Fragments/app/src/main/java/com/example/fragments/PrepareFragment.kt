package com.example.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: переименовать аргументы — подобрать имена, совпадающие с
// параметрами инициализации фрагмента, например ARG_ITEM_NUMBER
private const val ARG_APP_NAME = "param1"
private const val ARG_ACTION_NAME = "param2"

/**
 * Простой наследник [Fragment].
 * Используйте фабричный метод [PrepareFragment.newInstance], чтобы
 * создать экземпляр этого фрагмента.
 */
class PrepareFragment : Fragment() {
    // TODO: переименовать параметры и поменять их типы
    private var appName: String? = null
    private var actionName: String? = null

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreate", "PrepareFragment")
        arguments?.let {
            appName = it.getString(ARG_APP_NAME)
            actionName = it.getString(ARG_ACTION_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаём разметку фрагмента
        return inflater.inflate(R.layout.fragment_prepare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById<TextView>(R.id.prepare_text_view)
        textView.text = "$appName is preparing for $actionName..."
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrepareFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APP_NAME, param1)
                    putString(ARG_ACTION_NAME, param2)
                }
            }
    }
}