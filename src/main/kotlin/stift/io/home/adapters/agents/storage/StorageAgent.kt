package stift.io.home.adapters.agents.storage

import dev.langchain4j.service.SystemMessage

interface StorageAgent {
    // TODO: What could a system prompt look like?
    @SystemMessage(
        """
            
        """
    )
    // TODO: Do we need a template for user-messages? (@UserMessage)
    fun chat(
        userMessage: String
    ): String
}
