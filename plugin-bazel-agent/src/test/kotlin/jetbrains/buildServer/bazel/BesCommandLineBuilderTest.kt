package jetbrains.buildServer.bazel

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import jetbrains.buildServer.agent.runner.ParameterType
import jetbrains.buildServer.agent.runner.ParametersService
import jetbrains.buildServer.agent.runner.PathType
import jetbrains.buildServer.agent.runner.PathsService
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File
import java.nio.file.Files

class BesCommandLineBuilderTest {
    @MockK private lateinit var _pathsService: PathsService
    private lateinit var _parametersService: ParametersService
    @MockK private lateinit var _workingDirectoryProvider: WorkingDirectoryProvider
    @MockK private lateinit var _argumentsConverter: ArgumentsConverter
    @MockK private lateinit var _bazelCommand: BazelCommand
    private lateinit var tempDir: File

    @BeforeMethod
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        tempDir = Files.createTempDirectory(this.javaClass.name).toFile()
        tempDir.deleteOnExit()
    }

    @Test
    fun shouldGenerateEventServiceCall() {
        every { _pathsService.toolPath } returns File("/fake/bazel")
        every { _workingDirectoryProvider.workingDirectory } returns File("/fake/workdir")
        every { _pathsService.getPath(PathType.AgentTemp) } returns tempDir
        every { _pathsService.getPath(PathType.Checkout) } returns File("/fake/checkoutdir")
        every { _pathsService.getPath(PathType.Plugin) } returns File("/fake/plugindir")
        every { _pathsService.uniqueName } returns "rcFile" andThen "bazelCommandlineFile" andThen "eventFile"
        every { _bazelCommand.arguments } returns
                sequenceOf(CommandArgument(CommandArgumentType.StartupOption, "foo"))
        every { _argumentsConverter.buildRcLines(any(), any()) } returns sequenceOf("--bar=\"123\"", "--baz")
        every { _argumentsConverter.buildCommandLines(any(), any()) } returns sequenceOf("'zyzzyx'", "\"quux\"")
        _parametersService = ParametersServiceStub()
                .add(ParameterType.Runner, "integration", "BinaryFile")
                .add(ParameterType.System, "teamcity.buildType.id", "Fake build ID")
        val fixture = BesCommandLineBuilder(
                _pathsService, _parametersService, _workingDirectoryProvider, _argumentsConverter
        )
        val besCommandLine = fixture.build(_bazelCommand)

        verify { _argumentsConverter.buildRcLines(_bazelCommand, "Fake build ID") }
        verify { _argumentsConverter.buildCommandLines(_bazelCommand, File(tempDir, "rcFile")) }
        verify(exactly = 0) { _argumentsConverter.convert(any()) }

        Assert.assertEquals(besCommandLine.arguments, listOf(
                "-jar",
            File("/fake/plugindir/tools/plugin-bazel-event-service.jar").absolutePath,
                "-c=${File(tempDir, "bazelCommandlineFile").absolutePath}",
                "-f=${File(tempDir, "eventFile").absolutePath}"
        ))
        val bazelCommands = File(tempDir, "bazelCommandlineFile").readLines()
        Assert.assertEquals(bazelCommands, listOf(File("/fake/bazel").toString(), "'zyzzyx'", "quux"))
        val rcCommands = File(tempDir, "rcFile").readLines()
        Assert.assertEquals(rcCommands, listOf("--bar=\"123\"", "--baz"))
    }
}