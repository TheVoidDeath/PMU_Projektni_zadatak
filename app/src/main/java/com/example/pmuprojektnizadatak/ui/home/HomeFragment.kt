package com.example.pmuprojektnizadatak.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pmuprojektnizadatak.R
import com.example.pmuprojektnizadatak.buy_and_modify_activity
import com.example.pmuprojektnizadatak.data.Container
import com.example.pmuprojektnizadatak.databinding.FragmentHomeBinding
import android.widget.ScrollView
import androidx.core.view.isVisible

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet








class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    lateinit var recyclerView_Adapter:RecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var recyclerView= root.findViewById<RecyclerView>(R.id.item_recycleview);
        recyclerView.setLayoutManager(LinearLayoutManager(root.context));
        recyclerView_Adapter=RecyclerViewAdapter(root.context,activity)
        recyclerView.setAdapter(recyclerView_Adapter);
        recyclerView_Adapter.notifyDataSetChanged();

        val Add_BTN=root.findViewById<Button>(R.id.home_AddItem_Btn);
        Add_BTN.setOnClickListener {
            val intent = Intent(root.context, buy_and_modify_activity::class.java)
            intent.putExtra("Source",0)
            startActivityForResult(intent,1)
            //ActivityCompat.startActivity(root.context,intent,Bundle())
        }

        if(Container.LoggedInUser!=null) {
            if (Container.LoggedInUser!!.ClearanceLVL == 0) {
                Add_BTN.isVisible=false;

                val constraintLayout: ConstraintLayout = root.findViewById(R.id.home_Parent)
                val holder_=root.findViewById<ScrollView>(R.id.home_Scroll_Holder)

                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(R.id.home_Scroll_Holder,ConstraintSet.TOP,R.id.home_Parent,ConstraintSet.TOP,25);
                constraintSet.applyTo(constraintLayout);
            }
        }


        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            recyclerView_Adapter.notifyDataSetChanged();
        }
    } //onActivityResult


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecyclerViewAdapter(context: Context, activity: FragmentActivity?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
        public fun update() {
            notifyDataSetChanged()
        }

        private val context: Context
        private val activity: FragmentActivity?=activity

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val w = LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
            val w = LayoutInflater.from(activity).inflate(R.layout.home_recycleview_item_layout, parent, false)
            return ViewHolder(w)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = Container.Items[position]

            holder.textViewHead.text = item.Name
            holder.textViewPrice.text = item.Price.toString()

            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val intent = Intent(v.context, buy_and_modify_activity::class.java)
                    intent.putExtra("Source",1)
                    intent.putExtra("Name",item.Name)
                    ActivityCompat.startActivity(context,intent,Bundle())

                    //ActivityCompat.startActivityForResult(context as Activity, intent, 1, Bundle())
                }
            })
        }

        override fun getItemCount(): Int {
            return Container.Items.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var textViewHead: TextView ;
            lateinit var textViewPrice: TextView
            init {
                if(itemView.findViewById<TextView>(R.id.recycleview_Item_Title)!=null && itemView.findViewById<TextView>(R.id.recycleview_Item_Price)!=null)
                {
                    textViewHead= itemView.findViewById<TextView>(R.id.recycleview_Item_Title)
                    textViewPrice= itemView.findViewById<TextView>(R.id.recycleview_Item_Price)
                }

            }

        }

        init {
            this.context = context
        }
    }
}