package com.halliSanthe.app.utils

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {

    private const val PREF_NAME = "halli_santhe_prefs"
    private const val KEY_FAVORITES = "favorites_list"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun toggleFavorite(context: Context, productId: String): Boolean {
        val favorites = getFavorites(context).toMutableSet()
        val isFav: Boolean
        if (favorites.contains(productId)) {
            favorites.remove(productId)
            isFav = false
        } else {
            favorites.add(productId)
            isFav = true
        }
        getPrefs(context).edit().putStringSet(KEY_FAVORITES, favorites).apply()
        return isFav
    }

    fun isFavorite(context: Context, productId: String): Boolean {
        return getFavorites(context).contains(productId)
    }

    fun getFavorites(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }
}
