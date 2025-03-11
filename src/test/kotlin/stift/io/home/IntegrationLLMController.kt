package stift.io.home

import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import stift.io.home.evaluation.ResponseEvaluator

@SpringBootTest
@ActiveProfiles(value = ["local"])
class IntegrationLLMController {

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
