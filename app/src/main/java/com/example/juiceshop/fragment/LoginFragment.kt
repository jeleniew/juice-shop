package com.example.juiceshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentLoginBinding
import com.example.juiceshop.utils.Authentication
import com.example.juiceshop.utils.PopUpManager

class LoginFragment : Fragment(){

    private var binding : FragmentLoginBinding? = null
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var forgotPasswordTextView : TextView
    private lateinit var loginButton : Button
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var loginWithGoogleButton: Button
    private lateinit var notCustomerTextView: TextView
    private lateinit var authentication: Authentication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        var root = binding!!.root
        emailEditText = root.findViewById(R.id.email)
        passwordEditText = root.findViewById(R.id.password)
        forgotPasswordTextView = root.findViewById(R.id.forgot_pass_view)
        loginButton = root.findViewById(R.id.login_button)
        rememberCheckBox = root.findViewById(R.id.checkBox)
        loginWithGoogleButton = root.findViewById(R.id.button2)
        notCustomerTextView = root.findViewById(R.id.not_customer)

        authentication = Authentication(requireContext())

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        loginButton.setOnClickListener {
            run {
                var email = emailEditText.text
                var password = passwordEditText.text
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    ApiManager.logIn(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString(),
                        rememberCheckBox.isChecked,
                        onSuccess = {
                            loginButton.post {
                                findNavController().popBackStack()
                            }
                        }, onError = {
                            activity?.runOnUiThread {
                                PopUpManager.showPopUp(
                                    requireContext(),
                                    it?: "",
                                    "Ok"
                                ) {
                                    Log.d("debug", "ok")
                                }
                            }
                        }
                    )
                } else {
                    if (email.isEmpty()) {
                        emailEditText.setHintTextColor(resources.getColor(R.color.red))
                    }
                    if (password.isEmpty()) {
                        passwordEditText.setHintTextColor(resources.getColor(R.color.red))
                    }
                }
            }
        }

        loginWithGoogleButton.setOnClickListener {
            authentication.loginWithBrowser(callback = { email ->
                if (email != null) {
                    var password = email.reversed()
//                     TODO: code password
                    ApiManager.register(email, password, password, onSuccess = {
                        Log.d("debug", "success")
                        loginButton.post {
                            findNavController().popBackStack()
                        }
                    }, onError = {error ->
//                        TODO: check the code of the response
                        if (error == "email must be unique") {
                            ApiManager.logIn(email, password, true, onSuccess = {
                                Log.d("debug", "success")
                                loginButton.post {
                                    findNavController().popBackStack()
                                }
                            }, onError = {
                                Log.d("debug", "failed: $it")
                            })
                        }
                    })
                } else {

                }
            })
        }

        notCustomerTextView.setOnClickListener {
            run {
                findNavController().navigate(
                    R.id.action_login_to_register,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.navigation_login, true).build())
            }
        }
        return root
    }

}