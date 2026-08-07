package com.example.jetpackcompose.game.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jetpackcompose.R
import com.example.jetpackcompose.databinding.FragmentChooseLevelBinding
import com.example.jetpackcompose.game.domain.entity.Level


class ChooseLevelFragment : Fragment() {
    private var _binding: FragmentChooseLevelBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentChooseLevelBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        // Создаём разметку фрагмента
        with(binding) {
            btnTest.setOnClickListener {
                launchGameFragment(Level.TEST)
            }
            btnEasy.setOnClickListener {
                launchGameFragment(Level.EASY)
            }
            btnMedium.setOnClickListener {
                launchGameFragment(Level.MEDIUM)
            }
            btnHard.setOnClickListener {
                launchGameFragment(Level.HARD)
            }
        }

        return binding.root
    }

    private fun launchGameFragment(level: Level) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main, GameFragment.newInstance(level))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChooseLevelFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}