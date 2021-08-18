package com.example.pmuprojektnizadatak.ui.dashboard

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pmuprojektnizadatak.R
import com.example.pmuprojektnizadatak.buy_and_modify_activity
import com.example.pmuprojektnizadatak.data.Container
import com.example.pmuprojektnizadatak.databinding.FragmentDashboardBinding
import android.content.DialogInterface
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pmuprojektnizadatak.ui.home.HomeFragment


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val name_Edit=root.findViewById<EditText>(R.id.dashboard_Name)
        val surname_Edit=root.findViewById<EditText>(R.id.dashboard_SurName)
        val email_Edit=root.findViewById<EditText>(R.id.dashboard_Email)
        val country_Edit=root.findViewById<EditText>(R.id.dashboard_State)
        val city_Edit=root.findViewById<EditText>(R.id.dashboard_City)
        val street_Edit=root.findViewById<EditText>(R.id.dashboard_Street)
        val streetNum_Edit=root.findViewById<EditText>(R.id.dashboard_StreetNum)


        Container.Companion.UpdateLoadedUserList(root.context)
        Container.LoggedinUser=Container.UsersList.searchUsers(Container.LoggedinUser.UserName,Container.LoggedinUser.Password)!!
        var recyclerView= root.findViewById<RecyclerView>(R.id.dashboard_PastPurchases_recyclerview);
        recyclerView.setLayoutManager(LinearLayoutManager(root.context));
        val recyclerView_Adapter= DashboardFragment.RecyclerViewAdapter(root.context, activity)
        recyclerView.setAdapter(recyclerView_Adapter);

        fun load_fromUser()
        {
            name_Edit.setText(Container.LoggedinUser.Ime)
            surname_Edit.setText(Container.LoggedinUser.Prezime)
            email_Edit.setText(Container.LoggedinUser.Email)
            country_Edit.setText(Container.LoggedinUser.Country)
            city_Edit.setText(Container.LoggedinUser.Grad)
            street_Edit.setText(Container.LoggedinUser.Street)
            streetNum_Edit.setText(Container.LoggedinUser.StreetNum)

            recyclerView_Adapter.notifyDataSetChanged();
        }
        fun save_toUser()
        {
            Container.UsersList.AllUsers.find { it.UserName.compareTo(Container.LoggedinUser.UserName)==0 }
                ?.ChangeUserData(
                    name_Edit.text.toString(),
                    surname_Edit.text.toString(),
                    email_Edit.text.toString(),
                    country_Edit.text.toString(),
                    city_Edit.text.toString(),
                    street_Edit.text.toString(),
                    streetNum_Edit.text.toString()
                )
            Container.LoggedinUser=
                Container.UsersList.searchUsers(Container.LoggedinUser.UserName,Container.LoggedinUser.Password)!!

            Container.Companion.SaveLoadedUserList(root.context)

            load_fromUser()
        }


        load_fromUser()

        val dashboardSaveBtn=root.findViewById<Button>(R.id.dashboard_Save)
        dashboardSaveBtn.setOnClickListener {
            if(email_Edit.text.contains('@')) {
                save_toUser()
                Toast.makeText(root.context,"Successfull change",Toast.LENGTH_SHORT);
            }
            else
            {
                email_Edit.setError("Not a valid email")
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecyclerViewAdapter(context: Context, activity: FragmentActivity?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        private val context: Context
        private val activity: FragmentActivity?=activity

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val w = LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
            val w = LayoutInflater.from(activity).inflate(
                //R.layout.home_recycleview_item_layout,
                R.layout.dashboard_recycleview_item_purchased,
                parent,
                false)
            return ViewHolder(w)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //val item = Container.Items[position]
            val item = Container.LoggedinUser.Purchase_History[position]
            holder.textViewDate.text = item.Date

            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Vasa porudzbina od "+ item.Date)
                    builder.setMessage(item.Shoping_List.getAllItems())


                    val positiveButtonClick = { dialog: DialogInterface, which: Int ->}

                    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
                    builder.show()
                }
            })
        }

        override fun getItemCount(): Int {
            val size=Container.LoggedinUser.Purchase_History
            if(size!=null)
                return size.size
            else return 0;
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var textViewDate: TextView ;
            init {
                if(itemView.findViewById<TextView>(R.id.recycleview_Item_Title)!=null && itemView.findViewById<TextView>(R.id.recycleview_Item_Price)!=null)
                {
                    textViewDate= itemView.findViewById<TextView>(R.id.dashboard_recycleview_item_MainText)
                }
            }

        }

        init {
            this.context = context
        }
    }
}