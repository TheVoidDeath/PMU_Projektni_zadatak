package com.example.pmuprojektnizadatak.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmuprojektnizadatak.R
import com.example.pmuprojektnizadatak.data.Container
import com.example.pmuprojektnizadatak.data.model.Basket
import com.example.pmuprojektnizadatak.databinding.FragmentNotificationsBinding
import com.example.pmuprojektnizadatak.ui.dashboard.DashboardFragment

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var recyclerView= root.findViewById<RecyclerView>(R.id.notification_RecyclerView);
        recyclerView.setLayoutManager(LinearLayoutManager(root.context));
        val recyclerView_Adapter= NotificationsFragment.RecyclerViewAdapter(root.context, activity,this)
        recyclerView.setAdapter(recyclerView_Adapter);
        setTotals()


        val Buy_Button=root.findViewById<TextView>(R.id.notification_BuyButton)

        Buy_Button.requestFocus()
        Buy_Button.setOnClickListener {
            if(Container.LoggedInUser!!.checkData() && Container.CurrentBasket.Shoping_List.size>0)
            {
                Container.UsersList.AllUsers.find { it.UserName.compareTo(Container.LoggedInUser!!.UserName)==0 }!!.Add_Basket_ToHystory(Container.CurrentBasket)
                Container.SaveLoadedUserList(root.context)


                Container.CurrentBasket= Basket(mutableListOf())

                setTotals()
                recyclerView_Adapter.notifyDataSetChanged()
            }
            else
            {
                Buy_Button.setError("You need to set your shipping information before making a purchase \nAnd/Or\nYou can't buy an empty basket")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public fun setTotals()
    {
        val PriceLBL=binding.root.findViewById<TextView>(R.id.notification_CenaLBL)
        val EtaLBL=binding.root.findViewById<TextView>(R.id.notification_ETA_LBL)

        PriceLBL.setText("Ukupna cena svega jeste "+Container.CurrentBasket.getTotalPrice().toString())
        EtaLBL.setText("Sve bi trebalo da stigne u roku od "+Container.CurrentBasket.find_Longest_ETA())
    }

    class RecyclerViewAdapter(context: Context, activity: FragmentActivity?,caller:NotificationsFragment) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private val context: Context=context
        private val activity: FragmentActivity?=activity
        private val caller:NotificationsFragment=caller;

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val w = LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
            val w = LayoutInflater.from(activity).inflate(
                //R.layout.home_recycleview_item_layout,
                R.layout.notification_recyclerview_item_layout,
                parent,
                false)
            return ViewHolder(w)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //val item = Container.Items[position]
            val item = Container.CurrentBasket.Shoping_List[position]

            holder.textViewInfo.setText(item.Count.toString()+" x "+item.Name+" po ukupnoj ceni "+(item.Price*item.Count).toString())
            holder.button_Remover.setOnClickListener {
                Container.Items.find { it.Name.compareTo(item.Name)==0 }!!.addtoAvailableCount(item.Count)
                Container.CurrentBasket.Shoping_List.remove(item)
                this.notifyDataSetChanged();
                //
                caller.setTotals()
                Toast.makeText(context,"You have removed that item from the shoping list",Toast.LENGTH_SHORT)
            }
        }

        override fun getItemCount(): Int {
            return Container.CurrentBasket.Shoping_List.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var textViewInfo: TextView ;
            lateinit var button_Remover: Button ;
            init {
                if(itemView.findViewById<TextView>(R.id.notification_rview_item_Info)!=null && itemView.findViewById<TextView>(R.id.notification_rview_item_RemoveBTN)!=null)
                {
                    textViewInfo= itemView.findViewById<TextView>(R.id.notification_rview_item_Info)
                    button_Remover= itemView.findViewById<Button>(R.id.notification_rview_item_RemoveBTN)
                }
            }

        }

    }
}