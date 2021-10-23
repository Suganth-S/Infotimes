package com.suganth.infotimes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.suganth.infotimes.R
import com.suganth.infotimes.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

/**
 * in norma terms we use list of items and use notifyDataSetChanged() to keep refresh and update
 * but that is inefficient , the recycler view will always updates its whole items even the
 * items that didn't changed, to solve that we are using diffutil, where DiffUtil calculate the
 * difference b/w the two list and enables us to only update those items that were different,
 * and another advantage is its happen in the background , so we don't block our main thread with that
 */
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val diffCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * AsyncListDiffer - is one which compare two list, if there is any change update that
     * particular list
     */
    val differ = AsyncListDiffer(this,diffCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnItemClickListeners{
                onItemClickListeners?.let { it(article) }
            }
        }
    }

    public fun setOnItemClickListeners(listener: (Article) -> Unit) {
       onItemClickListeners = listener
    }

    private var onItemClickListeners: ((Article) -> Unit)? = null

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}