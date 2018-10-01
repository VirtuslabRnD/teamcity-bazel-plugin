/*
 * Copyright 2000-2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.bazel.commands

import jetbrains.buildServer.agent.runner.ParameterType
import jetbrains.buildServer.agent.runner.ParametersService
import jetbrains.buildServer.bazel.BazelCommand
import jetbrains.buildServer.bazel.BazelConstants
import jetbrains.buildServer.bazel.CommandLineBuilder
import kotlin.coroutines.experimental.buildSequence

/**
 * Provides arguments to bazel shutdowm command.
 */
class ShutdownCommand(
        override val commandLineBuilder: CommandLineBuilder)
    : BazelCommand {

    override val command: String = BazelConstants.COMMAND_SHUTDOWN

    override val arguments: Sequence<String>
        get() = emptySequence()
}