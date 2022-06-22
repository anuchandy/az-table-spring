package com.anuchan.console;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;

import java.util.List;
import java.util.stream.Collectors;

public class App {
    // A pre-created table.
    private static final String TABLE_NAME = "table400";
    private final TableClient tableClient;

    public static void main( String[] args ) {
        final App app = new App();

        // Creating 400 entities
        app.populateTable("p1", 400);
        // Keep fetching forever
        // app.fetchAndConsumeForEver(new ListEntitiesOptions());
    }

    public App() {
        final String connString = System.getenv("CON_STR");
        final TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                .connectionString(connString)
                .buildClient();

        tableClient = tableServiceClient.getTableClient(TABLE_NAME);
    }

    public void populateTable(String partitionKey, int rowCount) {
        for (int row = 0; row < rowCount; row++) {
            String rowKey = String.valueOf(row);
            TableEntity entity = new TableEntity(partitionKey, rowKey)
                    .addProperty("A", "foo1")
                    .addProperty("B", 5.0)
                    .addProperty("C", "bar1")
                    .addProperty("D", 6.0)
                    .addProperty("E","foo2")
                    .addProperty("F", 7.0)
                    .addProperty("G", "bar2")
                    .addProperty("H", 8.0)
                    .addProperty("I", "foo3")
                    .addProperty("K", 9.0)
                    .addProperty("J", "bar3")
                    .addProperty("L", 10.0)
                    .addProperty("M", "foo4")
                    .addProperty("N", 11.0)
                    .addProperty("O", "bar4");

            tableClient.createEntity(entity);
        }
    }

    public void fetchAndConsumeForEver(ListEntitiesOptions options) {
        // Fetch from table forever...
        long iterationId = 0;
        while (true) {
            fetchAndConsume(options, iterationId);
            iterationId = (iterationId + 1 == Long.MAX_VALUE - 1) ? 0 : iterationId + 1;
        }
    }

    public void fetchAndConsume(ListEntitiesOptions options, long iterationId) {
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
        System.out.println("IterationId:" + iterationId + " Entity count:" + n);
    }
}
