@file:OptIn(ExperimentalSerializationApi::class)

package techare.simplevms.demo.model

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import io.github.xn32.json5k.Json5
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL

val json5 = Json5 {
    prettyPrint = true
    indentationWidth = 3
    quoteMemberNames = false
}

val json = Json {
    prettyPrint = true
}

@OptIn(ExperimentalSerializationApi::class)
class SimulationModelTest : FunSpec({

    val yaml: URL = javaClass.getResource("${SimulationModelTest::class.java.simpleName}.SampleModel.yml")!!

    test("Loading simulation model from yaml file.") {
        val model = yaml.openStream().use { Yaml.default.decodeFromStream<Simulation>(it) }
        println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
        println(json.encodeToString(model))
        println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")
        println(json5.encodeToString(model))
        println("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯")

    }

})
