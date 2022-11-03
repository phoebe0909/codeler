package weiss.kotlin.wordleexperiment

class SolutionWord (var gameNumber: Int){

    val word: String

    private val answerBlock = arrayOf<String>(
        "REACT","SUPER","LOOPS", "SCOPE", "FILES", "CODES","THROW","LOGIC","WHILE","SCOPE","CRASH","VIRUS",
        "ERROR", "MODEM","ARRAY", "MOUSE", "BASIC", "BLOCK", "CACHE", "CLICK", "CLOSE",
        "EMAIL", "ERASE", "TABLE",  "RESET", "WRITE", "LOOPS", "REACT", "LOGIN", "GAMES", "COMMA",
         "FINAL", "EQUAL", "GEEKS", "TYPES", "CLASS", "QUEUE", "BYTES",  "RATIO","CATCH", "SLICE", "MICRO", "APPLE", "DIGIT",
        "BREAK", "LINES", "TYPES",  "LOCAL",  "INNER", "VALUE", "FIELD",  "LIMIT","DEBUG", "PRINT", "EQUAL",
        "POINT", "BRACE", "CATCH", "CYBER","OUTER"
    )

//    init {
//        var scope = (answerBlock.size - 1)
//        var gameNumber = (Math.random() * scope).toInt()
//        word = answerBlock[gameNumber]
//    }

    init{
        val scope = answerBlock.size
        if (gameNumber>=scope){
            gameNumber = 0
        }
        word = answerBlock[gameNumber]
    }



}