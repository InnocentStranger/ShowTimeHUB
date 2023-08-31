package com.example.movietmdb.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movietmdb.R
import com.example.movietmdb.databinding.FragmentMovieDetailBinding
import com.example.movietmdb.ui.adapter.ContentAdapterType2
import com.example.movietmdb.ui.adapter.GenreAdapterTypeBox
import com.example.movietmdb.ui.adapter.PeopleAdapterType1
import com.example.movietmdb.ui.viewmodel.HomeViewModel
import okhttp3.internal.notify

class MovieDetailFragment : Fragment() {
    private lateinit var binding : FragmentMovieDetailBinding
    private lateinit var genreAdapter : GenreAdapterTypeBox
    private lateinit var castAdapter : PeopleAdapterType1
    private lateinit var similarContentAdapter : ContentAdapterType2
    private lateinit var recommendedContentAdapter : ContentAdapterType2
    private val viewModel : HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_movie_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieId = viewModel.contentIdLiveData.value
        initRv()
        if(movieId != null) getData(movieId)
    }
    private fun onClickContent(id : Int) {
        viewModel.updateContentId(id)
        findNavController().navigate(R.id.action_movieDetailFragment_self)
    }
    private fun initRv() {
        genreAdapter = GenreAdapterTypeBox()
        binding.genreRv.adapter = genreAdapter
        binding.genreRv.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)

        castAdapter = PeopleAdapterType1()
        binding.castRv.adapter = castAdapter
        binding.castRv.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)

        similarContentAdapter = ContentAdapterType2(::onClickContent)
        binding.similarRv.adapter = similarContentAdapter
        binding.similarRv.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)

        recommendedContentAdapter = ContentAdapterType2(::onClickContent)
        binding.recommendedRv.adapter = recommendedContentAdapter
        binding.recommendedRv.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun getData(movieId : Int) {
        viewModel.getMovieDetails(movieId).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful && it.body() != null) {
                val uri = "https://image.tmdb.org/t/p/w500/" + it.body()!!.posterPath
                Glide.with(binding.backgroundImage.context)
                    .load(uri)
                    .into(binding.backgroundImage)

                Glide.with(binding.image.context)
                    .load(uri)
                    .into(binding.image)

                binding.title.text = it.body()!!.title
                binding.description.text = it.body()!!.overview
                binding.votes.text = it.body()!!.voteCount.toString()
                binding.ratingBar.rating = it.body()!!.voteAverage.toFloat()

                genreAdapter.updateGenre(it.body()!!.genres)
                genreAdapter.notifyDataSetChanged()
            }

        })

        viewModel.getMovieCast(movieId).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful && it.body() != null) {
                castAdapter.updateData(it.body()!!.cast)
                castAdapter.notifyDataSetChanged()
            }
        })

        viewModel.getSimilarMovieFirstPage(movieId).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful && it.body() != null) {
                similarContentAdapter.updateData(it.body()!!.itemList)
                similarContentAdapter.notifyDataSetChanged()
            }
        })

        viewModel.getRecommendedMovieFirstPage(movieId).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful && it.body() != null) {
                recommendedContentAdapter.updateData(it.body()!!.itemList)
                recommendedContentAdapter.notifyDataSetChanged()
            }
        })
    }
}