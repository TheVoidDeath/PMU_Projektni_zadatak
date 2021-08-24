package com.example.pmuprojektnizadatak.data

import android.app.Application
import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception
import android.os.Environment
import android.util.Log
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import android.os.AsyncTask
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloader.download
import com.downloader.PRDownloaderConfig
import com.example.pmuprojektnizadatak.data.model.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class Container: Application() {
    companion object
    {
        fun UpdateLoadedUserList(context: Context)
        {
            val read_text:String;
            try {
                read_text= File(context.filesDir ,"Users.txt").readText();

                //Loading Users from File
                var text=AsciiDecryption(read_text)
                UsersList=Json.decodeFromString<Users>(text)
            }
            catch (e:IOException)
            {
                UsersList=Users(mutableListOf())
            }
        }
        fun SaveLoadedUserList(context: Context)
        {
            try {

                var text=Json.encodeToString<Users>(UsersList)
                text= AsciiEncryption(text);
                var file:File=File(context.filesDir,"Users.txt")
                file.writeText(text)
                //File("Users.txt").writeText(text) Depriciated
            }
            catch (e:Exception)
            {
                throw e;
            }
        }
        fun AsciiEncryption(text:String):String
        {
            var crypted:String="";
            for(i in text.indices)
            {
                crypted+= ( (text[i].toChar().code)+i ).toChar()
            }
            return crypted
        }
        fun AsciiDecryption(text:String):String
        {
            var decrypted:String="";
            for(i in text.indices)
            {
                //  decrypted+= ( (text[i] as Int)-i ) as Char Depriciated
                decrypted+= ( (text[i].toChar().code)-i ).toChar()
            }
            return decrypted
        }
        fun LoadCities(context: Context)
        {
            val read_text:String;
            try {
                read_text= File(context.filesDir ,"Cities.json").readText();

                //Loading  from File
                CityList=Json.decodeFromString<Cities>(read_text)
                Log.e("-------","Successfull parse")
            }
            catch (e:IOException)
            {
                CityList= Cities(mutableListOf())
            }
        }
        lateinit var UsersList:Users;
        var LoggedInUser: User? =null;
        lateinit var CityList:Cities;

        lateinit var location:List<Double>;

        lateinit var Items:MutableList<Item>;

        lateinit var CurrentBasket:Basket

        fun DownloadFiles(context: Context) {
            try {

                /*
                if(File(context.filesDir.absolutePath,"Cities.json").exists())
                    File(context.filesDir.absolutePath,"Cities.json").delete();
                 */

                val url = URL("https://raw.githubusercontent.com/TheVoidDeath/viser/main/cities.json").toString()
                val config = PRDownloaderConfig.newBuilder()
                    .setReadTimeout(30000)
                    .setConnectTimeout(30000)
                    .build()
                PRDownloader.initialize(context, config)

                download(url,context.filesDir.absolutePath,"Cities.json").build()
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            Log.e("------------","Success")
                        }

                        override fun onError(error: Error?) {
                            if (error != null) {
                                throw error.connectionException
                            };
                        }
                    })
            }
            catch (e:Exception)
            {
                Log.e("----------------","Failed in dl fun")
                throw e;
            }
        }
    }





    override fun onCreate() {
        super.onCreate()
        UpdateLoadedUserList(this)

        //temporary()
        val thread = Thread {
            try {
                DownloadFiles(this);

                Thread.sleep(5000)

                LoadCities(this)

                Item.Load(this);

                CurrentBasket= Basket(mutableListOf());
                location= mutableListOf();
            } catch (e: Exception) {
                throw e;
            }
        }

        thread.start()
//Check location


    }



    //parsing sql
    fun temporary()
    {
        var x:Cities= Cities(mutableListOf())
        val read=File(this.filesDir ,"Cities.json").readText()
        for(string in read.split("{"))
        {
            var ascii:String="";
            var country:String="";
            var lat:Double=0.0
            var lng:Double= 0.0
            for(line in string.split(","))
            {
                if(line.contains("city_ascii"))
                    ascii=line.split("\"")[3]
                else if(line.contains("country"))
                    country=line.split("\"")[3]
                else if(line.contains("lat"))
                    lat=line.split(":")[1].split(",")[0].toDouble()
                else if(line.contains("lng"))
                    lng=line.split(":")[1].split("}")[0].toDouble()
            }
            x.cities.add(City(ascii,country,lat,lng));
        }

        var text=Json.encodeToString<Cities>(x)
        var file:File=File(this.filesDir,"Cities.txt")
        file.writeText(text)

    }


}
