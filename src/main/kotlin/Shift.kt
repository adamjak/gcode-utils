import java.io.File
import java.util.Locale

class Shift (
    private val file : File,
    private val xShift : Double?,
    private val yShift : Double?,
) {
    fun getGCode(): String {
        val sb = StringBuilder()
        this.file.forEachLine { line ->
            run {
                if (line.contains(" x", true) || line.contains(" y", true)) {
                    sb.append(shiftLine(line))
                } else {
                    sb.append(line)
                }
                sb.append(System.lineSeparator())
            }
        }
        return sb.toString()
    }

    private fun shiftLine(line : String) : String {
        val newLine = StringBuilder()
        line.uppercase(Locale.getDefault()).split(" ").forEach { part ->
            run {
                if (xShift != null && part.contains("x", true)) {
                    newLine.append("X").append(part.substring(1).toDouble().plus(xShift))
                } else if (yShift != null && part.contains("y", true)) {
                    newLine.append("Y").append(part.substring(1).toDouble().plus(yShift))
                } else {
                    newLine.append(part)
                }
                newLine.append(" ")
            }
        }
        return newLine.toString()
    }
}