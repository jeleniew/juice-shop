package com.example.juiceshop.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.juiceshop.R
import com.example.juiceshop.activity.MainActivity
import com.example.juiceshop.databinding.FragmentProfileBinding
import com.example.juiceshop.utils.ApiManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class ProfileFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private var binding : FragmentProfileBinding? = null
    private lateinit var profilePicture : ImageView
    private lateinit var emailEditText : EditText
    private lateinit var usernameEditText : EditText
    private lateinit var setUsernameButton : Button
    private lateinit var browseButton: Button
    private lateinit var noFileText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        var root = binding!!.root
        profilePicture = root.findViewById(R.id.profilePicture)
        emailEditText = root.findViewById(R.id.emailText)
        usernameEditText = root.findViewById(R.id.usernameText)
        setUsernameButton = root.findViewById(R.id.setUsernameButton)
        browseButton = root.findViewById(R.id.browseButton)
        noFileText = root.findViewById(R.id.noFileText)

        Thread {
            ApiManager.requestProfileData(callback = { email, profileImage ->
                Log.d("debug", "image: $profileImage")
                ApiManager.requestPicture(profileImage, callback = {
                    activity?.runOnUiThread {
                        Log.d("debug", "image: ${it?: "is null"}")
                        profilePicture.setImageBitmap(it)
                        emailEditText.setText(email)
                        emailEditText.keyListener = null
                    }
                })
            })
        }.start()

        setUsernameButton.setOnClickListener {
            var username = usernameEditText.text.toString()
            Log.d("debug", "username: $username")
            ApiManager.setUserName(
                username,
                object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        var json = response.body?.string()
                        if (!response.isSuccessful) {
                            Log.d("debug", "responseCode for username: ${response.code}")
//                            TODO("Not yet implemented")
                        }
                        (requireActivity() as MainActivity).hideKeyboard()
                        activity?.runOnUiThread {
                            (requireActivity() as MainActivity).clearFocusFromAllViews(usernameEditText)
                        }
                    }

                })
        }

        browseButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            // Handle the selected image here
            val selectedImageUri = data.data
            // Do something with the selected image URI
        }
    }

}