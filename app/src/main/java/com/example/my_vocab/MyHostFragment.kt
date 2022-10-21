package com.example.my_vocab

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavHostController

class MyController(context: Context): NavHostController(context) {


    override fun navigateUp(): Boolean {
        return super.navigateUp()
    }

    override fun restoreState(navState: Bundle?) {
        super.restoreState(navState)
    }



    override fun removeOnDestinationChangedListener(listener: OnDestinationChangedListener) {
        super.removeOnDestinationChangedListener(listener)
    }
}