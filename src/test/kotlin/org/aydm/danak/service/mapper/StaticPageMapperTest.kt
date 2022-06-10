package org.aydm.danak.service.mapper

import org.junit.jupiter.api.BeforeEach

class StaticPageMapperTest {

    private lateinit var staticPageMapper: StaticPageMapper

    @BeforeEach
    fun setUp() {
        staticPageMapper = StaticPageMapperImpl()
    }
}
