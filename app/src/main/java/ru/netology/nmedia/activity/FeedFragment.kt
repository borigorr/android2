package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.remove(post)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })
        binding.list.adapter = adapter
        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadPosts()
        }
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->

            viewModel.data.observe(viewLifecycleOwner) { state ->
                adapter.submitList(state.posts)

                binding.swiperefresh.isVisible = !state.empty
                binding.swiperefreshEmpty.isVisible = state.empty
                binding.emptyText.isVisible =  state.empty && !dataState.error && dataState.refreshing
            }
            binding.swiperefresh.isRefreshing = dataState.loading
            binding.swiperefreshEmpty.isRefreshing = dataState.loading

            binding.errorGroup.isVisible = dataState.error

            if (dataState.error) {
                Toast.makeText(activity, R.string.http_error, Toast.LENGTH_SHORT)
                    .show();
            }

        }

        binding.retryButton.setOnClickListener {

            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
