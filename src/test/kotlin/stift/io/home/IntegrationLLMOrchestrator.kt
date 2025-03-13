package stift.io.home

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import stift.io.home.evaluation.ResponseEvaluator

@SpringBootTest
class IntegrationLLMOrchestrator {

    private lateinit var responseEvaluator: ResponseEvaluator

    @PostConstruct
    fun setup() {
        responseEvaluator = ResponseEvaluator()
    }

    @Test
    fun `Whatever tests you feel are appropriate`() {
        // TODO: Implement
    }
}
