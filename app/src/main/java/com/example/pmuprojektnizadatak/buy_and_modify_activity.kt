package com.example.pmuprojektnizadatak

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.example.pmuprojektnizadatak.data.Container
import android.widget.TextView
import com.example.pmuprojektnizadatak.data.model.Item
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.pmuprojektnizadatak.data.model.BoughtItem
import com.google.android.gms.location.*

import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.pmuprojektnizadatak.data.getLocation


class buy_and_modify_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_and_modify)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()


        if(intent.getIntExtra("Source",-1)==0)
        {
            val view=findViewById<ScrollView>(R.id.buy_mLayout)
            view.isVisible=true;
            val switcher=findViewById<Button>(R.id.buy_SwitchToBuy)
            switcher.isVisible=false;

            //set all field vals

            val title_=findViewById<EditText>(R.id.buy_Title_Modify)
            val price_=findViewById<EditText>(R.id.buy_Price_Modify)
            val ammount=findViewById<EditText>(R.id.buy_ACount_Modify)

            val locationSelector_: Spinner = findViewById(R.id.buy_Location_Modify_Spinner)
            val arrayAdapter=ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,Container.CityList.get_all_city_Names()
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                locationSelector_.adapter = adapter
            }

            arrayAdapter.notifyDataSetChanged();


            val SaveBtn=findViewById<Button>(R.id.buy_mSave).setOnClickListener {
                if( TextUtils.isEmpty(title_.text)){
                    title_.setError( "Title is required!" );
                }
                else if (TextUtils.isEmpty(price_.text) || !TextUtils.isDigitsOnly(price_.text)){
                    price_.setError("Price is required!")
                }
                else if ( TextUtils.isEmpty(locationSelector_.selectedItem.toString() )){
                    (locationSelector_.getChildAt(0) as TextView).error = "You must select a location!"
                }
                else if (TextUtils.isEmpty(ammount.text) || !TextUtils.isDigitsOnly(ammount.text)){
                    ammount.setError("You must specify ammount in stock!")
                }
                else{
                    Container.Items.add(// -
                        Item(//
                            title_.text.toString(),
                            price_.text.toString().toInt(),
                            locationSelector_.selectedItem.toString(),
                            ammount.text.toString().toInt()
                        )  //
                    )// -
                    Item.Save(this);
                    val returnIntent = Intent()
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }

            }


        }
        else if(intent.getIntExtra("Source",-1)==1)
        {
            val view=findViewById<ScrollView>(R.id.buy_bLayout).also { it.isVisible=true };OnVisibilityChange();
        }
        else
        {
            throw Exception()
        }
    }

    public fun OnVisibilityChange()
    {
        val b_view=findViewById<ScrollView>(R.id.buy_bLayout)
        val m_view=findViewById<ScrollView>(R.id.buy_mLayout)
        if(b_view.isVisible)
        {
            if(Container.LoggedinUser.ClearanceLVL==1) {
                val switchToModify: Button = findViewById(R.id.buy_SwitchToModify)
                switchToModify.setOnClickListener {
                    b_view.isVisible = false; m_view.isVisible = true;
                    OnVisibilityChange()
                }
            }  //Switch to Modify
            val child_1=findViewById<LinearLayout>(R.id.buy_bLayout_Child_1).also { it.isVisible=true }
            val child_2=findViewById<LinearLayout>(R.id.buy_bLayout_Child_2).also { it.isVisible=true }

            val item= Container.Items.find{ intent.getStringExtra("Name")?.let { it1 ->
                it.Name.compareTo(
                    it1
                )
            } ==0} ?: throw java.lang.Exception("Error finding item")

            //set all field vals

            val title_=findViewById<TextView>(R.id.buy_Title).also { it.setText(item.Name) }
            val price_=findViewById<TextView>(R.id.buy_Price).also { it.setText("Cena je: " + item.Price.toString()) }
            val location=findViewById<TextView>(R.id.buy_Location).also { it.setText("Nalazi se u "+ item.SellerLocation) }
            val ammount_available=findViewById<TextView>(R.id.buy_ACount).also { it.setText("Trenutno ima :" + item.Available_Count.toString() + "u zalihama") }
            val ammount_Selector=findViewById<Spinner>(R.id.buy_AmountSbinner)

            val arrayAdapter=ArrayAdapter<Int>(
                this,android.R.layout.simple_spinner_item,(1..item.Available_Count).toMutableList()
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                ammount_Selector.adapter = adapter
            }

            arrayAdapter.notifyDataSetChanged();//SPinner
            ammount_Selector.setSelection(0)

            val ETA=findViewById<TextView>(R.id.buy_ETA)
            ETA.text="Ovom predmetu bi trebalo oko " + CalculateETA(item.getLatLong()) + " dana da stigne"

            val buy_BTN=findViewById<Button>(R.id.buy_BuyBtn)
            buy_BTN.setOnClickListener {
                try {
                    Container.CurrentBasket.add(
                        BoughtItem(
                            item.Name,
                            item.Price,
                            CalculateETA(item.getLatLong()),
                            ammount_Selector.selectedItem.toString().toInt()
                        )
                    )
                    Container.Items.find { it.Name == item.Name }!!.Available_Count -= ammount_Selector.selectedItem.toString()
                        .toInt()
                    Toast.makeText(
                        this,
                        "You just bought "+ammount_Selector.selectedItem.toString()+" of "+item.Name,
                        Toast.LENGTH_LONG
                    )
                    Item.Save(this);OnVisibilityChange()
                }
                catch (e:Exception)
                {
                    throw Exception("WeFucked")
                }
            }


        }
        else if(m_view.isVisible)
        {
            val child_1=findViewById<LinearLayout>(R.id.buy_bLayout_Child_1).also { it.isVisible=false }
            val child_2=findViewById<LinearLayout>(R.id.buy_bLayout_Child_2).also { it.isVisible=false }

            val switchToBuy:Button=findViewById(R.id.buy_SwitchToBuy)
            switchToBuy.setOnClickListener {
                b_view.isVisible=true; m_view.isVisible=false;
                OnVisibilityChange()
            }

            val item= Container.Items.find{ intent.getStringExtra("Name")?.let { it1 ->
                it.Name.compareTo(
                    it1
                )
            } ==0} ?: throw java.lang.Exception("Error finding item")

            //set all field vals

            val title_=findViewById<EditText>(R.id.buy_Title_Modify).also { it.setText(item.Name) }
            val price_=findViewById<EditText>(R.id.buy_Price_Modify).also { it.setText(item.Price) }
            val ammount=findViewById<EditText>(R.id.buy_ACount_Modify).also { it.setText(item.Available_Count) }

            //Spinner
            val locationSelector_: Spinner = findViewById(R.id.buy_Location_Modify_Spinner)
            val arrayAdapter=ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,Container.CityList.get_all_city_Names()
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                locationSelector_.adapter = adapter
            }

            arrayAdapter.notifyDataSetChanged();//SPinner


            locationSelector_.setSelection(arrayAdapter.getPosition(item.SellerLocation))
            val SaveBtn=findViewById<Button>(R.id.buy_mSave)
            SaveBtn.setOnClickListener {
                if( TextUtils.isEmpty(title_.text)){
                    title_.setError( "Title is required!" );
                }
                else if (TextUtils.isEmpty(price_.text) || !TextUtils.isDigitsOnly(price_.text)){
                    price_.setError("Price is required!")
                }
                else if ( TextUtils.isEmpty(locationSelector_.selectedItem.toString() )){
                    (locationSelector_.getChildAt(0) as TextView).error = "You must select a location!"
                }
                else if (TextUtils.isEmpty(ammount.text) || !TextUtils.isDigitsOnly(ammount.text)){
                    ammount.setError("You must specify ammount in stock!")
                }
                else{
                    Container.Items.find { it.Name.compareTo(item.Name)==0 }!!.OverrideData(
                        title_.text.toString(),
                        price_.text.toString().toInt(),
                        locationSelector_.selectedItem.toString(),
                        ammount.text.toString().toInt()
                    )// -
                    Item.Save(this);
                    Toast.makeText(this,"Successfull",Toast.LENGTH_SHORT)
                    m_view.isVisible=false;b_view.isVisible=true;OnVisibilityChange();
                }

            }

        }
    }

    class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
        }
    }

    fun CalculateETA(itemlocation:List<Double>?):String
    {
        val distance_in_km=CalculateDistance(itemlocation)

        if(distance_in_km>19000F)
        {
            return "otprilike 2 meseca"
        }
        else if(distance_in_km>12000F)
        {
            return "Preko mesec dana"
        }
        else if(distance_in_km>7000F)
        {
            return "otprilike 1 mesec"
        }
        else if(distance_in_km>2000F)
        {
            return "otprilike 20 radnih dana"
        }
        else if(distance_in_km>1000F)
        {
            return "oko 7 radnih dana"
        }
        else
        {
            return "2-3 dana"
        }
    }

    fun CalculateDistance(itemlocation:List<Double>?):Float
    {
        var _out_:FloatArray= floatArrayOf(0.0F,0.0F,0.0F,0.0F);
        getLocation.getLocation(this)
        while (Container.location.size==0)
        {
            Log.e("---------","Waiting for location")
            getLocation.getLocation(this)
            Thread.sleep(1000);
        }
        android.location.Location.distanceBetween(Container.location[0],Container.location[1],itemlocation!![0],itemlocation!![1],_out_)
        return _out_[0]/1000;
    }




    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest =  LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime= 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty())
            {

            }
            else Toast.makeText(this@buy_and_modify_activity,"No location found?",Toast.LENGTH_LONG)
        }
    }


    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

}