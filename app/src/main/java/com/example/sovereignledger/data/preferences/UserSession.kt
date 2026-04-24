package com.example.sovereignledger.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//marks isverified as true so swtiching tabs doesnt cause need for check again
//uses key-map

//create tehe datastore
val Context.dataStore by preferencesDataStore(name = "user_preferences")
class UserSession(private val context: Context) {

    //key for verification boolean
    private val IS_VERIFIED = booleanPreferencesKey("is_verified")
    //watch verififcation in real time
    val isVerified : Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_VERIFIED]?: false //defualt
    }

    //save status after sucess. a sus fun cuz it might take time and must not stop the app
    suspend fun setVerified(verified: Boolean){
        context.dataStore.edit { preferences -> preferences[IS_VERIFIED] = verified }
    }
}