package com.example.juiceshop.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.juiceshop.R
import com.example.juiceshop.databinding.FragmentProfileBinding
import com.example.juiceshop.utils.ApiManager

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
        browseButton.setOnClickListener {
//            getContent.launch("image/*")

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
        return root
    }

//    private val viewModel: ProfileViewModel by viewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Handle the selected image URI
            // Do something with the selected image URI
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            // Handle the selected image here
            val selectedImageUri = data.data
            // Do something with the selected image URI
        }
    }
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class ProfileViewModel : ViewModel() {
//
//    // Zmienna przechowująca adres e-mail użytkownika
//    private val _email = MutableLiveData<String>()
//    val email: LiveData<String>
//        get() = _email
//
//    // Zmienna przechowująca obraz profilowy użytkownika
//    private val _profilePictureUri = MutableLiveData<String>()
//    val profilePictureUri: LiveData<String>
//        get() = _profilePictureUri
//
//    init {
//        // Domyślne ustawienia początkowe dla adresu e-mail i obrazu profilowego
//        _email.value = ""
//        _profilePictureUri.value = ""
//    }
//
//    // Metoda do ustawiania adresu e-mail
//    fun setEmail(email: String) {
//        _email.value = email
//    }
//
//    // Metoda do ustawiania obrazu profilowego
//    fun setProfilePictureUri(uri: String) {
//        _profilePictureUri.value = uri
//    }
//}

}