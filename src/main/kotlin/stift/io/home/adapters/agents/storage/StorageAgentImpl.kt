package stift.io.home.adapters.agents.storage

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.service.AiServices
import org.springframework.stereotype.Service
import stift.io.home.domain.PantryService

@Service
class StorageAgentImpl(
    openaiLanguageModel: ChatLanguageModel,
    pantryService: PantryService,
) {
    val aiService: StorageAgent =
        AiServices.builder(StorageAgent::class.java)
            .chatLanguageModel(openaiLanguageModel)
            .tools(pantryService)
            .build()

    fun call(request: String): String {
        return aiService.chat(request)
    }
}
