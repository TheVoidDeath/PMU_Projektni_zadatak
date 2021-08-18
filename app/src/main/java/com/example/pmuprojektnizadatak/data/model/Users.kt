package com.example.pmuprojektnizadatak.data.model

import android.content.Context
import com.example.pmuprojektnizadatak.data.Container
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


@Serializable
data class Item(
    var Name: String,
    var Price: Int,
    var SellerLocation: String,
    var Available_Count: Int
)
{
    public fun getLatLong():List<Double>?
    {
        val x:City;
        if (Container.CityList.get_by_city_Name(SellerLocation) != null) {
            x = Container.CityList.get_by_city_Name(SellerLocation) as City;
            return listOf(x.lat,x.lng)
        }
        else if(Container.CityList.get_by_country_Name(SellerLocation) != null)
        {
            x = Container.CityList.get_by_country_Name(SellerLocation) as City;
            return listOf(x.lat,x.lng)
        }
        return null;
    }

    public fun OverrideData(NewName: String,NewPrice: Int,NewSellerLocation: String,NewAvailable_Count: Int)
    {
        Name=NewName;
        Price=NewPrice;
        SellerLocation=NewSellerLocation;
        Available_Count=NewAvailable_Count;
    }

    public fun addtoAvailableCount(num:Int)
    {
        Available_Count+=num
    }
    companion object
    {
        fun Load(context:Context)
        {
            val read_text:String;
            try {
                read_text= File(context.filesDir ,"Items.json").readText();

                //Loading  from File
                Container.Items = Json.decodeFromString<MutableList<Item>>(read_text)
            }
            catch (e: IOException)
            {
                Container.Items = mutableListOf()
            }
        }
        fun Save(context: Context)
        {
            try {
                File(context.filesDir ,"Items.json").writeText(Json.encodeToString<MutableList<Item>>(Container.Items));
            }
            catch (e: IOException)
            {
                throw e;
            }
        }


    }
}

fun MutableList<Item>.FindByName(Title:String): Int {
    for(item in this)
    {
        if(item.Name.compareTo(Title)==0)
        {
            return this.indexOf(item)
        }
    }
    return -1
}


@Serializable
data class BoughtItem(val Name: String,val Price: Int,val ETA:String,val Count:Int)

@Serializable
data class Basket(var Shoping_List: MutableList<BoughtItem>)
{
    fun add(Item:BoughtItem)
    {
        Shoping_List.add(Item);
    }
    fun find_Longest_ETA():String
    {
        var max_ETA:String="Nista u korpi"
        var checkbellow1000:Boolean=false;var check1000:Boolean=false;var check2000:Boolean=false;var check7000:Boolean=false;var check12000:Boolean=false;var check19000:Boolean=false;
        for(item in Shoping_List)
        {
            if(!checkbellow1000 && item.ETA.compareTo("2-3 dana")==0)
            {
                checkbellow1000=true
                max_ETA="2-3 dana";
            }
            if(!check1000 && item.ETA.compareTo("oko 7 radnih dana")==0)
            {
                check1000=true
                max_ETA="oko 7 radnih dana"
            }
            else if(!check2000 && item.ETA.compareTo("otprilike 20 radnih dana")==0)
            {
                check2000=true
                max_ETA="otprilike 20 radnih dana"
            }
            else if(!check7000 && item.ETA.compareTo("otprilike 1 mesec")==0)
            {
                check7000=true
                max_ETA="otprilike 1 mesec"
            }
            else if(!check12000 && item.ETA.compareTo("Preko mesec dana")==0)
            {
                check12000=true
                max_ETA="Preko mesec dana"
            }
            else if(!check19000 && item.ETA.compareTo("otprilike 2 meseca")==0)
            {
                check12000=true
                return "otprilike 2 meseca"
            }
        }
        return max_ETA;
    }

    fun getAllItems():String
    {
        var _out_="";
        for(item in Shoping_List)
        {
            _out_+="U toj porudzibini je bilo "+item.Count + " " + item.Name + " i njihova ukupna cena je " +item.Price*item.Count+"\n";
        }
        return _out_
    }

    fun getTotalPrice():Int
    {
        var _out_=0;
        for(item in Shoping_List)
        {
            _out_+=item.Price*item.Count
        }
        return _out_
    }
}

@Serializable
data class Order(val Shoping_List:Basket)
{
    var Date:String
        get() {
            return Date
        }
        set(value) {Date=value}
    init {
        Date=SimpleDateFormat("dd/M/yyyy").format((Date() ) );
    }
}

@Serializable
data class User(val UserName:String,val Password:String,val ClearanceLVL:Int)
{
    public fun GetUserName():String
    {
        return UserName;
    }
    public fun CheckPass(ProvidedPassword:String):Boolean
    {
        return Password.compareTo(ProvidedPassword)==0
    }
    constructor() : this("Test","Test",0)// Test Construktor

    var Ime:String="";
    var Prezime:String="";
    var Email:String="";
    var Country:String="";
    var Grad:String="";
    var Street:String="";
    var StreetNum:String="";

    public fun ChangeUserData(Name: String,SurName:String,E_Mail:String,country:String,grad:String,Location:String,Location_Num:String)
    {
        Ime=Name;
        Prezime=SurName;
        Email=E_Mail;
        Country=country;
        Grad=grad;
        Street=Location;
        StreetNum=Location_Num;
    }

    var Purchase_History:MutableList<Order> = mutableListOf()

    public fun Add_Basket_ToHystory(ShopingList: Basket)
    {
        Purchase_History.add(Order(ShopingList))
    }

    public fun checkData():Boolean
    {
        if(
            Ime.compareTo("")!=0 &&
            Prezime.compareTo("")!=0 &&
            Email.compareTo("")!=0 &&
            Country.compareTo("")!=0 &&
            Grad.compareTo("")!=0 &&
            Street.compareTo("")!=0 &&
            StreetNum.compareTo("")!=0
        )
            return true
        return false;
    }

}
@Serializable
data class Users (val AllUsers:MutableList<User>)
{

    public fun searchUsers(byUserName:String,Password: String): User? {
        for (user in AllUsers)
        {
            if(user.GetUserName().compareTo(byUserName)==0 && user.CheckPass(Password))
                return user
        }
        return null;
    }
}