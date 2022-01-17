package infra.properties

import java.io.File
import java.util.Properties

object OurMapProperties {
    private val defaultProperties = loadProperties("application.properties")
    private val overridingPropertiesList: MutableList<Properties> = mutableListOf()

    init {
        System.getenv("OUR_MAP_OVERRIDING_PROPERTIES_FILENAMES")?.let {
            it.split(",").forEach {
                overridingPropertiesList.add(loadProperties(it))
            }
        }
    }

    private fun loadProperties(propertiesFilename: String): Properties {
        val resource = javaClass.classLoader.getResourceAsStream(propertiesFilename)
        if (resource != null) {
            return resource.use {
                it.let {
                    Properties().apply { load(it) }
                }
            }
        }
        val file = File(propertiesFilename)
        if (file.exists()) {
            return file.inputStream().use {
                it.let {
                    Properties().apply { load(it) }
                }
            }
        }
        return Properties()
    }

    operator fun get(key: String): String? {
        overridingPropertiesList.forEach {
            val property = it.getProperty(key)
            if (property != null) {
                return property
            }
        }
        return defaultProperties.getProperty(key)
    }
}
