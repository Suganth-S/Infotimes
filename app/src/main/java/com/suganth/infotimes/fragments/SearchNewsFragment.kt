package com.suganth.infotimes.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.suganth.infotimes.R
import com.suganth.infotimes.adapter.NewsAdapter
import com.suganth.infotimes.ui.NewsActivity
import com.suganth.infotimes.ui.NewsViewModel
import com.suganth.infotimes.util.Constants
import com.suganth.infotimes.util.Constants.Companion.SEARCH_NEWS_DELAY
import com.suganth.infotimes.util.Resource
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {
    /**
     * instead of adding this in every fragment, we can create a base abstract frgment class,
     * and use that with each classes to infer
     */
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val TAG = "Search News"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        /**
         * through save args of navigation component , we can pass the selected item
         * from recyclerview to web view, so that the transition from one fragment
         * to another fragment has been done by using safe-args
         */
        newsAdapter.setOnItemClickListeners {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articlesNewsFragment, bundle
            )
        }


        /**
         * we handle search functionality here,so whenever we type something ,by the delay we add
         * which make search request on every single letter as like in instagram search
         * and that is done easily by using a coroutine job
         */
        var job: Job? = null
        etSearch.addTextChangedListener {editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty())
                    {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
                response -> when(response)
        {
            is Resource.Success -> {
                hideProgressBar()
                response.data.let {
                        newsResponse ->
                    if (newsResponse != null) {
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
            }
            is Resource.Error ->{
                hideProgressBar()
                response.message?.let {
                        message -> Log.e(TAG, "An error occured: $message")
                }
            }
            is Resource.Loading -> {
                showProgressBar()
            }
        }
        })
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}