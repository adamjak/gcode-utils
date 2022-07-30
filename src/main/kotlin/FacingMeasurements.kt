
import org.apache.commons.cli.ParseException

data class FacingMeasurements(
    val width: Double, // x
    val height: Double, // y
    val radius: Double // r
) {
    constructor(width: Double, height: Double) : this(width, height, 0.0)
    constructor(radius: Double) : this(0.0, 0.0, radius)

    companion object {

        fun getFacingMeasuresFromStringAndType(input: String, type: FacingType): FacingMeasurements {
            if (type == FacingType.RECTANGLE) {
                if (!input.contains('x')) throw ParseException("For rectangle measures have to be defined width and height with 'x' delimiter.")
                val split = input.split('x')
                if (split.size != 2) throw ParseException("For rectangle measures have to be defined width and height. For example 100x50")
                try {
                    return FacingMeasurements(width = split[0].toDouble(), height = split[1].toDouble())
                } catch (e: NumberFormatException) {
                    throw ParseException("Can not parse string $input")
                }
            } else {
                try {
                    return FacingMeasurements(input.toDouble())
                } catch (e: NumberFormatException) {
                    throw ParseException("Can not parse string $input")
                }
            }
        }
    }
}
