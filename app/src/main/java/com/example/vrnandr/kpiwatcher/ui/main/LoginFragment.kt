package com.example.vrnandr.kpiwatcher.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.databinding.LoginFragmentBinding
import com.skydoves.balloon.*
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var loginHint: Balloon
    private lateinit var passwordHint: Balloon
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = LoginFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.successLoginEvent.observe(viewLifecycleOwner,{
            binding.progressBar.visibility = View.GONE
            binding.login.isEnabled = true
            binding.loginEditText.isEnabled = true
            binding.passwordEditText.isEnabled = true
            if (it){
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }

        })

        loginHint = createBalloon(requireContext()) {
            setArrowSize(10)
            setWidth(190)
            setHeight(50)
            setArrowPosition(0.5f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setCornerRadius(4f)
            setAlpha(0.9f)
            setText(resources.getString(R.string.login_hint))
            setTextColor(Color.DKGRAY)
            //setTextIsHtml(true)
            setBackgroundColorResource(R.color.teal_200)
            //setOnBalloonClickListener(onBalloonClickListener)
            setOnBalloonOutsideTouchListener { _, _ ->  loginHint.dismiss()}
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
            setAutoDismissDuration(3000L)
        }

        passwordHint = createBalloon(requireContext()) {
            setArrowSize(10)
            setWidth(170)
            setHeight(50)
            setArrowPosition(0.5f)
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setCornerRadius(4f)
            setAlpha(0.9f)
            setText(resources.getString(R.string.password_hint))
            setTextColor(Color.DKGRAY)
            //setTextIsHtml(true)
            setBackgroundColorResource(R.color.teal_200)
            //setOnBalloonClickListener(onBalloonClickListener)
            setOnBalloonOutsideTouchListener { _, _ ->  passwordHint.dismiss()}
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
            setAutoDismissDuration(3000L)
        }


        return binding.root
    }

    override fun onStart() {
        binding.login.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString().toUpperCase(Locale.getDefault()).filter { !it.isWhitespace() }
            binding.passwordEditText.setText(password)

            binding.progressBar.visibility = View.VISIBLE
            binding.login.isEnabled = false
            binding.loginEditText.isEnabled = false
            binding.passwordEditText.isEnabled = false

            viewModel.login(login,password)

        }
        binding.loginHintButton.setOnClickListener {
            loginHint.showAlignTop(it)
        }
        binding.passwordHintButton.setOnClickListener {
            passwordHint.showAlignTop(it)
        }

        super.onStart()
    }
}