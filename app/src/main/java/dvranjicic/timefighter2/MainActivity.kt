package dvranjicic.timefighter2

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var playerOneTapButton: Button
    private lateinit var playerTwoTapButton: Button
    private lateinit var firstPlayerScoreTextView: TextView
    private lateinit var secondPlayerScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private var playerOneScore = 0
    private var playerTwoScore = 0
    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 20000
    internal val countDownInterval: Long = 1000
    private  val TAG = MainActivity::class.java.simpleName
    internal var timeLeftOnTimer: Long = 20000

    companion object {
        private const val PLAYER_ONE_SCORE_KEY = "PLAYER_ONE_SCORE_KEY"
        private const val PLAYER_TWO_SCORE_KEY = "PLAYER_TWO_SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showWelcomeInfo()

        Log.d(TAG, "onCreate called. Score is: Player 1 - $playerOneScore; Player 2 - $playerTwoScore")

        playerOneTapButton = findViewById(R.id.first_player_button)
        playerTwoTapButton = findViewById(R.id.second_player_button)
        firstPlayerScoreTextView = findViewById(R.id.first_player_score)
        secondPlayerScoreTextView = findViewById(R.id.second_player_score)
        timeLeftTextView = findViewById(R.id.time_left_indicator)

        if(savedInstanceState != null) {
            playerOneScore = savedInstanceState.getInt(PLAYER_ONE_SCORE_KEY)
            playerTwoScore = savedInstanceState.getInt(PLAYER_TWO_SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }

        playerOneTapButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore("Player 1")
        }

        playerTwoTapButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore("Player 2")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_menu) {
            showAboutInfo()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PLAYER_ONE_SCORE_KEY, playerOneScore)
        outState.putInt(PLAYER_TWO_SCORE_KEY, playerTwoScore)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState: Saving Score: Player 1 - $playerOneScore; Player 2- $playerTwoScore & Time Left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun showAboutInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_text)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun showWelcomeInfo() {
        val dialogTitle = getString(R.string.welcome_title)
        val dialogMessage = getString(R.string.welcome_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton("Let's play!", null)
        builder.setCancelable(false)
        builder.create().show()
    }

    // throw info dialog on game ending
    private fun showEndGameInfo() {
        val dialogTitle = getString(R.string.end_game_title)
        val winner = if (playerOneScore > playerTwoScore) "Player 1" else if (playerOneScore < playerTwoScore) "Player 2" else "No one"
        val dialogMessage = getString(R.string.end_game_text, playerOneScore.toString(), playerTwoScore.toString(), winner)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton("OK", null)
        builder.setCancelable(false)
        builder.create().show()
    }

    private fun restoreGame() {
        firstPlayerScoreTextView.text = getString(R.string.player_one_score, playerOneScore.toString())
        secondPlayerScoreTextView.text = getString(R.string.player_two_score, playerTwoScore.toString())
        val restoredTime = timeLeftOnTimer / 1000
        timeLeftTextView.text = getString(R.string.time_left_string, restoredTime.toString())

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = timeLeftOnTimer / 1000
                timeLeftTextView.text = getString(R.string.time_left_string, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun resetGame() {
        playerOneScore = 0
        playerTwoScore = 0
        firstPlayerScoreTextView.text = getString(R.string.player_one_score, playerOneScore.toString())
        secondPlayerScoreTextView.text = getString(R.string.player_two_score, playerTwoScore.toString())
        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = getString(R.string.time_left_string, initialTimeLeft.toString())

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.time_left_string, timeLeft.toString())
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    private fun endGame() {
        showEndGameInfo()
        resetGame()
    }

    private fun incrementScore(playerScore: String) {
        if(gameStarted.not()) {
            startGame()
        }

        if (playerScore == "Player 1") {
            playerOneScore += 1
            val newScore = getString(R.string.player_one_score, playerOneScore.toString())
            firstPlayerScoreTextView.text = newScore
            val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
            firstPlayerScoreTextView.startAnimation(blinkAnimation)
        } else if (playerScore == "Player 2") {
            playerTwoScore += 1
            val newScore = getString(R.string.player_two_score, playerTwoScore.toString())
            secondPlayerScoreTextView.text = newScore
            val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
            secondPlayerScoreTextView.startAnimation(blinkAnimation)
        }
    }
}