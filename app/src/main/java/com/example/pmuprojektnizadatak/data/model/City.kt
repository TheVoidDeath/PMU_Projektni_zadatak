package com.example.pmuprojektnizadatak.data.model

import kotlinx.serialization.Serializable
@Serializable
data class City(val city_ascii: String, val country: String, val lat: Double, val lng: Double) {

}
@Serializable
class Cities(var cities:MutableList<City>)
{
    fun get_by_city_Name(Name:String):City?
    {
        for(city in cities)
        {
            if(city.city_ascii.lowercase().compareTo(Name.lowercase())==0)
                return city
        }
        return null;
    }
    fun get_by_country_Name(Name:String):City?
    {
        for(city in cities)
        {
            if(city.country.lowercase().compareTo(Name.lowercase())==0)
                return city
        }
        return null;
    }

    fun get_all_city_Names():MutableList<String>
    {
        var temp= mutableListOf<String>()
        for(city in cities)
        {
            temp.add(city.city_ascii)
        }
        return temp;
    }
}