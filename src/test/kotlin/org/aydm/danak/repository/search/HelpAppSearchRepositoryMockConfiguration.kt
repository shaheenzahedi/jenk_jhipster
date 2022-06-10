package org.aydm.danak.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of HelpAppSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class HelpAppSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockHelpAppSearchRepository: HelpAppSearchRepository
}
