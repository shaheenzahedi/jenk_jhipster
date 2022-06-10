package org.aydm.danak.service.mapper

import org.junit.jupiter.api.BeforeEach

class HelpAppMapperTest {

    private lateinit var helpAppMapper: HelpAppMapper

    @BeforeEach
    fun setUp() {
        helpAppMapper = HelpAppMapperImpl()
    }
}
