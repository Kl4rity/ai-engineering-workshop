package stift.io.home.adapters

import org.springframework.stereotype.Service
import stift.io.home.adapters.agents.storage.StorageAgentImpl

@Service
class LLMController(
    private val storageAgent: StorageAgentImpl,
) {
    fun call(request: String, attachments: List<String>?): String {
        return this.storageAgent.call(
            request = request
        )
    }
}
