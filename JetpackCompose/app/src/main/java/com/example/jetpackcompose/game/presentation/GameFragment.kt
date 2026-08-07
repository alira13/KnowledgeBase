package com.example.jetpackcompose.game.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackcompose.R
import com.example.jetpackcompose.databinding.FragmentGameBinding
import com.example.jetpackcompose.game.domain.entity.GameResult
import com.example.jetpackcompose.game.domain.entity.GameSettings
import com.example.jetpackcompose.game.domain.entity.Level
import com.example.jetpackcompose.game.presentation.viewModel.GameViewModel
import com.example.jetpackcompose.game.presentation.viewModel.GameViewModelFactory


class GameFragment : Fragment() {

    private val viewModelFactory by lazy {
        GameViewModelFactory(requireActivity().application, level)
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[GameViewModel::class.java]
    }

    private lateinit var level: Level

    private var _binding: FragmentGameBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            level = it.getParcelable(ARG_LEVEL) ?: throw RuntimeException("Unknown level")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаём разметку фрагмента
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.ivMaxSumValue.setOnClickListener {
            launchGameFinishFragment(
                GameResult(
                    true, 0, 0,
                    GameSettings(0, 0, 0, 0)
                )
            )
        }
        observeViewModel()
        return binding.root
    }

    private fun launchGameFinishFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main, GameFinishFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        viewModel.question.observe(viewLifecycleOwner) {
            binding.ivMaxSumValue.text = it.sum.toString()
            binding.ivVisibleSumValue.text = it.visibleNumber.toString()
        }
    }

    companion object {
        private const val ARG_LEVEL = "level"

        @JvmStatic
        fun newInstance(level: Level) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_LEVEL, level)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}