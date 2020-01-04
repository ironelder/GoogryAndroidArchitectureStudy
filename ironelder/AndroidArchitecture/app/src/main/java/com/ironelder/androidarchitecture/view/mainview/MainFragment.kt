package com.ironelder.androidarchitecture.view.mainview

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ironelder.androidarchitecture.R
import com.ironelder.androidarchitecture.common.BLOG
import com.ironelder.androidarchitecture.common.TYPE_KEY
import com.ironelder.androidarchitecture.component.CustomListViewAdapter
import com.ironelder.androidarchitecture.data.ResultItem
import com.ironelder.androidarchitecture.data.database.SearchResultDatabase
import com.ironelder.androidarchitecture.databinding.FragmentMainBinding
import com.ironelder.androidarchitecture.view.baseview.BaseFragment


class MainFragment :
    BaseFragment<MainContract.View, FragmentMainBinding, MainContract.Presenter>(R.layout.fragment_main),
    MainContract.View {

    override val presenter = MainPresenter()

    private val mType: String? by lazy {
        arguments?.getString(TYPE_KEY)
    }

    private var mSearchWord: String? = null

    override fun onDataChanged(result: ObservableArrayList<ResultItem>) {
        binding.items = result
    }

    override fun showErrorMessage(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            .show()
    }

    override fun showLoading() {
        binding.showProgress = true
    }

    override fun hideLoading() {
        binding.showProgress = false
    }

    override fun onLoadFromDatabase(searchWord: String, result: ObservableArrayList<ResultItem>) {
        mSearchWord = searchWord
        binding.items = result
    }

    override fun doViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mainViewModel = MainViewModel()
        with(binding.searchLayout.rvResultListView) {
            adapter =
                CustomListViewAdapter()
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager(context).orientation
                )
            )
        }
        binding.mainViewModel.let {
            it?.searchQuery?.addOnPropertyChangedCallback(object :
                Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    it.searchWithAdapter(
                        mType ?: BLOG,
                        it.searchQuery.get(),
                        SearchResultDatabase.getInstance(
                            context?.applicationContext ?: (activity as Context).applicationContext
                        )
                    )
                }
            })
        }
    }

    override fun doLoadFromDatabase() {
        binding.mainViewModel.let {
            it?.getSearchResultToRoom(
                mType ?: BLOG,
                SearchResultDatabase.getInstance(
                    context?.applicationContext ?: (activity as Context).applicationContext
                )
            )
        }
    }

    override fun doCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchView =
            SearchView((context as? MainActivity)?.supportActionBar?.themedContext ?: context)
        menu.findItem(R.id.action_search)?.apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mSearchWord = query ?: ""
                binding.mainViewModel.let {
                    it?.searchQuery?.set(query ?: "")
                }
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        searchView.setOnSearchClickListener {
            if (!binding.mainViewModel?.searchQuery?.get().isNullOrEmpty()) {
                searchView.setQuery(binding.mainViewModel?.searchQuery?.get(), false)
            }
        }
    }

    companion object {
        fun newInstance(type: String?): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply {
                    putString(TYPE_KEY, type)
                }
            }
        }
    }

}