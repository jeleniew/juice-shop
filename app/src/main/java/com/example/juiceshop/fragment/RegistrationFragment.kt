package com.example.juiceshop.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.juiceshop.utils.ApiManager
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentRegistrationBinding
import com.jaredrummler.materialspinner.MaterialSpinner


class RegistrationFragment : Fragment() {

    private var binding : FragmentRegistrationBinding? = null
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordInfoText: TextView
    private lateinit var repeatPasswordTextView: TextView
    private lateinit var doNotMatchTextView: TextView
    private lateinit var answerTextView: TextView
    private lateinit var showPasswordSwitch: Switch
    private lateinit var registerButton: Button
    private lateinit var alreadyCustomerTextView: TextView
    private lateinit var questionMaterialSpinner: MaterialSpinner

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        var root = binding!!.root
        emailEditText = root.findViewById(R.id.email)
        passwordEditText = root.findViewById(R.id.password)
        passwordInfoText = root.findViewById(R.id.passwdInfo)
        repeatPasswordTextView = root.findViewById(R.id.repeatPassword)
        doNotMatchTextView = root.findViewById(R.id.passwordsNotMatch)
        answerTextView = root.findViewById(R.id.answer)
        showPasswordSwitch = root.findViewById(R.id.showPassword)
        questionMaterialSpinner = root.findViewById(R.id.materialSpinner)
        registerButton = root.findViewById(R.id.registerButton)
        alreadyCustomerTextView = root.findViewById(R.id.alreadyCustomerTextView)

        passwordEditText.setOnFocusChangeListener { view, focus ->
            run {
                var password = passwordEditText.text.toString()
                if(password.length in 5..40) {
                    passwordEditText.setTextColor(resources.getColor(R.color.white))
                    passwordInfoText.setTextColor(resources.getColor(R.color.grey))

                } else if (!focus) {
                    passwordEditText.setTextColor(resources.getColor(R.color.red))
                    passwordInfoText.setTextColor(Color.RED)
                } else {
                    passwordEditText.setTextColor(resources.getColor(R.color.white))
                }
            }
        }

        repeatPasswordTextView.setOnFocusChangeListener { view, focus ->
            run {
                var password = passwordEditText.text.toString()
                var repeatPassword = repeatPasswordTextView.text.toString()
                if(password != repeatPassword && !focus) {
                    repeatPasswordTextView.setTextColor(resources.getColor(R.color.red))
                    doNotMatchTextView.visibility = View.VISIBLE
                } else if (!focus) {
                    repeatPasswordTextView.setTextColor(resources.getColor(R.color.white))
                    doNotMatchTextView.visibility = View.INVISIBLE
                } else if (focus) {
                    repeatPasswordTextView.setTextColor(resources.getColor(R.color.white))
                }
            }
        }

        repeatPasswordTextView.setOnKeyListener { _, _, _ ->
            if(doNotMatchTextView.visibility == View.VISIBLE) {
                if (passwordEditText.text.toString() == repeatPasswordTextView.text.toString()) {
                    doNotMatchTextView.visibility = View.INVISIBLE
                }
            }
            false
        }

        showPasswordSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            // TODO: showPasswordAdvice
            run {
                if(checked) {
                    passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                }
            }
        }

        ApiManager.getSecurityQuestions{ questionList  ->
            run {
                if (questionList == null) {
                    activity?.runOnUiThread { questionMaterialSpinner.visibility = View.VISIBLE }
                } else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1, questionList)
                    activity?.runOnUiThread { questionMaterialSpinner.setAdapter(adapter) }
                }
            }
        }

        registerButton.setOnClickListener {
            run {
                var date = Calendar.DATE.toString()
                val selectedOption = questionMaterialSpinner.text.toString()
                var email = emailEditText.text.toString()
                var password = passwordEditText.text.toString()
                var repeatPassword = repeatPasswordTextView.text.toString()
                var securityQuestion = ApiManager.createSecurityQuestionJson(
                    questionMaterialSpinner.id + 1, selectedOption, date)
                var answer = answerTextView.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()) {
                    ApiManager.register(
                        email,
                        password,
                        repeatPassword,
                        securityQuestion,
                        answer,
                        onSuccess =  {
                            registerButton.post {
                                findNavController().popBackStack()
                            }
                        }, onError =  {
                            showPopUp(it)
                        }
                    )
                } else {
                    if (email == "") {
                        emailEditText.setHintTextColor(resources.getColor(R.color.red))
                    }
                    if (password == "") {
                        passwordEditText.setHintTextColor(resources.getColor(R.color.red))
                    }
                    if (repeatPassword == "") {
                        repeatPasswordTextView.setHintTextColor(resources.getColor(R.color.red))
                    }
                    if (answer == "") {
                        answerTextView.setHintTextColor(resources.getColor(R.color.red))
                    }
                }
            }
        }

        alreadyCustomerTextView.setOnClickListener {
            run {
                findNavController().navigate(R.id.action_register_to_login,
                null,
                NavOptions.Builder().setPopUpTo(R.id.navigation_register, true).build())
            }
        }

        return root
    }

    private fun showPopUp(text: String?) {
        Log.d("debug", "show popup: $text")
        // TODO
    }
}