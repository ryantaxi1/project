package com.android.taxi1in_carapp.activity.ui.meter.tarif

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.databinding.FragmentTarifBinding

class TarifFragment : Fragment() {
    lateinit var binding: FragmentTarifBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTarifBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.tvRegular?.setOnClickListener {
            binding.tvRegular?.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tarif_selection)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.ivRegularSelection?.visibility = VISIBLE


            binding.tvMaxi?.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.ivMaxiSelection?.visibility = GONE
        }

        binding.tvMaxi?.setOnClickListener {
            binding.tvRegular?.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_edit_text_white)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_medium)
            }
            binding.ivRegularSelection?.visibility = GONE


            binding.tvMaxi?.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tarif_selection)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.euclid_square_bold)
            }
            binding.ivMaxiSelection?.visibility = VISIBLE
        }

    }


}