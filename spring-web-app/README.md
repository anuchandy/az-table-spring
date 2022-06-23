

## Build and Run the application

1. Create `application.properties` under `src\resources` directory.
2. Add storage connection string in the below key-value format
> azure.connString=<conn-string>
3. Open `WebBackendService.java`, set an existing table name for the static variable `TABLE_NAME`.
4. Spring app exposes an endpoint `/fetch`, when called, retrieve and count all entries from the table and return the string `Fetched and enumerated {N} entities <random-uuid>`
5. Running locally:
   * mvn clean package
   * mvn spring-boot:run
   * visit http://localhost:8080/fetch
6. Running on AppService:
   * az login
   * Open pom.xml
     * Uncomment `azure-webapp-maven-plugin` plugin section.
     * In the uncommented plugin section set subscriptionId, a new resourceGroup name and a unique (e.g. uuid) <domain-name> for the app service.
     * run 'mvn package azure-webapp:deploy'
     * visit https://&lt;domain-name&gt;.azurewebsites.net/fetch
     * For more details [refer](https://docs.microsoft.com/en-us/azure/app-service/quickstart-java?tabs=javase&pivots=platform-windows).


## Application Insight to monitor the  Java Heap

From Azure Portal, we can configure and install Application Insight Java Agent to the App-Service.

1. Goto AppService -> Settings -> Application Insights
2. 'Enable' Application Insights.
3. Under 'Instrument your application', select 'Java' and choose 'Yes' for 'My application is a Java app:'.
4. The Application Insight Java Agent uses the content of 'applicationinsights.json' file as configuration.
5. In Azure Portal, we can provide this configuration as plain text.
  
  <img width="750" alt="EnableAppInsightAppServiceBegin" src="https://user-images.githubusercontent.com/1471612/175177518-b4f626a6-6aae-446c-862f-265426a99dbe.png">
  
6. The configuration can have a section `jmxMetrics` describing the JMX metrics to be collected by the agent.
7. But Application Insights Java Agent collects some of the JMX metrics by default (i.e. without you defining `jmxMetrics` section). For example - Heap Memory used, GC Total Count, etc. Navigate to your application insights resource. Under the Metrics tab, select the dropdown as shown below to view the metrics.
  
  <img width="400" alt="AppInsightCustomMetrics" src="https://user-images.githubusercontent.com/1471612/175177652-4cdbd88b-dc1e-41b7-bcdd-17b2495d9cf6.png">

  Here is Heap Memory used, GC Total Count (the JMX metrics collected by default) plotted
  
  <img width="750" alt="HeapMemGcUsedMetrics" src="https://user-images.githubusercontent.com/1471612/175177987-1fb8338e-c6a5-4feb-9400-485cbbe79cbc.png">

If the default JMX metrics are not sufficient, then see next steps.
  
8. The JMX attributes to provide under `jmxMetrics` differ from JVM to JVM, so first we need to get the exact attributes [link](https://docs.microsoft.com/en-us/azure/azure-monitor/app/java-jmx-metrics-configuration) [link](https://docs.microsoft.com/en-us/azure/azure-monitor/app/java-standalone-config#self-diagnostics).
9. To get JMX attributes for your environment, set the following configuration content and click 'Apply'.

```
{
  "selfDiagnostics": {
    "destination": "file+console",
    "level": "DEBUG"
  }
}
```

<img width="750" alt="EnableAppInsightAppService" src="https://user-images.githubusercontent.com/1471612/175178373-85f1d2b9-e205-466b-a722-9b5980430e00.png">

10. Now we need to download the 'applicationInsights.log' file containing the JMX attributes.
11. Go to 'https://&lt;domain-name&gt;.scm.azurewebsites.net'
12. From menu select 'Debug Console' -> PowerShell 
  
<img width="600" alt="KuduAppService" src="https://user-images.githubusercontent.com/1471612/175178559-f9498c70-f200-402d-ad0c-7c997db13830.png">
  
13. From the explorer like section, navigate to 'applicationInsights.log' (LogFiles\ApplicationInsights\applicationinsights.log) and click download button.

<img width="600" alt="KuduLogFilesAppService" src="https://user-images.githubusercontent.com/1471612/175178635-4e9d47b6-d949-44ac-a304-fbfaa0889f2d.png">

14. Search for 'available jmx metrics' in the downloaded log file.

```
2022-06-22 20:17:38.411Z INFO  c.m.applicationinsights.agent - Java version: 11.0.13, vendor: Microsoft, 
                              home: C:\Program Files\Java\microsoft-jdk-11.0.13.8
...
2022-06-22 20:18:58.293Z INFO  c.m.a.a.i.p.AvailableJmxMetricLogger - available jmx metrics:
  
  
  - object name: java.lang:name=Survivor Space,type=MemoryPool
    attributes: 
                 CollectionUsage.committed (number), CollectionUsage.init (number), CollectionUsage.max (number),
                 CollectionUsage.used (number), CollectionUsageThreshold (number), CollectionUsageThresholdCount (number), 
                 CollectionUsageThresholdExceeded (boolean), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other), 
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number), 
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number), 
                 Usage.used (number), UsageThreshold (exception), UsageThresholdCount (exception), UsageThresholdExceeded (exception), 
                 UsageThresholdSupported (boolean), Valid (boolean)
  
  - object name: JMImplementation:type=MBeanServerDelegate
    attributes: 
                 ImplementationName (string), ImplementationVendor (string), ImplementationVersion (string), MBeanServerId (string),
                 SpecificationName (string), SpecificationVendor (string), SpecificationVersion (number)

  - object name: ava.lang:type=Runtime
    attributes: 
                 BootClassPath (exception), BootClassPathSupported (boolean), ClassPath (string), InputArguments (other), 
                 LibraryPath (string), ManagementSpecVersion (number), Name (string), ObjectName (other), Pid (number), 
                 SpecName (string), SpecVendor (string), SpecVersion (number), StartTime (number), SystemProperties (other),
                 Uptime (number), VmName (string), VmVendor (string), VmVersion (string)
  
  - object name: java.lang:type=Threading
    attributes: 
                 AllThreadIds (other), CurrentThreadAllocatedBytes (number), CurrentThreadCpuTime (number), 
                 CurrentThreadCpuTimeSupported (boolean), CurrentThreadUserTime (number), DaemonThreadCount (number),
                 ObjectMonitorUsageSupported (boolean), ObjectName (other), PeakThreadCount (number), 
                 SynchronizerUsageSupported (boolean), ThreadAllocatedMemoryEnabled (boolean), ThreadAllocatedMemorySupported (boolean),
                 ThreadContentionMonitoringEnabled (boolean), ThreadContentionMonitoringSupported (boolean), ThreadCount (number),
                 ThreadCpuTimeEnabled (boolean), ThreadCpuTimeSupported (boolean), TotalStartedThreadCount (number)

  - object name: java.lang:type=OperatingSystem
    attributes: 
                 Arch (string), AvailableProcessors (number), CommittedVirtualMemorySize (number), FreePhysicalMemorySize (number),
                 FreeSwapSpaceSize (number), Name (string), ObjectName (other), ProcessCpuLoad (number), ProcessCpuTime (number),
                 SystemCpuLoad (number), SystemLoadAverage (number), TotalPhysicalMemorySize (number), TotalSwapSpaceSize (number),
                 Version (number)

  - object name: java.nio:name=direct,type=BufferPool
    attributes:  
                 Count (number), MemoryUsed (number), Name (string), ObjectName (other), TotalCapacity (number)

  - object name: java.lang:type=Compilation
    attributes:  CompilationTimeMonitoringSupported (boolean), Name (string), ObjectName (other), TotalCompilationTime (number)
  
  - object name: java.lang:name=Tenured Gen,type=MemoryPool
    attributes: 
                 CollectionUsage.committed (number), CollectionUsage.init (number), CollectionUsage.max (number),
                 CollectionUsage.used (number), CollectionUsageThreshold (number), CollectionUsageThresholdCount (number),
                 CollectionUsageThresholdExceeded (boolean), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)
  
  - object name: java.lang:name=CodeCacheManager,type=MemoryManager
    attributes: 
                MemoryPoolNames (other), Name (string), ObjectName (other), Valid (boolean)
  
  - object name: java.util.logging:type=Logging
    attributes: 
                 LoggerNames (other), ObjectName (other)

  - object name: java.lang:type=ClassLoading
    attributes: 
                 LoadedClassCount (number), ObjectName (other), TotalLoadedClassCount (number), UnloadedClassCount (number),
                 Verbose (boolean)

  - object name: java.lang:name=Metaspace Manager,type=MemoryManager
    attributes: 
                 MemoryPoolNames (other), Name (string), ObjectName (other), Valid (boolean)

  - object name: java.lang:name=Metaspace,type=MemoryPool
    attributes: 
                 CollectionUsage (exception), CollectionUsageThreshold (exception), CollectionUsageThresholdCount (exception),
                 CollectionUsageThresholdExceeded (exception), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)

  - object name: java.lang:name=Eden Space,type=MemoryPool
    attributes: 
                 CollectionUsage.committed (number), CollectionUsage.init (number), CollectionUsage.max (number),
                 CollectionUsage.used (number), CollectionUsageThreshold (number), CollectionUsageThresholdCount (number),
                 CollectionUsageThresholdExceeded (boolean), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (exception), UsageThresholdCount (exception), UsageThresholdExceeded (exception),
                 UsageThresholdSupported (boolean), Valid (boolean)

  - object name: java.lang:name=CodeHeap 'profiled nmethods',type=MemoryPool
    attributes: 
                 CollectionUsage (exception), CollectionUsageThreshold (exception), CollectionUsageThresholdCount (exception),
                 CollectionUsageThresholdExceeded (exception), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)

  - object name: java.lang:name=Copy,type=GarbageCollector
    attributes: 
                 CollectionCount (number), CollectionTime (number), LastGcInfo.duration (number), LastGcInfo.endTime (number)
                 LastGcInfo.id (number), LastGcInfo.memoryUsageAfterGc (other), LastGcInfo.memoryUsageBeforeGc (other),
                 LastGcInfo.startTime (number), MemoryPoolNames (other), Name (string), ObjectName (other), Valid (boolean)
  
  - object name: java.lang:name=MarkSweepCompact,type=GarbageCollector
    attributes: 
                 CollectionCount (number), CollectionTime (number), LastGcInfo.duration (number), LastGcInfo.endTime (number),
                 LastGcInfo.id (number), LastGcInfo.memoryUsageAfterGc (other), LastGcInfo.memoryUsageBeforeGc (other),
                 LastGcInfo.startTime (number), MemoryPoolNames (other), Name (string), ObjectName (other), Valid (boolean)
  
  - object name: java.lang:name=CodeHeap 'non-nmethods',type=MemoryPool
    attributes: 
                 CollectionUsage (exception), CollectionUsageThreshold (exception), CollectionUsageThresholdCount (exception),
                 CollectionUsageThresholdExceeded (exception), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)
  
  - object name: java.lang:name=Compressed Class Space,type=MemoryPool
    attributes: 
                 CollectionUsage (exception), CollectionUsageThreshold (exception), CollectionUsageThresholdCount (exception),
                 CollectionUsageThresholdExceeded (exception), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other),
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)
  
  - object name: java.lang:type=Memory
    attributes: 
                 HeapMemoryUsage.committed (number), HeapMemoryUsage.init (number), HeapMemoryUsage.max (number), 
                 HeapMemoryUsage.used (number), NonHeapMemoryUsage.committed (number), NonHeapMemoryUsage.init (number),
                 NonHeapMemoryUsage.max (number), NonHeapMemoryUsage.used (number), ObjectName (other),
                 ObjectPendingFinalizationCount (number), Verbose (boolean)
  
  - object name: java.nio:name=mapped,type=BufferPool
    attributes: 
                Count (number), MemoryUsed (number), Name (string), ObjectName (other), TotalCapacity (number)
  
  - object name: com.sun.management:type=DiagnosticCommand
    attributes: (no attributes found)
  
  - object name: java.lang:name=CodeHeap 'non-profiled nmethods',type=MemoryPool
    attributes: 
                 CollectionUsage (exception), CollectionUsageThreshold (exception), CollectionUsageThresholdCount (exception), 
                 CollectionUsageThresholdExceeded (exception), CollectionUsageThresholdSupported (boolean), MemoryManagerNames (other), 
                 Name (string), ObjectName (other), PeakUsage.committed (number), PeakUsage.init (number), PeakUsage.max (number),
                 PeakUsage.used (number), Type (string), Usage.committed (number), Usage.init (number), Usage.max (number),
                 Usage.used (number), UsageThreshold (number), UsageThresholdCount (number), UsageThresholdExceeded (boolean),
                 UsageThresholdSupported (boolean), Valid (boolean)
  
  - object name: com.sun.management:type=HotSpotDiagnostic
    attributes: 
                DiagnosticOptions (other), ObjectName (other)
  
  - object name: jdk.management.jfr:type=FlightRecorder
    attributes: 
                 Configurations (other), EventTypes (other), ObjectName (other), Recordings (other)

```

15. Below given an example configuration with `jmxMetrics` section based on the above attributes, which you can set through portal (step 9).

```json
{
  "jmxMetrics": [
        {
          "name": "MSC-GC MemoryUsageBefore",
          "objectName": "java.lang:name=MarkSweepCompact,type=GarbageCollector",
          "attribute": "LastGcInfo.memoryUsageBeforeGc"
        },
        {
          "name": "MSC-GC MemoryUsageAfter",
          "objectName": "java.lang:name=MarkSweepCompact,type=GarbageCollector",
          "attribute": "LastGcInfo.memoryUsageAfterGc"
        },
        {
          "name": "MSC-GC Duration",
          "objectName": "java.lang:name=MarkSweepCompact,type=GarbageCollector",
          "attribute": "LastGcInfo.duration"
        },
              {
          "name": "COPY-GC MemoryUsageBefore",
          "objectName": "java.lang:name=Copy,type=GarbageCollector",
          "attribute": "LastGcInfo.memoryUsageBeforeGc"
        },
        {
          "name": "COPY-GC MemoryUsageAfter",
          "objectName": "java.lang:name=Copy,type=GarbageCollector",
          "attribute": "LastGcInfo.memoryUsageAfterGc"
        },
        {
          "name": "COPY-GC Duration",
          "objectName": "java.lang:name=Copy,type=GarbageCollector",
          "attribute": "LastGcInfo.duration"
        }
  ]
}
```
