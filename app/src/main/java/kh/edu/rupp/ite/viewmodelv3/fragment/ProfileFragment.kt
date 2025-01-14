package kh.edu.rupp.ite.viewmodelv3.fragment

import android.app.Dialog
import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.viewmodelv2.model.ProfileModel
import kh.edu.rupp.ite.viewmodelv2.view_model.PostViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.activity.FormActivity
import kh.edu.rupp.ite.viewmodelv3.adapter.PostAdapter
import kh.edu.rupp.ite.viewmodelv3.databinding.FragmentProfileBinding
import kh.edu.rupp.ite.viewmodelv3.dialog.LogOutDialogFragment
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.PostModel
import kh.edu.rupp.ite.viewmodelv3.state.EState

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.postState.observe(viewLifecycleOwner) { state ->
            when (state.state) {
                EState.loading -> showLoading()
                EState.success -> {
                    hideLoading()
                    if (state.data.isNullOrEmpty()) {
                        showError("No Record.")
                    } else {
                        setupRecyclerView(state.data!!)
                    }
                }
                EState.error -> {
                    hideLoading()
                    showError("Failed to load data.")
                }
            }
        }
        viewModel.loadData()

        loadProfileFromLocalStorage()


        binding.onAdd.setOnClickListener {
            val intent = Intent(requireContext(), FormActivity::class.java)
            startActivity(intent)
        }

        binding.onSearch.setOnClickListener {
            val dialog = Dialog(requireContext(), R.style.Theme_FullScreenDialog)
            dialog.setContentView(R.layout.search_bar)

            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            val searchEditText = dialog.findViewById<EditText>(R.id.searchEditText)
            val closeButton: ImageView = dialog.findViewById(R.id.onBack)

            searchEditText.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    val searchText = searchEditText.text.toString()
                    showFragment(HomeFragment(), searchText)
                    dialog.dismiss()
                    true
                } else {
                    false
                }
            }

            closeButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        binding.logOut.setOnClickListener {
            val logOutDialog = LogOutDialogFragment()
            logOutDialog.show(parentFragmentManager, "LogOutDialogFragment")
        }
    }

    private fun showFragment(fragment: Fragment, searchText: String? = null) {
        val bundle = Bundle().apply {
            putString("search_query", searchText)
        }
        fragment.arguments = bundle

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun loadProfileFromLocalStorage() {
        val profile = SharedPreferencesHelper.getProfile(requireContext())
        if (profile != null) {
            showData(profile)
        } else {
            Toast.makeText(requireContext(), "No profile data found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showData(profile: ProfileModel) {
        binding.username.text = profile.username
        binding.email.text = profile.email
    }


    private fun setupRecyclerView(items: List<PostModel>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PostAdapter(items ,requireActivity() ,viewModel)
        }
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.loadingIndicator.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
        binding.recyclerView.visibility = View.GONE
    }
}
