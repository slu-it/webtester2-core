package info.novatec.testit.webtester.config.builders;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import info.novatec.testit.webtester.config.ConfigurationAdapter;
import info.novatec.testit.webtester.config.ConfigurationBuilder;
import info.novatec.testit.webtester.config.ConfigurationExporter;
import info.novatec.testit.webtester.config.adapters.GlobalFileConfigurationAdapter;
import info.novatec.testit.webtester.config.adapters.LocalFileConfigurationAdapter;
import info.novatec.testit.webtester.internal.configuration.NamedPropertiesConfigurationAdapter;


public class DefaultConfigurationBuilderTest {

    @Test
    public void defaultAdaptersAreAddedInCorrectOrder() {

        List<ConfigurationAdapter> addedAdapters = new ArrayList<>();
        new DefaultConfigurationBuilder() {

            @Override
            public ConfigurationBuilder withAdapter(ConfigurationAdapter adapterToAdd) {
                addedAdapters.add(adapterToAdd);
                return super.withAdapter(adapterToAdd);
            }

        };

        assertThat(addedAdapters).hasSize(3);
        assertThat(addedAdapters.get(0)).isInstanceOf(NamedPropertiesConfigurationAdapter.class);
        assertThat(addedAdapters.get(1)).isInstanceOf(GlobalFileConfigurationAdapter.class);
        assertThat(addedAdapters.get(2)).isInstanceOf(LocalFileConfigurationAdapter.class);

    }

    @Test
    public void thereAreNoDefaultExporters() {

        List<ConfigurationExporter> addedExporters = new ArrayList<>();
        new DefaultConfigurationBuilder() {

            @Override
            public ConfigurationBuilder withExporter(ConfigurationExporter exporterToAdd) {
                addedExporters.add(exporterToAdd);
                return super.withExporter(exporterToAdd);
            }

        };

        assertThat(addedExporters).isEmpty();

    }

}
