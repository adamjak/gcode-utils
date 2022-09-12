import java.io.File
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import kotlin.system.exitProcess
import org.apache.commons.cli.MissingArgumentException

fun main(args: Array<String>) {
    val options = Options()

    val outputFile = Option.builder("o")
        .longOpt("output")
        .hasArg()
        .argName("output file")
        .build()

    val facingOption = Option.builder("f")
        .longOpt("facing")
        .hasArg()
        .argName("facingType")
        .desc("set facing type: " + FacingType.values().joinToString(", "))
        .build()

    val facingMeasurementsOption = Option.builder("fm")
        .longOpt("facingMeasurements")
        .hasArg()
        .argName("measurements of facing")
        .desc("set measurements of facing for example 100x50 for rectangle")
        .build()

    val facingDeepOption = Option.builder("fd")
        .longOpt("facingDeep")
        .hasArg()
        .argName("deep of facing")
        .desc("set deep of facing for example 0.75 ")
        .build()

    val deepStepOption = Option.builder("ds")
        .longOpt("deepStep")
        .hasArg()
        .argName("deep step")
        .desc("set deep of 1 step down ")
        .build()

    val toolDiameterOption = Option.builder("td")
        .longOpt("toolDiameter")
        .hasArg()
        .argName("diameter of tool")
        .build()

    val toolHorizontalSpeedOption = Option.builder("ths")
        .longOpt("toolHorizontalSpeed")
        .hasArg()
        .argName("tool horizontal speed")
        .build()

    val toolVerticalSpeedOption = Option.builder("tvs")
        .longOpt("toolverticalSpeed")
        .hasArg()
        .argName("tool vertical speed")
        .build()

    val inputFile = Option.builder("i")
        .longOpt("input")
        .hasArg()
        .argName("input file")
        .build()

    val shiftX = Option.builder("sx")
        .longOpt("shiftX")
        .hasArg()
        .argName("shift X axis from input file")
        .build()

    val shiftY = Option.builder("sy")
        .longOpt("shiftY")
        .hasArg()
        .argName("shift Y axis from input file")
        .build()


    options.addOption(facingOption)
    options.addOption(facingMeasurementsOption)
    options.addOption(toolDiameterOption)
    options.addOption(facingDeepOption)
    options.addOption(deepStepOption)
    options.addOption(toolVerticalSpeedOption)
    options.addOption(toolHorizontalSpeedOption)

    options.addOption(outputFile)
    options.addOption(inputFile)

    options.addOption(shiftX)
    options.addOption(shiftY)

    // define parser
    val cmd: CommandLine
    val parser: CommandLineParser = DefaultParser()
    val helper: HelpFormatter = HelpFormatter()

    try {
        cmd = parser.parse(options, args)

        var gcode = ""

        if (cmd.hasOption(facingOption)) {
            if (!cmd.hasOption(facingMeasurementsOption)) throw MissingArgumentException("In facing operation required measurements")
            if (!cmd.hasOption(facingDeepOption)) throw MissingArgumentException("In facing operation required deep")
            if (!cmd.hasOption(toolDiameterOption)) throw MissingArgumentException("In facing operation tool diameter is required")
            if (!cmd.hasOption(deepStepOption)) throw MissingArgumentException("In facing operation deep step is required")
            if (!cmd.hasOption(toolVerticalSpeedOption) || !cmd.hasOption(toolHorizontalSpeedOption)) throw MissingArgumentException(
                "In facing operation tool speeds are required"
            )

            val facingType = FacingType.getTypeFromString(cmd.getOptionValue(facingOption))
                ?: throw ParseException("Can not parse facing type")

            gcode = Facing(
                type = facingType,
                measurements = FacingMeasurements.getFacingMeasuresFromStringAndType(
                    cmd.getOptionValue(
                        facingMeasurementsOption
                    ), facingType
                ),
                deep = cmd.getOptionValue(facingDeepOption).toDoubleOrNull()
                    ?: throw ParseException("Can not parse deep"),
                tool = Tool(
                    diameter = cmd.getOptionValue(toolDiameterOption).toDoubleOrNull()
                        ?: throw ParseException("Can not parse tool diameter"),
                    verticalSpeed = cmd.getOptionValue(toolVerticalSpeedOption).toDoubleOrNull()
                        ?: throw ParseException("Can not parse tool vertical speed "),
                    horizontalSpeed = cmd.getOptionValue(toolHorizontalSpeedOption).toDoubleOrNull()
                        ?: throw ParseException("Can not parse tool horizontal speed ")
                ),
                deepStep = cmd.getOptionValue(deepStepOption).toDoubleOrNull()
                    ?: throw ParseException("Can not parse deep speed")
            ).getGCode()
        }

        if (cmd.hasOption(shiftX) || cmd.hasOption(shiftY)) {
            if (cmd.hasOption(inputFile)) {
                val file = File(cmd.getOptionValue(inputFile))
                if (file.isDirectory || (!file.canRead())) {
                    throw ParseException("Input file is not file or cant read.")
                } else {
                    gcode = Shift(
                        file = file,
                        xShift = if (cmd.hasOption(shiftX)) cmd.getOptionValue(shiftX).toDoubleOrNull() else null ,
                        yShift = if (cmd.hasOption(shiftY)) cmd.getOptionValue(shiftY).toDoubleOrNull() else null ,
                    ).getGCode()
                }
            } else {
                throw ParseException("For shift must insert input file.")
            }
        }

        if (cmd.hasOption(outputFile)) {
            val file = File(cmd.getOptionValue(outputFile))
            if (file.isDirectory || (file.exists() && !file.canWrite())) {
                throw ParseException("Output file is not file or cant writable.")
            } else {
                file.writeText(gcode)
            }
        } else {
            print(gcode)
        }

    } catch (e: ParseException) {
        println(e.message)
        helper.printHelp("  ", options)
    }

    exitProcess(0)
}