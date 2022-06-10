package org.aydm.danak.service

import org.aydm.danak.domain.StaticPage
import org.aydm.danak.repository.StaticPageRepository
import org.aydm.danak.repository.search.StaticPageSearchRepository
import org.aydm.danak.service.dto.StaticPageDTO
import org.aydm.danak.service.mapper.StaticPageMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID
import java.util.stream.Collectors

/**
 * Service Implementation for managing [StaticPage].
 */
@Service
@Transactional
class StaticPageService(
    private val staticPageRepository: StaticPageRepository,
    private val staticPageMapper: StaticPageMapper,
    private val staticPageSearchRepository: StaticPageSearchRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a staticPage.
     *
     * @param staticPageDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(staticPageDTO: StaticPageDTO): StaticPageDTO {
        log.debug("Request to save StaticPage : $staticPageDTO")
        var staticPage = staticPageMapper.toEntity(staticPageDTO)
        staticPage = staticPageRepository.save(staticPage)
        val result = staticPageMapper.toDto(staticPage)
        staticPageSearchRepository.save(staticPage)
        return result
    }

    /**
     * Update a staticPage.
     *
     * @param staticPageDTO the entity to save.
     * @return the persisted entity.
     */
    fun update(staticPageDTO: StaticPageDTO): StaticPageDTO {
        log.debug("Request to save StaticPage : {}", staticPageDTO)
        var staticPage = staticPageMapper.toEntity(staticPageDTO)
        staticPage = staticPageRepository.save(staticPage)
        val result = staticPageMapper.toDto(staticPage)
        staticPageSearchRepository.save(staticPage)
        return result
    }

    /**
     * Partially updates a staticPage.
     *
     * @param staticPageDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(staticPageDTO: StaticPageDTO): Optional<StaticPageDTO> {
        log.debug("Request to partially update StaticPage : {}", staticPageDTO)

        return staticPageRepository.findById(staticPageDTO.id)
            .map {
                staticPageMapper.partialUpdate(it, staticPageDTO)
                it
            }
            .map { staticPageRepository.save(it) }
            .map {
                staticPageSearchRepository.save(it)

                it
            }
            .map { staticPageMapper.toDto(it) }
    }

    /**
     * Get all the staticPages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<StaticPageDTO> {
        log.debug("Request to get all StaticPages")
        return staticPageRepository.findAll()
            .mapTo(mutableListOf(), staticPageMapper::toDto)
    }

    /**
     * Get one staticPage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: UUID): Optional<StaticPageDTO> {
        log.debug("Request to get StaticPage : $id")
        return staticPageRepository.findById(id)
            .map(staticPageMapper::toDto)
    }

    /**
     * Delete the staticPage by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: UUID) {
        log.debug("Request to delete StaticPage : $id")

        staticPageRepository.deleteById(id)
        staticPageSearchRepository.deleteById(id)
    }

    /**
     * Search for the staticPage corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun search(query: String): MutableList<StaticPageDTO> {
        log.debug("Request to search StaticPages for query $query")
        return staticPageSearchRepository.search(query)
            .map { staticPageMapper.toDto(it) }
            .collect(Collectors.toList())
            .toMutableList()
    }
}
