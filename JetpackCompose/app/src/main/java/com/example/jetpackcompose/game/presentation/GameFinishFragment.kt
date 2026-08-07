package com.example.jetpackcompose.game.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jetpackcompose.databinding.FragmentGameFinishBinding
import com.example.jetpackcompose.game.domain.entity.GameResult


class GameFinishFragment : Fragment() {

    private var gameResult: GameResult? = null

    private var _binding: FragmentGameFinishBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameResult = it.getParcelable(GAME_RESULT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameFinishBinding.inflate(inflater, container, false)
        // Создаём разметку фрагмента
        binding.btnRetryGame.setOnClickListener {
            retryGame()
        }
        return binding.root
    }

    private fun retryGame() {
//TODO реализовать
    }

    companion object {
        private const val GAME_RESULT = "gameResult"

        @JvmStatic
        fun newInstance(gameResult: GameResult) =
            GameFinishFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(GAME_RESULT, gameResult)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}