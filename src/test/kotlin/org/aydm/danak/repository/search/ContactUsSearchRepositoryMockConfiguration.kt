package org.aydm.danak.repository.search

import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Configuration

/**
 * Configure a Mock version of ContactUsSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
class ContactUsSearchRepositoryMockConfiguration {

    @MockBean
    private lateinit var mockContactUsSearchRepository: ContactUsSearchRepository
}
