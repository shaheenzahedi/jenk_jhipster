package org.aydm.danak.repository.search

import org.aydm.danak.domain.StaticPage
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID
import java.util.stream.Stream

/**
 * Spring Data Elasticsearch repository for the [StaticPage] entity.
 */
interface StaticPageSearchRepository : ElasticsearchRepository<StaticPage, UUID>, StaticPageSearchRepositoryInternal

interface StaticPageSearchRepositoryInternal {
    fun search(query: String): Stream<StaticPage>
}

class StaticPageSearchRepositoryInternalImpl(val elasticsearchTemplate: ElasticsearchRestTemplate) : StaticPageSearchRepositoryInternal {

    override fun search(query: String): Stream<StaticPage> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return elasticsearchTemplate
            .search(nativeSearchQuery, StaticPage::class.java)
            .map(SearchHit<StaticPage>::getContent)
            .stream()
    }
}
