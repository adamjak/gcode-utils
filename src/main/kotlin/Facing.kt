import java.util.Date
import kotlin.math.abs

class Facing(
    private val type: FacingType,
    private val measurements: FacingMeasurements,
    private val deep: Double,
    private val tool: Tool,
    private val deepStep: Double
) {
    fun getGCode(): String {
        val sb: StringBuilder = StringBuilder()
        sb.append("; Outpit time ${Date().toString()}").append(System.lineSeparator())
        sb.append("G17 G90 G21").append(System.lineSeparator())
        sb.append("G0 X0 Y0 F${this.tool.horizontalSpeed}").append(System.lineSeparator())
        sb.append("G0 Z0 F${this.tool.verticalSpeed}").append(System.lineSeparator())

        var actualDeep: Double = 0.0

        while (abs(actualDeep) <= abs(deep)) {

            when (this.type) {
                FacingType.RECTANGLE -> {

                    var actualX: Double = 0.0
                    var actualY: Double = 0.0
                    sb.append("G1 X$actualX Y$actualY F${this.tool.horizontalSpeed}").append(System.lineSeparator())
                    sb.append("G0 Z$actualDeep F${this.tool.verticalSpeed}").append(System.lineSeparator())

                    while (abs(actualY) <= abs(measurements.height)) {

                        if (actualX == 0.0) {
                            sb.append("G1 X${this.measurements.width} Y$actualY F${this.tool.horizontalSpeed}")
                                .append(System.lineSeparator())
                            actualX = this.measurements.width
                        } else {
                            sb.append("G1 X0 Y$actualY F${this.tool.horizontalSpeed}").append(System.lineSeparator())
                            actualX = 0.0
                        }

                        if (abs(actualY) != abs(measurements.height)) {

                            if (abs(actualY + tool.diameter) > abs(measurements.height)) {
                                actualY = measurements.height
                            } else {
                                actualY += tool.diameter
                            }
                            sb.append("G1 X$actualX Y$actualY F${this.tool.horizontalSpeed}").append(System.lineSeparator())
                        } else  {
                            actualY += tool.diameter
                        }
                    }
                }
                FacingType.CIRCLE -> {

                    var actualX: Double = measurements.radius
                    var actualY: Double = 0.0
                    var actualRadius : Double = measurements.radius
                    sb.append("G1 X$actualX Y$actualY F${this.tool.horizontalSpeed}").append(System.lineSeparator())
                    sb.append("G0 Z$actualDeep F${this.tool.verticalSpeed}").append(System.lineSeparator())

                    while (actualY < measurements.radius) {
                        sb.append("G1 X$actualX Y$actualY F${this.tool.horizontalSpeed}").append(System.lineSeparator())

                        sb.append(";---").append(System.lineSeparator())

                         sb.append("G2 X${actualY} Y${measurements.radius} R${actualRadius}").append(System.lineSeparator())
                         sb.append("G2 X${measurements.radius} Y${measurements.radius * 2 - actualY} R${actualRadius}").append(System.lineSeparator())
                         sb.append("G2 X${measurements.radius * 2 - actualY} Y${measurements.radius} R${actualRadius}").append(System.lineSeparator())
                         sb.append("G2 X$actualX Y$actualY R${actualRadius}").append(System.lineSeparator())

                        sb.append(";---").append(System.lineSeparator())

                        actualRadius -= tool.diameter
                        actualY += tool.diameter
                    }
                }
            }

            if (deep < 0) {
                actualDeep -= deepStep
            } else {
                actualDeep += deepStep
            }

        }

        sb.append("M5").append(System.lineSeparator())

        return sb.toString()
    }
}