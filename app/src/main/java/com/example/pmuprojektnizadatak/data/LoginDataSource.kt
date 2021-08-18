package com.example.pmuprojektnizadatak.data

import com.example.pmuprojektnizadatak.data.Container.Companion
import com.example.pmuprojektnizadatak.data.model.User
import java.io.IOException
/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<User> {
        try {
            // TEMPORARY THING
            //val fakeUser = User();
            //return Result.Success(fakeUser)
                if(Companion.UsersList.searchUsers(username,password)!=null)
                {
                    return Result.Success(Companion.UsersList.searchUsers(username,password) as User )
                }
            return Result.Error(IOException("No users with this info found"))

        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}