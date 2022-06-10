package org.aydm.danak.web.rest

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.IntegrationTest
import org.aydm.danak.domain.HelpApp
import org.aydm.danak.domain.StaticPage
import org.aydm.danak.domain.enumeration.StaticPageStatus
import org.aydm.danak.repository.StaticPageRepository
import org.aydm.danak.repository.search.StaticPageSearchRepository
import org.aydm.danak.service.mapper.StaticPageMapper
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.UUID
import java.util.stream.Stream
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [StaticPageResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class StaticPageResourceIT {
    @Autowired
    private lateinit var staticPageRepository: StaticPageRepository

    @Autowired
    private lateinit var staticPageMapper: StaticPageMapper

    /**
     * This repository is mocked in the org.aydm.danak.repository.search test package.
     *
     * @see org.aydm.danak.repository.search.StaticPageSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockStaticPageSearchRepository: StaticPageSearchRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restStaticPageMockMvc: MockMvc

    private lateinit var staticPage: StaticPage

    @BeforeEach
    fun initTest() {
        staticPage = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createStaticPage() {
        val databaseSizeBeforeCreate = staticPageRepository.findAll().size
        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)
        restStaticPageMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        ).andExpect(status().isCreated)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeCreate + 1)
        val testStaticPage = staticPageList[staticPageList.size - 1]

        assertThat(testStaticPage.name).isEqualTo(DEFAULT_NAME)
        assertThat(testStaticPage.content).isEqualTo(DEFAULT_CONTENT)
        assertThat(testStaticPage.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testStaticPage.fileId).isEqualTo(DEFAULT_FILE_ID)

        // Validate the StaticPage in Elasticsearch
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createStaticPageWithExistingId() {
        // Create the StaticPage with an existing ID
        staticPageRepository.saveAndFlush(staticPage)
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        val databaseSizeBeforeCreate = staticPageRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restStaticPageMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        ).andExpect(status().isBadRequest)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeCreate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = staticPageRepository.findAll().size
        // set the field null
        staticPage.name = null

        // Create the StaticPage, which fails.
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        restStaticPageMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        ).andExpect(status().isBadRequest)

        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPages() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList
        restStaticPageMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staticPage.id.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fileId").value(hasItem(DEFAULT_FILE_ID.toString())))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getStaticPage() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        val id = staticPage.id
        assertNotNull(id)

        // Get the staticPage
        restStaticPageMockMvc.perform(get(ENTITY_API_URL_ID, staticPage.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(staticPage.id.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.fileId").value(DEFAULT_FILE_ID.toString()))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getStaticPagesByIdFiltering() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)
        val id = staticPage.id

        defaultStaticPageShouldBeFound("id.equals=$id")
        defaultStaticPageShouldNotBeFound("id.notEquals=$id")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameIsEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name equals to DEFAULT_NAME
        defaultStaticPageShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the staticPageList where name equals to UPDATED_NAME
        defaultStaticPageShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameIsNotEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name not equals to DEFAULT_NAME
        defaultStaticPageShouldNotBeFound("name.notEquals=$DEFAULT_NAME")

        // Get all the staticPageList where name not equals to UPDATED_NAME
        defaultStaticPageShouldBeFound("name.notEquals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameIsInShouldWork() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStaticPageShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the staticPageList where name equals to UPDATED_NAME
        defaultStaticPageShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameIsNullOrNotNull() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name is not null
        defaultStaticPageShouldBeFound("name.specified=true")

        // Get all the staticPageList where name is null
        defaultStaticPageShouldNotBeFound("name.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameContainsSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name contains DEFAULT_NAME
        defaultStaticPageShouldBeFound("name.contains=$DEFAULT_NAME")

        // Get all the staticPageList where name contains UPDATED_NAME
        defaultStaticPageShouldNotBeFound("name.contains=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByNameNotContainsSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where name does not contain DEFAULT_NAME
        defaultStaticPageShouldNotBeFound("name.doesNotContain=$DEFAULT_NAME")

        // Get all the staticPageList where name does not contain UPDATED_NAME
        defaultStaticPageShouldBeFound("name.doesNotContain=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByStatusIsEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where status equals to DEFAULT_STATUS
        defaultStaticPageShouldBeFound("status.equals=$DEFAULT_STATUS")

        // Get all the staticPageList where status equals to UPDATED_STATUS
        defaultStaticPageShouldNotBeFound("status.equals=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByStatusIsNotEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where status not equals to DEFAULT_STATUS
        defaultStaticPageShouldNotBeFound("status.notEquals=$DEFAULT_STATUS")

        // Get all the staticPageList where status not equals to UPDATED_STATUS
        defaultStaticPageShouldBeFound("status.notEquals=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByStatusIsInShouldWork() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultStaticPageShouldBeFound("status.in=$DEFAULT_STATUS,$UPDATED_STATUS")

        // Get all the staticPageList where status equals to UPDATED_STATUS
        defaultStaticPageShouldNotBeFound("status.in=$UPDATED_STATUS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByStatusIsNullOrNotNull() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where status is not null
        defaultStaticPageShouldBeFound("status.specified=true")

        // Get all the staticPageList where status is null
        defaultStaticPageShouldNotBeFound("status.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByFileIdIsEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where fileId equals to DEFAULT_FILE_ID
        defaultStaticPageShouldBeFound("fileId.equals=$DEFAULT_FILE_ID")

        // Get all the staticPageList where fileId equals to UPDATED_FILE_ID
        defaultStaticPageShouldNotBeFound("fileId.equals=$UPDATED_FILE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByFileIdIsNotEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where fileId not equals to DEFAULT_FILE_ID
        defaultStaticPageShouldNotBeFound("fileId.notEquals=$DEFAULT_FILE_ID")

        // Get all the staticPageList where fileId not equals to UPDATED_FILE_ID
        defaultStaticPageShouldBeFound("fileId.notEquals=$UPDATED_FILE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByFileIdIsInShouldWork() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where fileId in DEFAULT_FILE_ID or UPDATED_FILE_ID
        defaultStaticPageShouldBeFound("fileId.in=$DEFAULT_FILE_ID,$UPDATED_FILE_ID")

        // Get all the staticPageList where fileId equals to UPDATED_FILE_ID
        defaultStaticPageShouldNotBeFound("fileId.in=$UPDATED_FILE_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByFileIdIsNullOrNotNull() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        // Get all the staticPageList where fileId is not null
        defaultStaticPageShouldBeFound("fileId.specified=true")

        // Get all the staticPageList where fileId is null
        defaultStaticPageShouldNotBeFound("fileId.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllStaticPagesByHelpAppIsEqualToSomething() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)
        var helpApp: HelpApp
        if (findAll(em, HelpApp::class).isEmpty()) {
            helpApp = HelpAppResourceIT.createEntity(em)
            em.persist(helpApp)
            em.flush()
        } else {
            helpApp = findAll(em, HelpApp::class)[0]
        }
        em.persist(helpApp)
        em.flush()
        staticPage.helpApp = helpApp
        staticPageRepository.saveAndFlush(staticPage)
        val helpAppId = helpApp?.id

        // Get all the staticPageList where helpApp equals to helpAppId
        defaultStaticPageShouldBeFound("helpAppId.equals=$helpAppId")

        // Get all the staticPageList where helpApp equals to UUID.randomUUID()
        defaultStaticPageShouldNotBeFound("helpAppId.equals=${UUID.randomUUID()}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultStaticPageShouldBeFound(filter: String) {
        restStaticPageMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staticPage.id?.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fileId").value(hasItem(DEFAULT_FILE_ID.toString())))

        // Check, that the count call also returns 1
        restStaticPageMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultStaticPageShouldNotBeFound(filter: String) {
        restStaticPageMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restStaticPageMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingStaticPage() {
        // Get the staticPage
        restStaticPageMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString()))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewStaticPage() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size

        // Update the staticPage
        val updatedStaticPage = staticPageRepository.findById(staticPage.id).get()
        // Disconnect from session so that the updates on updatedStaticPage are not directly saved in db
        em.detach(updatedStaticPage)
        updatedStaticPage.name = UPDATED_NAME
        updatedStaticPage.content = UPDATED_CONTENT
        updatedStaticPage.status = UPDATED_STATUS
        updatedStaticPage.fileId = UPDATED_FILE_ID
        val staticPageDTO = staticPageMapper.toDto(updatedStaticPage)

        restStaticPageMockMvc.perform(
            put(ENTITY_API_URL_ID, staticPageDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        ).andExpect(status().isOk)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)
        val testStaticPage = staticPageList[staticPageList.size - 1]
        assertThat(testStaticPage.name).isEqualTo(UPDATED_NAME)
        assertThat(testStaticPage.content).isEqualTo(UPDATED_CONTENT)
        assertThat(testStaticPage.status).isEqualTo(UPDATED_STATUS)
        assertThat(testStaticPage.fileId).isEqualTo(UPDATED_FILE_ID)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository).save(testStaticPage)
    }

    @Test
    @Transactional
    fun putNonExistingStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            put(ENTITY_API_URL_ID, staticPageDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            put(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        ).andExpect(status().isBadRequest)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(staticPageDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateStaticPageWithPatch() {
        staticPageRepository.saveAndFlush(staticPage)

        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size

// Update the staticPage using partial update
        val partialUpdatedStaticPage = StaticPage().apply {
            id = staticPage.id

            name = UPDATED_NAME
            content = UPDATED_CONTENT
        }

        restStaticPageMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedStaticPage.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedStaticPage))
        )
            .andExpect(status().isOk)

// Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)
        val testStaticPage = staticPageList.last()
        assertThat(testStaticPage.name).isEqualTo(UPDATED_NAME)
        assertThat(testStaticPage.content).isEqualTo(UPDATED_CONTENT)
        assertThat(testStaticPage.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testStaticPage.fileId).isEqualTo(DEFAULT_FILE_ID)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateStaticPageWithPatch() {
        staticPageRepository.saveAndFlush(staticPage)

        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size

// Update the staticPage using partial update
        val partialUpdatedStaticPage = StaticPage().apply {
            id = staticPage.id

            name = UPDATED_NAME
            content = UPDATED_CONTENT
            status = UPDATED_STATUS
            fileId = UPDATED_FILE_ID
        }

        restStaticPageMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedStaticPage.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedStaticPage))
        )
            .andExpect(status().isOk)

// Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)
        val testStaticPage = staticPageList.last()
        assertThat(testStaticPage.name).isEqualTo(UPDATED_NAME)
        assertThat(testStaticPage.content).isEqualTo(UPDATED_CONTENT)
        assertThat(testStaticPage.status).isEqualTo(UPDATED_STATUS)
        assertThat(testStaticPage.fileId).isEqualTo(UPDATED_FILE_ID)
    }

    @Throws(Exception::class)
    fun patchNonExistingStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            patch(ENTITY_API_URL_ID, staticPageDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(staticPageDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            patch(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(staticPageDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamStaticPage() {
        val databaseSizeBeforeUpdate = staticPageRepository.findAll().size
        staticPage.id = UUID.randomUUID()

        // Create the StaticPage
        val staticPageDTO = staticPageMapper.toDto(staticPage)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStaticPageMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(staticPageDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the StaticPage in the database
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(0)).save(staticPage)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteStaticPage() {
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)

        val databaseSizeBeforeDelete = staticPageRepository.findAll().size

        // Delete the staticPage
        restStaticPageMockMvc.perform(
            delete(ENTITY_API_URL_ID, staticPage.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val staticPageList = staticPageRepository.findAll()
        assertThat(staticPageList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the StaticPage in Elasticsearch
        verify(mockStaticPageSearchRepository, times(1)).deleteById(staticPage.id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchStaticPage() {
        // Configure the mock search repository
        // Initialize the database
        staticPageRepository.saveAndFlush(staticPage)
        `when`(mockStaticPageSearchRepository.search("id:${staticPage.id}"))
            .thenReturn(Stream.of(staticPage))
        // Search the staticPage
        restStaticPageMockMvc.perform(get("$ENTITY_SEARCH_API_URL?query=id:${staticPage.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(staticPage.id.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].fileId").value(hasItem(DEFAULT_FILE_ID.toString())))
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_CONTENT = "AAAAAAAAAA"
        private const val UPDATED_CONTENT = "BBBBBBBBBB"

        private val DEFAULT_STATUS: StaticPageStatus = StaticPageStatus.DRAFT
        private val UPDATED_STATUS: StaticPageStatus = StaticPageStatus.PUBLIC

        private val DEFAULT_FILE_ID: UUID = UUID.randomUUID()
        private val UPDATED_FILE_ID: UUID = UUID.randomUUID()

        private val ENTITY_API_URL: String = "/api/static-pages"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"
        private val ENTITY_SEARCH_API_URL: String = "/api/_search/static-pages"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): StaticPage {
            val staticPage = StaticPage(
                name = DEFAULT_NAME,

                content = DEFAULT_CONTENT,

                status = DEFAULT_STATUS,

                fileId = DEFAULT_FILE_ID

            )

            return staticPage
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): StaticPage {
            val staticPage = StaticPage(
                name = UPDATED_NAME,

                content = UPDATED_CONTENT,

                status = UPDATED_STATUS,

                fileId = UPDATED_FILE_ID

            )

            return staticPage
        }
    }
}
