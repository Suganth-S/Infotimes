package com.suganth.infotimes.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.suganth.infotimes.R
import com.suganth.infotimes.adapter.NewsAdapter
import com.suganth.infotimes.ui.NewsActivity
import com.suganth.infotimes.ui.NewsViewModel
import com.suganth.infotimes.util.Constants.Companion.QUERY_PAGE_SIZE
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
                            // list differ wont work with mutable list , so we convert it into
                                // Normal list
                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                            val totalPages = newsResponse.totalResults/ QUERY_PAGE_SIZE +2
                            isLastPage = viewModel.breakingNewsPage == totalPages
                            if (isLastPage)
                            {
                                rvBreakingNews.setPadding(0,0,0,0)
                            }
                        }
                    }
                }
            is Resource.Error ->{
                hideProgressBar()
                response.message?.let {message->
                    Toast.makeText(activity, "An error occured : $message",Toast.LENGTH_LONG).show()
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
        isLoading = true
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    /**
     * pagination for loading new pages , when we reach end of the activity
     */
    var isLoading= false
    var isLastPage = false
    var isScrolling = false

    var scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            /**
             * there is no default way to find that we are at end page and we need to implement a next page
             * to do that we manually did it with help of LayoutManager
             */
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isScrolling
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val istotalMorethanVisible = totalCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && istotalMorethanVisible && isScrolling
            if(shouldPaginate)
            {
                viewModel.getBreakingNews("us")
                isLoading = false
            }
        }
    }
}