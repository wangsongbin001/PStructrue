rootProject.name = "PStructrue"
include("app")

fun getLocalModuleConfig(): List<Map<String, String>> {
    val file = File(rootDir, ".idea/local.config")
    if (!file.exists()) {
        return emptyList()
    }
    val content = file.readText()
    if (content.isEmpty()) {
        return emptyList()
    }
    return try {
        val list = groovy.json.JsonSlurper().parseText(content) as MutableMap<String, List<Map<String, String>>>
        list["local"] ?: listOf()
    } catch (e: Exception) {
        throw groovy.lang.GroovyRuntimeException(".idea/poizon.pzn格式错误, 需要手动删除文件后重试")
    }
}

getLocalModuleConfig().forEach {
    try {
        var path = it["path"]
                ?: throw groovy.lang.GroovyRuntimeException(".idea/poizon.pzn格式错误, 缺少path字段")
        if (path.endsWith("/")) {
            path = path.substring(0, path.length - 1)
        }
        val name = path.substring(path.lastIndexOf("/") + 1)
        println("include $name on $path")
        include(":$name")
        project(":$name").projectDir = file(path)
    } catch (e: java.lang.Exception) {
        throw e
    }
}