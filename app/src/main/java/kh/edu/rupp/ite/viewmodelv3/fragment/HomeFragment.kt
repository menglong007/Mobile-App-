package kh.edu.rupp.ite.viewmodelv3.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.viewmodelv2.view_model.HomeViewModel
import kh.edu.rupp.ite.viewmodelv2.view_model.ProfileViewModel
import kh.edu.rupp.ite.viewmodelv3.activity.FormActivity
import kh.edu.rupp.ite.viewmodelv3.R
import kh.edu.rupp.ite.viewmodelv3.adapter.HomeAdapter
import kh.edu.rupp.ite.viewmodelv3.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.viewmodelv3.model.HomeModel
import kh.edu.rupp.ite.viewmodelv3.state.EState

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homeState.observe(viewLifecycleOwner) { state ->
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

        viewModelProfile.loadProfile(requireContext())

        val searchText = arguments?.getString("search_query").orEmpty() // Ensure `searchText` is non-null
        if (searchText.isNotBlank()) {
            viewModel.loadData(searchText, "", false)
        } else {
            viewModel.loadData("", "", false)
        }

        var isDateSortDescending = true
         var isNameSortDescending = true

        binding.sortItem.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.sort_menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.itemByDate -> {
                        viewModel.loadData("", "created", isDateSortDescending)
                        isDateSortDescending = !isDateSortDescending
                        true
                    }

                    R.id.itemByName -> {
                        viewModel.loadData("", "Title", isNameSortDescending)
                        isNameSortDescending = !isNameSortDescending
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }


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
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    val searchText = searchEditText.text.toString()
                    viewModel.loadData(searchText, "", false)
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


    private fun setupRecyclerView(items: List<HomeModel>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HomeAdapter(items)
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

