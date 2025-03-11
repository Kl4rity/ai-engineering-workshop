package stift.io.home.adapters

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.time.LocalDateTime

@RestController
class OllamaController(
    val llmController: LLMController
) {
    @RequestMapping("/ollama/**")
    fun handleRequest(request: HttpServletRequest): ResponseEntity<Any> {
        // Log incoming requests that hit unknown endpoints
        logger.info("GENERAL")
        logger.info(map(request))
        return ResponseEntity.unprocessableEntity().build()
    }

    @GetMapping("/ollama/")
    fun getHeartbeat() {
        logger.info("HEARTBEAT")
    }

    @PostMapping("/ollama/api/chat")
    fun chatRequest(@RequestBody request: OllamaChatRequest, @RequestParam instanceId: String): OllamaChatResponse {
        val lastMessage = request.messages.last()
        val reply = llmController.call(lastMessage.content, lastMessage.images)

        // Dummy values to satisfy Ollama interface for use with other clients
        return OllamaChatResponse(
                Message(
                        role = "system",
                        content = reply,
                        images = listOf(),
                        tool_calls = listOf()
                ),
                model = "homegrown",
                created_at = LocalDateTime.now(),
                done = true,
                done_reason = "stop",
                total_duration = 5589157167,
                load_duration = 3013701500,
                prompt_eval_count = 50,
                prompt_eval_duration = 2000,
                eval_count = 200,
                eval_duration = 1325948000
        )
    }

    private fun map(request: HttpServletRequest): String {
        val method = request.method
        val path = request.requestURI
        val queryString = request.queryString ?: ""

        val body = request.reader.use(BufferedReader::readText)
        val headers =
                request.headerNames.toList().associate { name -> name to request.getHeader(name) }

        return """
            Method: $method
            Path: $path
            Query: $queryString
            Headers: $headers
            Body: $body
        """.trimIndent()
    }

    companion object {
        val logger = LoggerFactory.getLogger(OllamaController::class.java)
    }
}

data class OllamaChatResponse(
        val message: Message,
        val model: String,
        val created_at: LocalDateTime,
        val done: Boolean,
        val done_reason: String,
        val total_duration: Long,
        val load_duration: Long,
        val prompt_eval_count: Long,
        val prompt_eval_duration: Long,
        val eval_count: Long,
        val eval_duration: Long
)

data class Message(
        val role: String,
        val content: String,
        val images: List<String>?,
        val tool_calls: List<ToolCall>?
)

data class ToolCall(val function: Function)

data class Function(val name: String, val arguments: Map<String, Any>)

data class OllamaChatRequest(
        val stream: Boolean,
        val messages: List<Message>,
        val options: ChatOptions,
        val model: String
)

data class ChatOptions(val temperature: Int)
