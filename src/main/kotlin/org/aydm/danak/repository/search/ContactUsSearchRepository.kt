package org.aydm.danak.repository.search

import org.aydm.danak.domain.ContactUs
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID
import java.util.stream.Stream

/**
 * Spring Data Elasticsearch repository for the [ContactUs] entity.
 */
interface ContactUsSearchRepository : ElasticsearchRepository<ContactUs, UUID>, ContactUsSearchRepositoryInternal

interface ContactUsSearchRepositoryInternal {
    fun search(query: String): Stream<ContactUs>
}

class ContactUsSearchRepositoryInternalImpl(val elasticsearchTemplate: ElasticsearchRestTemplate) : ContactUsSearchRepositoryInternal {

    override fun search(query: String): Stream<ContactUs> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return elasticsearchTemplate
            .search(nativeSearchQuery, ContactUs::class.java)
            .map(SearchHit<ContactUs>::getContent)
            .stream()
    }
}
