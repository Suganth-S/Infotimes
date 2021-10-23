package com.suganth.infotimes.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.suganth.infotimes.R
import com.suganth.infotimes.adapter.NewsAdapter
import com.suganth.infotimes.ui.NewsActivity
import com.suganth.infotimes.ui.NewsViewModel
import com.suganth.infotimes.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import retrofit2.Response

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private val TAG = "Breaking News"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * we set that viewModel to our fragments activity and
         * we cast that as a NewsActivity , so we have access to viewmodel
         * created in that newsActivity and call that viewModel afterwards
         */
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
                R.id.action_breakingNewsFragment_to_articlesNewsFragment, bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
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
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}