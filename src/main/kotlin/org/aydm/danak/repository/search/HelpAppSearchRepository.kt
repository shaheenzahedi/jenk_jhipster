package org.aydm.danak.repository.search

import org.aydm.danak.domain.HelpApp
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID
import java.util.stream.Stream

/**
 * Spring Data Elasticsearch repository for the [HelpApp] entity.
 */
interface HelpAppSearchRepository : ElasticsearchRepository<HelpApp, UUID>, HelpAppSearchRepositoryInternal

interface HelpAppSearchRepositoryInternal {
    fun search(query: String): Stream<HelpApp>
}

class HelpAppSearchRepositoryInternalImpl(val elasticsearchTemplate: ElasticsearchRestTemplate) : HelpAppSearchRepositoryInternal {

    override fun search(query: String): Stream<HelpApp> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return elasticsearchTemplate
            .search(nativeSearchQuery, HelpApp::class.java)
            .map(SearchHit<HelpApp>::getContent)
            .stream()
    }
}
