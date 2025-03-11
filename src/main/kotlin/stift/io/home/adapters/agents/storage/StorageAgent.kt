package stift.io.home.adapters.agents.storage

import dev.langchain4j.service.SystemMessage

interface StorageAgent {
    @SystemMessage(
        """
            What could I be doing? 🤷‍♂️
        """
    )
    fun chat(
        userMessage: String
    ): String
}
