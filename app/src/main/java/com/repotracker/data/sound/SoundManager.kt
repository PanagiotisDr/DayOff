package com.repotracker.data.sound

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.repotracker.R
import com.repotracker.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager για αναπαραγωγή ήχων UI.
 * Χρησιμοποιεί MediaPlayer για button clicks.
 * Ελέγχει αν οι ήχοι είναι ενεργοποιημένοι μέσω PreferencesRepository.
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {
    
    companion object {
        private const val TAG = "SoundManager"
    }
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    /**
     * Παίζει τον ήχο button click.
     * Ελέγχει πρώτα αν οι ήχοι click είναι ενεργοποιημένοι.
     */
    fun playClickSound() {
        scope.launch {
            try {
                val clickSoundsEnabled = preferencesRepository.areClickSoundsEnabled().first()
                if (!clickSoundsEnabled) {
                    Log.d(TAG, "Οι ήχοι click είναι απενεργοποιημένοι")
                    return@launch
                }
                
                // Αναπαραγωγή ήχου click
                playSound(R.raw.button_click)
                Log.d(TAG, "Button click sound played")
            } catch (e: Exception) {
                Log.e(TAG, "Σφάλμα κατά την αναπαραγωγή ήχου: ${e.message}")
            }
        }
    }
    
    /**
     * Παίζει έναν ήχο από το raw resources.
     * Απελευθερώνει τον MediaPlayer μετά την ολοκλήρωση.
     */
    private fun playSound(soundResId: Int) {
        try {
            val mediaPlayer = MediaPlayer.create(context, soundResId)
            if (mediaPlayer == null) {
                Log.w(TAG, "Δεν βρέθηκε ο ήχος με ID: $soundResId")
                return
            }
            
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
            
            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e(TAG, "Σφάλμα MediaPlayer: ${e.message}")
        }
    }
}
