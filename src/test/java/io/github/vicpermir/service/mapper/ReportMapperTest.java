package io.github.vicpermir.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ReportMapperTest {

    private ReportMapper reportMapper;

    @BeforeEach
    public void setUp() {
        reportMapper = new ReportMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(reportMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(reportMapper.fromId(null)).isNull();
    }
}
