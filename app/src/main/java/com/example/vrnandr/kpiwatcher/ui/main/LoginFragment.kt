package com.example.vrnandr.kpiwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vrnandr.kpiwatcher.R
import com.example.vrnandr.kpiwatcher.databinding.LoginFragmentBinding
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private var exit = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = LoginFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.successLogin.observe(viewLifecycleOwner,{
            binding.progressBar.visibility = View.GONE
            binding.login.isEnabled = true
            binding.loginEditText.isEnabled = true
            binding.passwordEditText.isEnabled = true
            if (exit){
                if (it){
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
            }
        })

        return binding.root
    }

    override fun onStart() {
        binding.login.setOnClickListener {
            exit = true
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString().toUpperCase(Locale.getDefault()).filter { !it.isWhitespace() }
            binding.passwordEditText.setText(password)

            binding.progressBar.visibility = View.VISIBLE
            binding.login.isEnabled = false
            binding.loginEditText.isEnabled = false
            binding.passwordEditText.isEnabled = false

            viewModel.login(login,password)

        }
        super.onStart()
    }
}