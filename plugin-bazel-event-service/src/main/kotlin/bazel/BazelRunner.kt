

package bazel

import bazel.messages.Color
import bazel.messages.apply
import devteam.rx.Disposable
import devteam.rx.use
import java.io.BufferedReader
import java.io.File

class BazelRunner(
        private val _verbosity: Verbosity,
        private val _bazelCommandlineFile: File,
        private val _besPort: Int,
        private val _eventFile: File? = null,
        private val _eventJsonFile: File? = null,
        private val _profileTraceFile: File? = null,
        private val _buildJavaVersion: Int? = null,
        private val _runJavaVersion: Int? = null,
        ) {

    val args: Sequence<String>
        get() = sequence {
            var hasSpecialArgs = false
            for (arg in _bazelCommandlineFile.readLines()) {
                val normalizedArg = arg.replace(" ", "").replace("\"", "").replace("'", "")

                // remove existing bes_backend arg if port != 0
                if (_besPort != 0 && normalizedArg.startsWith(besBackendArg, true)) {
                    continue
                }

                // remove existing bes_backend arg if eventFile was specified
                if (_eventFile != null && normalizedArg.startsWith(eventBinaryFileArg, true)) {
                    continue
                }

                if (arg.trim() == "--") {
                    yieldAll(specialArgs)
                    hasSpecialArgs = true
                }

                yield(arg)
            }

            if (!hasSpecialArgs) {
                yieldAll(specialArgs)
            }
        }

    private val specialArgs: Sequence<String>
        get() = sequence {
            if (_besPort != 0) {
                yield("${besBackendArg}grpc://localhost:$_besPort")
            }

            if (_eventFile != null) {
                yield("${eventBinaryFileArg}${_eventFile.absolutePath}")
            }

            if (_eventJsonFile != null) {
                yield("${eventJsonFileArg}${_eventJsonFile.absolutePath}")
            }

            if (_profileTraceFile != null) {
                yield("${profileTraceFileArg}${_profileTraceFile.absolutePath}")
            }

            _buildJavaVersion?.let {
                yield("--java_language_version=${it}")
            }

            _runJavaVersion?.let {
                yield("--java_runtime_version=${it}")
            }
        }

    val workingDirectory: File = File(".").absoluteFile

    fun run(): Result {
        val process = ProcessBuilder(args.toList())
                .directory(workingDirectory)
                .start()

        val errors = mutableListOf<String>()

        ActiveReader(process.inputStream.bufferedReader()) { line ->
            if (_verbosity.atLeast(Verbosity.Diagnostic)) {
                System.out.println("> ".apply(Color.Trace) + line)
            }
        }.use {
            ActiveReader(process.errorStream.bufferedReader()) { line ->
                if (line.startsWith("ERROR:") || line.startsWith("FATAL:")) {
                    errors.add(line)
                }

                if (_verbosity.atLeast(Verbosity.Diagnostic)) {
                    System.out.println("> ".apply(Color.Trace) + line)
                }
            }.use { }
        }

        process.waitFor()
        _eventJsonFile?.let { println("##teamcity[publishArtifacts '$it']") }
        _profileTraceFile?.let { println("##teamcity[publishArtifacts '$it']") }
        val exitCode = process.exitValue()
        return Result(exitCode, errors)
    }

    companion object {
        private const val besBackendArg = "--bes_backend="
        private const val eventBinaryFileArg = "--build_event_binary_file="
        private const val eventJsonFileArg = "--build_event_json_file="
        private const val profileTraceFileArg = "--profile="
    }

    private class ActiveReader(reader: BufferedReader, action: (line: String) -> Unit) : Disposable {
        private val _tread: Thread = object : Thread() {
            override fun run() {
                do {
                    val line = reader.readLine()
                    if (!line.isNullOrBlank()) {
                        action(line)
                    }
                } while (line != null)
            }
        }

        init {
            _tread.start()
        }

        override fun dispose() = _tread.join()
    }

    data class Result(val exitCode: Int, val errors: List<String>)
}