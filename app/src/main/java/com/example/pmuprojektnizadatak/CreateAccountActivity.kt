package com.example.pmuprojektnizadatak

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.pmuprojektnizadatak.data.Container
import com.example.pmuprojektnizadatak.data.model.User
import com.example.pmuprojektnizadatak.ui.login.LoginActivity

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val Username=findViewById<EditText>(R.id.create_Username)
        val Password=findViewById<EditText>(R.id.create_Password)
        val Admin_Password=findViewById<EditText>(R.id.create_Administrator_Password)
        var check:Int=0;

        val Register_Button=findViewById<Button>(R.id.create_User);

        Register_Button.setOnClickListener {
            if(Admin_Password.text.toString().compareTo("")!=0 && Admin_Password.text.toString().toInt()==42069) check=1

            if(Container.Companion.UsersList.searchUsers(Username.text.toString(),Password.text.toString())==null)
            {
                Container.Companion.UsersList.AllUsers.add(User(Username.text.toString(),Password.text.toString(),check))
                Container.Companion.SaveLoadedUserList(this);

                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setMessage("Succesfully registered")
                    setOnDismissListener {
                        val intent = Intent(this.context, LoginActivity::class.java).apply { }
                        //startActivity(intent)
                        finish()
                    }
                    show()
                }
            }
            else
            {
                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setMessage("This Account Already Exists")
                    show()
                }
            }
        }

        val BackButton=findViewById<Button>(R.id.create_account_BackButton)
        BackButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply { }
            //startActivity(intent)
            finish()
        }
    }
}