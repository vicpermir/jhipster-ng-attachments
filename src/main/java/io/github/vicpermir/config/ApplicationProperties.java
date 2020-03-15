package io.github.vicpermir.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Jh Attachments.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String baseDir;
    private String uploadDir;

    public String getBaseDir() {
        return baseDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
