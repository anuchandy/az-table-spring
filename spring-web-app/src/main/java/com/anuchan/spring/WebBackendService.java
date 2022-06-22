package com.anuchan.spring;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class WebBackendService {
    private final TableClient tableClient;
    private static final String TABLE_NAME = "table400";
    private final ListEntitiesOptions options = new ListEntitiesOptions();

    public WebBackendService(TableServiceClient azureClient) {
        this.tableClient = azureClient.getTableClient(TABLE_NAME);
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> fetch() {
        PagedIterable<TableEntity> tableEntities = tableClient.listEntities(options, null, null);

        List<String> rowIds = tableEntities.streamByPage()
                .flatMap(response -> {
                    return response.getElements()
                            .stream()
                            .map(e -> e.getRowKey());
                })
                .collect(Collectors.toList());

        int n = 0;
        for (String ignored : rowIds) {
            n++;
        }

        return CompletableFuture.completedFuture("Fetched and enumerated " + n + " entities " + UUID.randomUUID());
    }
}
