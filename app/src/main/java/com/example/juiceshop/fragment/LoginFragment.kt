package com.example.juiceshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentLoginBinding

class LoginFragment : Fragment(){

    private var binding : FragmentLoginBinding? = null
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var forgotPasswordTextView : TextView
    private lateinit var loginButton : Button
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var loginWithGoogleButton: Button
    private lateinit var notCustomerTextView: TextView

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
                            showPopUp(it)
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

    private fun showPopUp(text: String?) {
        // TODO
    }

}