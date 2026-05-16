#!/usr/bin/env kotlin

import java.io.File
import java.util.regex.Pattern

enum class SourceScope {
    PRODUCTION,
    TEST,
}

data class GraphTarget(
    val scope: SourceScope,
    val label: String,
    val dotFile: File,
    val pngFile: File,
)

val packagePattern: Pattern = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*$")
val importPattern: Pattern = Pattern.compile("^\\s*import\\s+([\\w.]+)\\s*$")
val basePackage = "dev.pubgstats.bot"

val workingDirectory: File = File(System.getProperty("user.dir")).canonicalFile
val projectRoot = findProjectRoot(workingDirectory)
val scriptFile = resolvePath(__FILE__.path, projectRoot)
val scriptDir: File? = scriptFile.parentFile
val sourceRoot =
    args
        .getOrNull(0)
        ?.let { path -> resolvePath(path, workingDirectory) }
        ?: projectRoot.resolve("src")
val outputBasePath =
    args
        .getOrNull(1)
        ?.let { path -> resolvePath(path, workingDirectory) }
        ?: scriptDir?.resolve("packages")

require(sourceRoot.exists() && sourceRoot.isDirectory) {
    "Source directory does not exist or is not a directory: ${sourceRoot.absolutePath}"
}

val sourceFiles = collectSourceFiles(sourceRoot)
val targets =
    listOf(
        GraphTarget(
            scope = SourceScope.PRODUCTION,
            label = "production",
            dotFile = File("${outputBasePath?.absolutePath}-production.dot"),
            pngFile = File("${outputBasePath?.absolutePath}-production.png"),
        ),
        GraphTarget(
            scope = SourceScope.TEST,
            label = "tests",
            dotFile = File("${outputBasePath?.absolutePath}-test.dot"),
            pngFile = File("${outputBasePath?.absolutePath}-test.png"),
        ),
    )

targets.forEach { target ->
    val fileTree = buildFileTree(sourceFiles, target.scope)
    generateDotFile(target.dotFile, fileTree, target.label)
    createImageOutput(target.dotFile, target.pngFile)
}

println("Scanned root: ${sourceRoot.absolutePath}")
targets.forEach { target ->
    println("${target.label.replaceFirstChar(Char::uppercaseChar)} DOT written: ${target.dotFile.absolutePath}")
    println("${target.label.replaceFirstChar(Char::uppercaseChar)} PNG written: ${target.pngFile.absolutePath}")
}

fun resolvePath(
    path: String,
    baseDir: File,
): File =
    File(path).let { file ->
        if (file.isAbsolute) file.canonicalFile else baseDir.resolve(path).canonicalFile
    }

fun findProjectRoot(startDir: File): File {
    generateSequence(startDir) { it.parentFile }
        .firstOrNull { candidate ->
            candidate.resolve("settings.gradle.kts").isFile || candidate.resolve("gradlew").isFile
        }?.let { return it }

    return startDir
}

fun collectSourceFiles(rootDir: File): List<File> =
    rootDir
        .walkTopDown()
        .onEnter { dir -> !isBuildDir(dir, rootDir) }
        .filter { file -> file.isFile && file.extension == "kt" && classifySourceScope(file) != null }
        .toList()

fun buildFileTree(
    files: List<File>,
    scope: SourceScope,
): Map<String, Set<String>> =
    files
        .asSequence()
        .filter { file -> classifySourceScope(file) == scope }
        .map { file -> file.extractPackageAndImports() }
        .filter { (pkg, _) -> pkg.startsWith(basePackage) }
        .groupingBy { it.first }
        .fold(emptySet()) { acc, (_, importedPackages) -> acc + importedPackages }

fun classifySourceScope(file: File): SourceScope? {
    val path = file.invariantSeparatorsPath
    return when {
        "/src/main/" in path -> SourceScope.PRODUCTION
        "/src/test/" in path -> SourceScope.TEST
        else -> null
    }
}

fun isBuildDir(
    dir: File,
    rootDir: File,
): Boolean {
    if (!dir.isDirectory) return false
    val rel = dir.relativeTo(rootDir).invariantSeparatorsPath
    return rel == "build" || rel.endsWith("/build")
}

fun File.extractPackageAndImports(): Pair<String, Set<String>> {
    val lines = readLines()

    val packageName =
        lines.firstNotNullOfOrNull { line ->
            packagePattern.matcher(line).takeIf { it.find() }?.group(1)
        } ?: "<root>"

    val importedPackages =
        lines
            .mapNotNull { line ->
                val m = importPattern.matcher(line)
                if (!m.find()) return@mapNotNull null
                val fqName = m.group(1)
                if (!fqName.startsWith("$basePackage.")) return@mapNotNull null
                fqName.substringBeforeLast('.', missingDelimiterValue = fqName)
            }.toSet()

    return packageName to importedPackages
}

fun generateDotFile(
    dotFile: File,
    fileTree: Map<String, Set<String>>,
    graphLabel: String,
) {
    dotFile.printWriter().use { out ->
        val allPackages = (fileTree.keys + fileTree.values.flatten()).toSortedSet()
        val packagesByCluster = allPackages.groupBy(::clusterIdForPackage).toSortedMap()
        val edges =
            buildList {
                fileTree.forEach { (pkg, imports) ->
                    imports.forEach { importedPkg ->
                        if (importedPkg != pkg) {
                            add(pkg to importedPkg)
                        }
                    }
                }
            }

        out.println("digraph PackageDependencies {")
        out.println("  rankdir=TB;")
        out.println("  layout=dot;")
        out.println("  ranksep=1.0;")
        out.println("  nodesep=0.6;")
        out.println("  splines=true;")
        out.println("  concentrate=true;")
        out.println("  newrank=true;")
        out.println("  label=\"PUBG Stats Bot — package dependencies ($graphLabel)\";")
        out.println("  labelloc=t;")
        out.println("  fontsize=16;")
        out.println("  fontname=\"Helvetica\";")
        out.println("  node [shape=box, style=\"rounded,filled\", fontname=\"Helvetica\", fontsize=12];")
        out.println("  edge [color=\"#555555\", arrowsize=0.8];")
        out.println()

        packagesByCluster.forEach { (clusterId, packages) ->
            out.println("  subgraph \"cluster_$clusterId\" {")
            out.println("    label=\"${clusterLabel(clusterId)}\";")
            out.println("    style=\"rounded,dashed\";")
            out.println("    color=\"${clusterColor(clusterId)}\";")
            out.println("    fontname=\"Helvetica\";")
            out.println("    fontsize=13;")
            out.println()
            packages.forEach { pkg ->
                val shortName = pkg.removePrefix("$basePackage.")
                out.println("    \"$pkg\" [label=\"$shortName\", fillcolor=\"${nodeColor(clusterId)}\"];")
            }
            out.println("  }")
            out.println()
        }

        edges.forEach { (from, to) ->
            out.println("  \"$from\" -> \"$to\";")
        }

        out.println("}")
    }
}

fun clusterIdForPackage(packageName: String): String =
    when {
        packageName.startsWith("$basePackage.cache") -> "cache"
        packageName.startsWith("$basePackage.discord.command") -> "command"
        packageName.startsWith("$basePackage.discord") -> "discord"
        else -> "misc"
    }

fun clusterLabel(clusterId: String): String =
    when (clusterId) {
        "cache" -> "Cache"
        "command" -> "Command"
        "discord" -> "Discord"
        else -> "Other"
    }

fun clusterColor(clusterId: String): String =
    when (clusterId) {
        "cache" -> "#6C8EBF"
        "command" -> "#B85450"
        "discord" -> "#D4A843"
        else -> "#82B366"
    }

fun nodeColor(clusterId: String): String =
    when (clusterId) {
        "cache" -> "#DAE8FC"
        "command" -> "#F8CECC"
        "discord" -> "#FFF2CC"
        else -> "#D5E8D4"
    }

fun createImageOutput(
    dotFile: File,
    outputPngFile: File,
) {
    val process =
        ProcessBuilder(
            "dot",
            "-Tpng",
            "-Gdpi=150",
            dotFile.absolutePath,
            "-o",
            outputPngFile.absolutePath,
        ).redirectErrorStream(true).start()

    val output = process.inputStream.bufferedReader().readText()
    val exit = process.waitFor()
    check(exit == 0) { "Graphviz 'dot' failed (exit=$exit)\n$output" }
}
