package com.example.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class ReadyFragment : Fragment() {

    interface StatusChangedListener {
        fun onStatusChanged(status:String)
    }

    private lateinit var noButton: Button
    private lateinit var yesButton: Button
    private lateinit var textView: TextView

    private lateinit var statusChangedListener: StatusChangedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is StatusChangedListener){
            statusChangedListener = context
        }
        else {
            throw RuntimeException("Activity must implement StatusChangedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreate", "ReadyFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаём разметку фрагмента
        return inflater.inflate(R.layout.fragment_ready, container, false)
    }

    //Точно знаем, что view уже создана
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        yesButton = view.findViewById(R.id.yes_button)
        noButton = view.findViewById(R.id.no_button)

        textView = view.findViewById(R.id.prepare_text_view)
        textView.text = "Are you ready?"
        yesButton.setOnClickListener({
            textView.text = "Nice!"
            statusChangedListener.onStatusChanged("Ready...")
        })
        noButton.setOnClickListener({
            textView.text = "Ooops..."
            statusChangedListener.onStatusChanged("Not ready...")
        })
    }

}