package org.aydm.danak.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of StaticPageSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class StaticPageSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockStaticPageSearchRepository: StaticPageSearchRepository
}
