package kh.edu.rupp.ite.viewmodelv3.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.viewmodelv2.view_model.SavedViewModel
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.activity.FormActivity
import kh.edu.rupp.ite.viewmodelv3.adapter.SaveAdapter
import kh.edu.rupp.ite.viewmodelv3.databinding.FragmentSaveBinding
import kh.edu.rupp.ite.viewmodelv3.helper.SharedPreferencesHelper
import kh.edu.rupp.ite.viewmodelv3.model.SavedModel
import kh.edu.rupp.ite.viewmodelv3.state.EState

class SaveFragment: Fragment() {

    private lateinit var binding: FragmentSaveBinding;
    private val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.savedState.observe(viewLifecycleOwner) { state ->
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
            val id = profile.userId.toString()
            Log.d("ProfileId" ,"$id")
            viewModel.loadData(id);
        } else {
            Toast.makeText(requireContext(), "No profile data found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView(items: List<SavedModel>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SaveAdapter(items , viewModel = viewModel)
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
    }

    private fun showError(message: String) {
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }
}