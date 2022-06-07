package com.mqtt.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mqtt.MainViewModel
import com.mqtt.MessageAdapter
import com.mqtt.R
import com.mqtt.printToast
import kotlinx.android.synthetic.main.fragment_second.*

class SecondFragment : Fragment() {

    // ViewModel:
    private lateinit var viewModel: MainViewModel

    // RecyclerView:
    private lateinit var rv:RecyclerView
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(),defaultViewModelProviderFactory).get(
            MainViewModel::class.java)

        initRecyclerView()
        second_btn_save.setOnClickListener {
            if (!second_et_topic.text.isNullOrEmpty()) {
                viewModel.subscribe(second_et_topic.text.toString()) { status->
                    if(status)
                        printToast(requireContext(), "La sottoscrizione al Topic ha avuto successo")
                    else
                        printToast(requireContext(), "La sottoscrizione al Topic NON ha avuto successo")
                }
            }
        }

        second_btn_send.setOnClickListener {

            if (!second_et_publish.text.isNullOrEmpty() && !second_et_topic.text.isNullOrEmpty()) {
                viewModel.publish(second_et_topic.text.toString(), second_et_publish.text.toString()) { status->
                    if(status)
                        printToast(requireContext(), "La pubblicazione ha avuto successo")
                    else
                        printToast(requireContext(), "La pubblicazione NON ha avuto successo")
                }
            }
        }

        viewModel.getLiveMessages().observe(viewLifecycleOwner, Observer { messages ->
            adapter.updateContent(messages)
            rv.scrollToPosition(0)
        })


    }

    private fun initRecyclerView()
    {

        rv = second_rv
        adapter = MessageAdapter(ArrayList())
        val manager = LinearLayoutManager(rv.context, RecyclerView.VERTICAL, false)

        rv.layoutManager = manager
        rv.adapter = adapter

    }

}