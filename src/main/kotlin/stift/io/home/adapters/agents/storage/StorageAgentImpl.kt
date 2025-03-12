package stift.io.home.adapters.agents.storage

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.service.AiServices
import org.springframework.stereotype.Service

// TODO: How does tool calling work in general? Using LangChain4J, how could you register tools?
@Service
class StorageAgentImpl(
    openaiLanguageModel: ChatLanguageModel,
) {
    val aiService: StorageAgent =
        AiServices.builder(StorageAgent::class.java)
            .chatLanguageModel(openaiLanguageModel)
            .build()

    fun call(request: String): String {
        return aiService.chat(request)
    }
}
