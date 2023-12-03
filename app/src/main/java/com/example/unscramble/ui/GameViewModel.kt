package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


//membuat class bernama GameViewModel yg perluas dari class ViewModel
class GameViewModel: ViewModel(){

    // tambahkan properti var yang disebut userGuess. Gunakan mutableStateOf() agar Compose mengamati nilai ini dan menetapkan nilai awal ke "".
    var userGuess by mutableStateOf("")

    private set

    // Game UI State
    private val _uiState = MutableStateFlow(GameUiState())

    // menambahkan properti pendukung ke uiState
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
//asStateFlow() membuat alur status yang dapat berubah ini menjadi alur status hanya baca.

    //tambahkan properti bernama currentWord dari jenis string untuk menyimpan kata acak saat ini
    private lateinit var currentWord: String

    //    Set of words used in the game
    private var usedWords: MutableSet<String> = mutableSetOf()


    //pickRandowWordAndShuffle() adalah metode bantuan untuk pilih kata acak dari daftar dan acaklah
    private fun pickRandomWordAndShuffle(): String{
// Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()


        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        }else{
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    //metode helper lain untuk mengacak kata saat ini yang disebut shuffleCurrentWord() yang menggunakan String dan menampilkan String yang diacak.
    private fun shuffleCurrentWord(word:String):String{
        val tempWord = word.toCharArray()

//    scramble the word
        tempWord.shuffle()
        while(String(tempWord).equals(word)){
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    // fungsi bantuan untuk melakukan inisialisasi game yang disebut resetGame(). Gunakan fungsi ini nanti untuk memulai dan memulai ulang game.
    fun resetGame(){
//    Pada fungsi ini, hapus semua kata dalam kumpulan usedWords, lakukan inisialisasi _uiState. Pilih kata baru untuk currentScrambledWord menggunakan pickRandomWordAndShuffle().
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    fun checkUserGuess(){
        if(userGuess.equals(currentWord, ignoreCase = false)){
// User's guess is correct, increase the score
//             and call updateGameState() to prepare the game for next round
            val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updateScore)
        }else{
//            User's guess is wrong, show an error
            _uiState.update{ currentState->
                currentState.copy(isGuessedWordWrong = true)
//            fungsi copy() untuk menyalin objek, sehingga Anda dapat mengubah beberapa propertinya dan tidak mengubah sisanya.
            }
        }

//        Reset User guess
        updateUserGuess("")
    }

//    tambahkan metode lain bernama updateGameState untuk memperbarui skor, menambah jumlah kata saat ini, dan memilih kata baru dari file WordsData.kt
    private fun updateGameState(updatedScore: Int){
    if(usedWords.size == MAX_NO_OF_WORDS){
//last round in the game, update isGameOver to true, don't pick a new word
        _uiState.update{
            currentState->
            currentState.copy(
                isGuessedWordWrong =  false,
                score = updatedScore,
                isGameOver = true
            )
        }
    }else{
//        Normal round in the game
        _uiState.update {
                currentState->
            currentState.copy(
                isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScore,
                currentWordCount = currentState.currentWordCount.inc()
            )
        }
    }
    }

//    fungsi skipWord() untuk meneruskan skor dan mereset tebakan pengguna
    fun skipWord(){
        updateGameState(_uiState.value.score)

//        reset user guess
        updateUserGuess("")
    }


}