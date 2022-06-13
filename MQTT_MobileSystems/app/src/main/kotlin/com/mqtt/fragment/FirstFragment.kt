package com.mqtt.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mqtt.MainViewModel
import com.mqtt.R
import com.mqtt.printToast
import kotlinx.android.synthetic.main.fragment_first.*

class FirstFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(),defaultViewModelProviderFactory).get(
            MainViewModel::class.java)


        first_btn_login.setOnClickListener {

            if(checkInputs()) {
                connectToBroker()
            } else {
                printToast(requireContext(), "Si prega di compilare tutti i campi...")
            }

        }

    }

    private fun connectToBroker()
    {
        showViews(first_pb, first_tv_pb)
        blockInputs()

        //IP Adress, port, serverUri, clientID:
        val ip = "tcp://"+first_et_ip.text.toString()
        val port = first_et_port.text.toString()
        val serverUri = "$ip:$port"
        val clientID = "MobyleSystemsMQTT"
        viewModel.initClient(serverUri, clientID)

        val pwd = first_et_pwd.text.toString()
        val username = first_et_name.text.toString()
        viewModel.connectClient(username,pwd) { status->

            if(status) {
                findNavController().navigate(R.id.action_first_secpnd)
            } else {
                printToast(requireContext(), "Impossibile stabilire una connessione...")

                first_btn_login.setOnClickListener {
                    if(checkInputs()) {
                        connectToBroker()
                    } else {
                        printToast(requireContext(), "Si prega di compilare tutti i campi...")
                    }
                }
            }

            unblockInputs()
            hideViews(first_pb,first_tv_pb)

        }

    }


    private fun blockInputs()
    {
        first_et_pwd.isEnabled = false
        first_et_name.isEnabled = false
        first_et_port.isEnabled = false
        first_et_ip.isEnabled = false
        first_btn_login.isEnabled = false

    }

    private fun unblockInputs()
    {
        first_et_pwd.isEnabled = true
        first_et_name.isEnabled = true
        first_et_port.isEnabled = true
        first_et_ip.isEnabled = true
        first_btn_login.isEnabled = true
    }


    private fun checkInputs():Boolean
    {
        if(first_et_ip.text.isNullOrEmpty())
            return false
        if(first_et_port.text.isNullOrEmpty())
            return false
        if(first_et_name.text.isNullOrEmpty())
            return false
        if(first_et_pwd.text.isNullOrEmpty())
            return false
        return true
    }


    private fun hideViews(vararg views: View)
    {
        for (view in views) {
            view.visibility = View.GONE
        }
    }

    private fun showViews(vararg views: View)
    {
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }
}