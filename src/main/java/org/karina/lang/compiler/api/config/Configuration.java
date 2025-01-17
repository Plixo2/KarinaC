package org.karina.lang.compiler.api.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;


@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class Configuration {
    /*
     * absolut path to the source directory
     */
    @NotNull Path sourceDirectory;

    /*
     * print verbose error messages
     */
    boolean verbose;

    /*
     * compilation target
     */
    @NotNull ConfiguredTarget target;


    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {


        private ConfigurationBuilder() {}

        /**
         * Verbose is disabled by default
         */
        private boolean verbose = false;
        private ConfiguredTarget target = null;
        private Path sourceDirectory = null;

        public ConfigurationBuilder sourceDirectory(Path sourceDirectory) {
            this.sourceDirectory = sourceDirectory;
            return this;
        }

        public ConfigurationBuilder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public ConfigurationBuilder target(ConfiguredTarget target) {
            this.target = target;
            return this;
        }

        public Configuration build() {
            return new Configuration(this.sourceDirectory, this.verbose, this.target);
        }
    }

}
