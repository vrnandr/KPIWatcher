package com.example.vrnandr.kpiwatcher.ui.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.vrnandr.kpiwatcher.databinding.LoginFragmentBinding
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val viewModel: LoginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private var callbacks: Callbacks? = null
    private var exit = false

    companion object {
        fun newInstance() = LoginFragment()
    }

    interface Callbacks {
        fun onLogin()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
                    callbacks?.onLogin()
                } else {
                    Toast.makeText(activity,"Incorrect username or password", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return binding.root
    }

    override fun onStart() {
        binding.login.setOnClickListener {
            exit = true
            //val login = binding.loginEditText.text.toString().padStart(10,'0')
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString().toUpperCase(Locale.getDefault()).filter { !it.isWhitespace() }
            binding.loginEditText.setText(login)
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