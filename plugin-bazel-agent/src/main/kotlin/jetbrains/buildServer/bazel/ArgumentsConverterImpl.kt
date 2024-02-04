

package jetbrains.buildServer.bazel

import jetbrains.buildServer.RunBuildException
import java.io.File

class ArgumentsConverterImpl : ArgumentsConverter {
    override fun convert(arguments: Sequence<CommandArgument>): Sequence<String> =
            sequence {
                val commands = mutableListOf<String>()
                val args = mutableListOf<String>()
                val targets = mutableListOf<String>()
                for ((type, value) in arguments) {
                    when (type) {
                        CommandArgumentType.StartupOption -> yield(value)
                        CommandArgumentType.Command -> commands.add(value)
                        CommandArgumentType.Argument -> args.add(value)
                        CommandArgumentType.Target -> targets.add(value)
                    }
                }

                if (commands.isEmpty()) {
                    throw RunBuildException("The command was not specified.")
                }

                yieldAll(commands)
                yieldAll(args)
                if (targets.any()) {
                    yield(targetsSplitter)
                    yieldAll(targets)
                }
            }

    override fun buildRcLines(command: BazelCommand, projectId: String?): Sequence<String> = sequence {
        val commandName = command.arguments.first { it.type == CommandArgumentType.Command }.value
        for((type, value) in command.arguments) {
            when (type) {
                CommandArgumentType.StartupOption -> yield("startup $value")
                CommandArgumentType.Argument -> yield("$commandName $value")
                else -> {}
            }
        }
        projectId?.let {
            if (it.isNotBlank()) {
                yield("$commandName --project_id=$it")
            }
        }
    }

    override fun buildCommandLines(command: BazelCommand, rcFile: File): Sequence<String> = sequence {
        yield("--bazelrc=$rcFile")
        val targets = mutableListOf<String>()
        for((type, value) in command.arguments) {
            when (type) {
                CommandArgumentType.Command -> yield(value)
                CommandArgumentType.Target -> targets.add(value)
                else -> {}
            }
        }
        if (targets.any()) {
            yield(targetsSplitter)
            yieldAll(targets)
        }
    }

    companion object {
        private const val targetsSplitter = "--"
    }
}