package com.example.movietmdb.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movietmdb.R
import com.example.movietmdb.databinding.FragmentSeeAllBinding
import com.example.movietmdb.ui.adapter.ContentAdapterType1
import com.example.movietmdb.ui.adapter.ContentAdapterTypeAll
import com.example.movietmdb.ui.adapter.PeopleAdapterType1
import com.example.movietmdb.ui.adapter.PeopleAdapterTypeAll
import com.example.movietmdb.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class SeeAllFragment : Fragment() {
    private lateinit var binding : FragmentSeeAllBinding
    private lateinit var movieAdapter : ContentAdapterTypeAll
    private lateinit var tvSeriesAdapter : ContentAdapterTypeAll
    private lateinit var movieAdapterFirst : ContentAdapterType1
    private lateinit var tvSeriesAdapterFirst : ContentAdapterType1
    private lateinit var peopleAdapterFirst : PeopleAdapterType1
    private lateinit var peopleAdapter : PeopleAdapterTypeAll

    private val viewModel : HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentSeeAllBinding>(
            inflater,
            R.layout.fragment_see_all,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        val type = this.arguments?.getString("type") ?: ""
        val id = this.arguments?.getString("id")?.toInt()
        getData(type,id)
    }
    private fun onClickPeople(id : Int) {
        val bundle  = Bundle()
        bundle.putString("id", id.toString())
        findNavController().navigate(R.id.action_seeAllFragment_to_peopleDetailFragment,bundle)
    }
    private fun onClickMovie(id : Int) {
        val bundle  = Bundle()
        bundle.putString("id", id.toString())
        findNavController().navigate(R.id.action_seeAllFragment_to_movieDetailFragment,bundle)
    }
    private fun onClickTvSeries(id : Int) {
        val bundle  = Bundle()
        bundle.putString("id", id.toString())
        findNavController().navigate(R.id.action_seeAllFragment_to_tvSeriesDetailsFragment,bundle)
    }
    private fun initRv() {
        peopleAdapter = PeopleAdapterTypeAll(::onClickPeople)
        tvSeriesAdapterFirst = ContentAdapterType1(::onClickTvSeries)
        tvSeriesAdapter = ContentAdapterTypeAll(::onClickTvSeries)
        movieAdapterFirst = ContentAdapterType1(::onClickMovie)
        peopleAdapterFirst = PeopleAdapterType1(::onClickPeople)
        movieAdapter = ContentAdapterTypeAll(::onClickMovie)
        binding.allRv.adapter = movieAdapter
        binding.allRv.layoutManager = GridLayoutManager(binding.root.context,2)
    }
    private fun getData(type : String,id : Int? = null) {
        when(type) {
            "popular_movies" -> {
                viewModel.getPopularMoviesPaging(type).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "top_rated_movies" -> {
                viewModel.getTopRatedMoviesPaging(type).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "now_playing_movies" -> {
                viewModel.getNowPlayingMoviesPaging(type).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "upcoming_movies" -> {
                viewModel.getUpcomingMoviesPaging(type).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "similar_movies" -> {
                viewModel.getSimilarMoviePaging(type,id!!).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "recommended_movies" -> {
                viewModel.getRecommendedMoviePaging(type,id!!).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        movieAdapter.submitData(it)
                        movieAdapter.notifyDataSetChanged()
                    }
                })
            }
            "tv_series_cast" -> {
                binding.allRv.adapter = peopleAdapterFirst
                viewModel.getTvSeriesCast(id!!).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        peopleAdapterFirst.updateData(it.body()!!.cast)
                        peopleAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
            "movie_cast" -> {
                binding.allRv.adapter = peopleAdapterFirst
                viewModel.getMovieCast(id!!).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        peopleAdapterFirst.updateData(it.body()!!.cast)
                        peopleAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
            "artist_movies" -> {
                binding.allRv.adapter = movieAdapterFirst
                viewModel.getPeopleMovies(id!!).observe(viewLifecycleOwner,Observer{
                    if(it.isSuccessful && it.body() != null) {
                        movieAdapterFirst.updateData(it.body()!!.cast)
                        movieAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
            "artist_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapterFirst
                viewModel.getPeopleTvSeries(id!!).observe(viewLifecycleOwner,Observer{
                    if(it.isSuccessful && it.body() != null) {
                        tvSeriesAdapterFirst.updateData(it.body()!!.cast)
                        tvSeriesAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
            "popular_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getPopularTvSeriesPaging(type,null).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "top_rated_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getTopRatedTvSeriesPaging(type,null).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "on_the_air_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getOnTheAirTvSeriesPaging(type,null).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "airing_today_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getAiringTodayTvSeiresPaging(type,null).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "similar_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getSimilarTvSeriesPaging(type,id).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "recommended_tv_series" -> {
                binding.allRv.adapter = tvSeriesAdapter
                viewModel.getRecommendedTvSeriesPaging(type,id).observe(viewLifecycleOwner, Observer {
                    lifecycleScope.launch {
                        tvSeriesAdapter.submitData(it)
                        tvSeriesAdapter.notifyDataSetChanged()
                    }
                })
            }
            "popular_people" -> {
                binding.allRv.adapter = peopleAdapter
                viewModel.getTrendingPeoplePaging(type,id).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        peopleAdapter.submitData(it)
                        peopleAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
            "trending_people" -> {
                binding.allRv.adapter = peopleAdapter
                viewModel.getTrendingPeoplePaging(type,id).observe(viewLifecycleOwner,Observer{
                    lifecycleScope.launch {
                        peopleAdapter.submitData(it)
                        peopleAdapterFirst.notifyDataSetChanged()
                    }
                })
            }
        }
    }
}