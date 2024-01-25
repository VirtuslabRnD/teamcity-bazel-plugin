

package jetbrains.buildServer.bazel

/**
 * Provides parameters for bazel runner.
 */
class BazelParametersProvider {

    val workingDirKey: String
        get() = BazelConstants.PARAM_WORKING_DIR

    val commandKey: String
        get() = BazelConstants.PARAM_COMMAND

    val targetsKey: String
        get() = BazelConstants.PARAM_TARGETS

    val toolPathKey: String
        get() = BazelConstants.TOOL_PATH

    val argumentsKey: String
        get() = BazelConstants.PARAM_ARGUMENTS

    val startupOptionsKey: String
        get() = BazelConstants.PARAM_STARTUP_OPTIONS

    val verbosityKey: String
        get() = BazelConstants.PARAM_VERBOSITY

    val verbosityValues: List<Verbosity>
        get() = Verbosity.values().toList()

    val integrationModeKey: String
        get() = BazelConstants.PARAM_INTEGRATION_MODE

    val bepJsonKey: String
        get() = BazelConstants.PARAM_BEP_JSON_FILE

    val profileTraceKey: String
        get() = BazelConstants.PARAM_PROFILE_TRACE_FILE

    val buildJavaVersionKey: String
        get() = BazelConstants.PARAM_BUILD_JAVA_VERSION

    val runJavaVersionKey: String
        get() = BazelConstants.PARAM_RUN_JAVA_VERSION

    val javaVersions: List<Int>
        get() = listOf(8, 11, 17)

    val javaHomeKey: String
        get() = BazelConstants.PARAM_JAVA_HOME

    val integrationModes: List<IntegrationMode>
        get() = IntegrationMode.values().toList()

    // Build feature
    val remoteCacheKey: String
        get() = BazelConstants.PARAM_REMOTE_CACHE
}