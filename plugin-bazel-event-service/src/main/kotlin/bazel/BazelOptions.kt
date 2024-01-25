

package bazel

import org.apache.commons.cli.*
import java.io.File


class BazelOptions(args: Array<String>) {
    private var _line: CommandLine

    init {
        _line = parser.parse(options, args, true)
    }

    val verbosity: Verbosity
        get() = _line
                .getOptionValue("l")
                ?.let {
                    val curVerbosity = it.toLowerCase()
                    Verbosity.values().map { Pair(it, it.toString().toLowerCase()) }.firstOrNull { it.second == curVerbosity }?.first
                }
                ?: Verbosity.Normal

    val port: Int get() = _line.getOptionValue("p")?.toInt() ?: 0

    val eventFile: File? get() = _line.getOptionValue("f")?.let { File(it) }

    val eventJsonFile: File? get() = _line.getOptionValue("j")?.let { File(it) }

    val profileTraceFile: File? get() = _line.getOptionValue("t")?.let { File(it) }

    val bazelCommandlineFile: File? get() = _line.getOptionValue("c")?.let { File(it) }

    val buildJavaVersion: Int? get() = _line.getOptionValue("build_java_version")?.let { it.toInt() }

    val runJavaVersion: Int? get() = _line.getOptionValue("run_java_version")?.let { it.toInt() }

    val javaHome: File? get() = _line.getOptionValue("java_home")?.let { File(it) }

    companion object {
        private val options = createOptions()
        @Suppress("DEPRECATION")
        private val parser: CommandLineParser = GnuParser()

        private fun createOptions(): Options {
            val options = Options()
            options.addOption("l", "logging", true, "The logging level (Quiet, Normal, Detailed, Verbose, Diagnostic). Optional and Normal by default.")
            options.addOption("p", "port", true, "Specifies the build event service (BES) backend endpoint PORT. Optional and Auto by default.")
            options.addOption("f", "file", true, "Binary file of build event protocol.")
            options.addOption("j", "json_file", true, "JSON file of build event protocol.")
            options.addOption("t", "profile_trace", true, "Profile trace file.")
            options.addOption("c", "command", true, "Specifies the new line separated file containing bazel executable and its command line arguments.")
            options.addOption(null, "build_java_version", true, "The version of Java (e. g. 8, 11, 17) to use for building Java targets.")
            options.addOption(null, "run_java_version", true, "The version of Java (e. g. 8, 11, 17) to use for running Java targets (such as tests).")
            options.addOption(null, "java_home", true, "The path to a Java installation which will override JAVA_HOME.")
            return options
        }

        fun printHelp() {
            // automatically generate the help statement
            val formatter = HelpFormatter()
            formatter.printHelp("java -jar plugin-bazel-event-service.jar [args]", options)
        }
    }
}