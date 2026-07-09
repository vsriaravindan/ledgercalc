package com.example.calchub.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing Favorite Calculators.
 * Persists and retrieves favorite calculator IDs using SharedPreferences.
 *
 * @property context The application context.
 */
class FavoritesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("calc_favorites", Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow<Set<String>>(getFavoritesFromPrefs())
    
    /**
     * A [StateFlow] emitting the current set of favorite calculator IDs.
     */
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private fun getFavoritesFromPrefs(): Set<String> {
        return prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
    }

    /**
     * Toggles the favorite status of a calculator.
     * If the ID exists, it's removed; otherwise, it's added.
     * Persistence is handled automatically.
     *
     * @param calculatorId The unique ID of the calculator.
     */
    fun toggleFavorite(calculatorId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(calculatorId)) {
            current.remove(calculatorId)
        } else {
            current.add(calculatorId)
        }
        _favorites.value = current
        prefs.edit().putStringSet("favorite_ids", current).apply()
    }

    /**
     * Checks if a calculator is currently marked as favorite.
     *
     * @param calculatorId The unique ID of the calculator.
     * @return True if favorite, False otherwise.
     */
    fun isFavorite(calculatorId: String): Boolean {
        return _favorites.value.contains(calculatorId)
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritesRepository? = null

        /**
         * Returns the singleton instance of [FavoritesRepository].
         *
         * @param context Context used to create the repository if needed.
         */
        fun getInstance(context: Context): FavoritesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
