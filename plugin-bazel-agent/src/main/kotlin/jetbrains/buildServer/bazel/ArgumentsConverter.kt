

package jetbrains.buildServer.bazel

import java.io.File

interface ArgumentsConverter {
    fun convert(arguments: Sequence<CommandArgument>): Sequence<String>
    fun buildCommandLines(command: BazelCommand, rcFile: File): Sequence<String>
    fun buildRcLines(command: BazelCommand, projectId: String?): Sequence<String>
}