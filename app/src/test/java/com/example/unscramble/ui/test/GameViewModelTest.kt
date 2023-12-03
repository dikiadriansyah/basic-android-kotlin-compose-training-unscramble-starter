package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Test

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue

class GameViewModelTest
{
    private val viewModel = GameViewModel()


    //    Kasus batas
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded(){
//    properti viewModel.uiState.value untuk mendapatkan instance awal class GameUiState
        val gameUiState = viewModel.uiState.value

//    fungsi getUnscrambledWord() untuk mendapatkan kata pemain yang benar dan menampilkan kata yang tidak diacak.
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

// Assert that current word is scrambled.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)

        // Assert that current word count is set to 1.
// fungsi assertTrue() untuk menyatakan bahwa properti currentWordCount disetel ke 1
        assertTrue(gameUiState.currentWordCount == 1)

        // Assert that initially the score is 0.
        assertTrue(gameUiState.score == 0)

//    Assert that the wrong word guessed is false
        assertFalse(gameUiState.isGuessedWordWrong)

        // Assert that game is not over
        assertFalse(gameUiState.isGameOver)
    }
//    ---------------
//    Jalur Gagal
@Test
fun gameViewModel_IncorrectGuess_ErrorFlagSet(){
    // Given an incorrect word as input
    val incorrectPlayerWord = "and"
//        tetapkan nilai "and" ke variabel tersebut, yang seharusnya tidak ada dalam daftar kata.

    viewModel.updateUserGuess(incorrectPlayerWord)
    viewModel.checkUserGuess()
//         metode viewModel.checkUserGuess() untuk memverifikasi tebakan.

    val currentGameUiState = viewModel.uiState.value
    // Assert that score is unchanged
    assertEquals(0, currentGameUiState.score)

    // Assert that checkUserGuess() method updates isGuessedWordWrong correctly
    assertTrue(currentGameUiState.isGuessedWordWrong)


}
//-----------------
//    Jalur Berhasil
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset(){
        /*
        Kode di atas menggunakan format thingUnderTest_TriggerOfTest_ResultOfTest guna memberi nama untuk fungsi pengujian:
thingUnderTest = gameViewModel
TriggerOfTest = CorrectWordGuessed
ResultOfTest = ScoreUpdatedAndErrorFlagUnset
         */
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

//        Untuk memverifikasi apakah kata yang ditebak sudah benar, tambahkan panggilan ke metode viewModel.updateUserGuess()dan teruskan variabel correctPlayerWord sebagai argumen
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
//        metode viewModel.checkUserGuess() untuk memverifikasi tebakan.

        currentGameUiState = viewModel.uiState.value

// Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(currentGameUiState.isGuessedWordWrong)

//        Assert that score is updated correctly.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }
//----------
@Test
fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased(){
    var currentGameUiState = viewModel.uiState.value
    val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

    viewModel.updateUserGuess(correctPlayerWord)
    viewModel.checkUserGuess()
    currentGameUiState = viewModel.uiState.value
    val lastWordCount = currentGameUiState.currentWordCount

    viewModel.skipWord()
    currentGameUiState = viewModel.uiState.value
    // Assert that score remains unchanged after word is skipped.
    assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    // Assert that word count is increased by 1 after word is skipped.
    assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)

}

//----------------
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly(){
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        /*
        Untuk mendapatkan kata pemain yang benar, gunakan fungsi getUnscrambledWord(), yang
        menggunakan kata currentGameUiState.currentScrambledWord dan menampilkan kata yang tidak diacak.
         */

/*
Untuk menguji apakah pengguna menebak semua jawaban, gunakan blok repeat untuk mengulangi eksekusi metode viewModel.updateUserGuess()
 */
        repeat(MAX_NO_OF_WORDS){
//            expectedScore untuk menyatakan bahwa skor akan bertambah untuk setiap jawaban benar.
            expectedScore += SCORE_INCREASE

            viewModel.updateUserGuess(correctPlayerWord)

//            metode viewModel.checkUserGuess() untuk memicu pemeriksaan tebakan pengguna.
            viewModel.checkUserGuess()

            currentGameUiState  = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

//             Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }

        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)

        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)
    }

    companion object{
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}