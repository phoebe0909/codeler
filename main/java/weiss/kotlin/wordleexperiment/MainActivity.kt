package weiss.kotlin.wordleexperiment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View.NOT_FOCUSABLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import weiss.kotlin.wordleexperiment.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader



//To Do:
//Create string resources for buttons.
//Replace guessArray and answerArray with String variables.
//Tidy up xml files.
//Learn to use color resources.


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private lateinit var solutionWord: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dictionary: MutableList<String>
    private var guess = "     "
    private var guessArray = guess.toCharArray()
    private lateinit var currentButtonRow: Array<Button>
    private var buttonFlag = 1
    private var gameNumber: Int? = null
    private var textWatcher: TextWatcher = object : TextWatcher {
        //This watches the field and activates receive and display input when keys are typed
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            receiveKeyboardInput()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.keyboardActiveArea.addTextChangedListener(textWatcher)
        initializeButtonRow()

        //OnEditorActionListener is listening for an enter key to begin the process of
        //analyzing the user's guess.
        binding.keyboardActiveArea.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                analyzeGuess()
                true
            } else {
                false
            }
        }
        //Getting the number of the last game played so we can continue on with the solution arrayList in order.
        sharedPreferences = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        var gameNumber = sharedPreferences.getInt("gameNumber", 0)
        solutionWord = SolutionWord(gameNumber).word
        Log.d(TAG, "Our solution this round: $solutionWord")
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        gameNumber+=1
        editor.putInt("gameNumber", gameNumber)
        editor.apply()

        binding.playAgainButton.setOnClickListener {
            playVibrationPattern(3)
            val startDialog = NewGameDialog()
            startDialog.show(supportFragmentManager, "NewGame")
        }

        dictionary = createDictionary()
        val wins = sharedPreferences.getInt("wins", 0)
        val losses = sharedPreferences.getInt("losses", 0)
        binding.winsButton.text = "Wins: $wins Losses: $losses"
        editor.putString("solution", solutionWord)
        editor.apply()


    }

    //This method makes the buttons available to receive input from the keyboard.
    private fun initializeButtonRow() {
        currentButtonRow = when (buttonFlag) {
            1 -> arrayOf(binding.b1, binding.b2, binding.b3, binding.b4, binding.b5)
            2 -> arrayOf(binding.b11, binding.b12, binding.b13, binding.b14, binding.b15)
            3 -> arrayOf(binding.b21, binding.b22, binding.b23, binding.b24, binding.b25)
            4 -> arrayOf(binding.b31, binding.b32, binding.b33, binding.b34, binding.b35)
            5 -> arrayOf(binding.b41, binding.b42, binding.b43, binding.b44, binding.b45)
            else -> arrayOf(binding.b51, binding.b52, binding.b53, binding.b54, binding.b55)
        }
    }

    private fun playVibrationPattern(pattern: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            if (pattern == 1) {
                //Lost game vibration pattern.
                val array = longArrayOf(0, 100, 50, 100, 50, 100)
                vibrator.vibrate(VibrationEffect.createWaveform(array, -1))
            }
            if (pattern == 2) {
                //Winning game
                val array =
                    longArrayOf(0, 100, 200, 100, 200, 100, 200, 200, 300)
                vibrator.vibrate(VibrationEffect.createWaveform(array, -1))
            }
            if (pattern == 3) {
                //single click
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            }
        } else {
            val v = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (pattern == 1) {
                    //losing game
                    val array = longArrayOf(0, 100, 50, 100, 50, 100)
                    v.vibrate(VibrationEffect.createWaveform(array, -1))
                }
                if (pattern == 2) {
                    //winning game
                    val array = longArrayOf(
                        0,
                        100,
                        200,
                        100,
                        200,
                        100,
                        200,
                        200,
                        300
                    )
                    v.vibrate(VibrationEffect.createWaveform(array, -1))
                }
                if (pattern == 3) {
                    v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))

                }

            } else {
                v.vibrate(200L)
            }
        }
    }

    //This function sends input from the keyboard to the button display on screen.
    fun receiveKeyboardInput() {
        var guessField: String = binding.keyboardActiveArea.text.toString().uppercase() + "     "
        guessField = guessField.take(5)
        guessArray = guessField.toCharArray()

        for ((index, button) in currentButtonRow.withIndex()) {
            button.text = guessArray[index].toString()
        }
    }

    private fun analyzeGuess(): Boolean {
        Log.d(TAG, "The guess is:  ${String(guessArray)}")

        //Incomplete word. Vibrate and try again.
        if (guessArray.contains(' ')) {
            playVibrationPattern(3)
            return false
        }

        //Test if word is in the dictionary
        val guess = String(guessArray).lowercase()
        var isWord: Boolean
        isWord = try {
            searchDictionary(guess)
        } catch (e: Exception) {
            Log.d(TAG, "Dictionary exception. Proceed without dictionary support.")
            true
        }
        if (!isWord) {
            playVibrationPattern(3)
            val duration = Toast.LENGTH_SHORT
            val toast =
                Toast.makeText(applicationContext, "This word is not in our dictionary.", duration)
            toast.show()
            return false
        }
        //Continue if word is in the dictionary:
        else {

            //Winning guess:
            if (String(guessArray) == solutionWord) {
                gameOver(3)
                val startDialog = WinDialog()
                startDialog.show(supportFragmentManager, "Win")
                playVibrationPattern(2)

            } else {  //Losing guess:
                if (currentButtonRow[0] == binding.b51) {
                    gameOver(10)
                    playVibrationPattern(1)
                    val startDialog = LossDialog()
                    startDialog.show(supportFragmentManager, "Loss")

                } else { //Continue to next row:
                    playVibrationPattern(3)
                    colorTinting()
                    buttonFlag += 1
                    initializeButtonRow()
                    binding.keyboardActiveArea.text.clear()
                }
            }
        }
        return true
    }


    private fun gameOver(int: Int) {
        //Displaying the winning or losing message and coloring the button display.
        colorTinting()
        //I'm storing the solution word in sharedPreferences in order to pass it to
        //the dialog object. There is probably another way to achieve this.
        sharedPreferences.getString("solution", "default")

        //Storing results in shared preferences
        if (int == 3) {
            var value = sharedPreferences.getInt("wins", 0)
            value += 1
            val editor = sharedPreferences.edit()
            editor.putInt("wins", value)
            editor.apply()
            Log.d("MainActivity", "Wins = $value")
        } else {
            var value = sharedPreferences.getInt("losses", 0)
            value += 1
            val editor = sharedPreferences.edit()
            editor.putInt("losses", value)
            editor.apply()
            Log.d("MainActivity", "losses = $value")
        }

        //This hides the keyboard, but it doesn't stop it from coming back if the user taps the screen.
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.keyboardActiveArea.windowToken, 0)

        //This prevents touch from bringing the keyboard back. I also need a solution for earlier build versions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.keyboardActiveArea.focusable = NOT_FOCUSABLE
        }

    }

    //Adding color to the display row of buttons.
    private fun colorTinting() {
        val answerArray = solutionWord.toCharArray()
        val decoyArray = guessArray.copyOf(5)
        for ((index, element) in guessArray.withIndex()) {
            //Green answers
            if (element == answerArray[index]) {
                decoyArray[index] = '#'
                answerArray[index] = '$'
            }
        }
        //Yellow answers
        for (x in 0..4) {
            for (y in 4 downTo 0) {
                if (decoyArray[y] == answerArray[x]) {
                    decoyArray[y] = '@'
                    answerArray[x] = '$'
                }
            }
        }
        for ((index, element) in decoyArray.withIndex()) {
            when (element) {
                '#' -> {
                    currentButtonRow[index].background.setTint(Color.parseColor("#19a337")) //green
                }
                '@' -> {
                    currentButtonRow[index].background.setTint(Color.parseColor("#FF9800"))//yellow
                }
                else -> {
                    currentButtonRow[index].background.setTint(Color.parseColor("#FF040404")) //black
                }
            }
        }
    }

    private fun createDictionary(): MutableList<String>{
        val dictionary = mutableListOf<String>()
        val input = InputStreamReader(assets.open("testfile.csv"))
        val bufferedReader = BufferedReader(input)

        try {
            bufferedReader.forEachLine {
                it
                val row: List<String> = it.split(",")
                for (each in row) {
                    val newWord = each.replace("\"", "")
                    dictionary.add(newWord)
                }
            }
        }catch(e: Exception){
            Log.d(TAG, "Exception in createDictionary()")
        }
        return dictionary
    }

    private fun searchDictionary(string: String): Boolean{
        return dictionary.contains(string)
    }




}






