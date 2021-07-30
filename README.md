Bug reproduction project for AGP 7.0.0 + Jacoco issue
=====================================================

Steps to reproduce:
------------------
* Run instrumentation tests, disabling cache to make sure everything is run from scratch:
    ```
    ./gradlew --no-daemon --no-build-cache clean cAT jacocoTestReport --stacktrace
    ``` 
* Expected behavior: tests pass
* Actual behavior: the gradle tasks fails, with this stacktrace:
    ```
    Caused by: java.lang.IllegalStateException: Unexpected SMAP line: *S KotlinDebug
        at org.jacoco.core.internal.analysis.filter.KotlinInlineFilter.getFirstGeneratedLineNumber(KotlinInlineFilter.java:98)
        at org.jacoco.core.internal.analysis.filter.KotlinInlineFilter.filter(KotlinInlineFilter.java:43)
        at org.jacoco.core.internal.analysis.filter.Filters.filter(Filters.java:57)
        at org.jacoco.core.internal.analysis.ClassAnalyzer.addMethodCoverage(ClassAnalyzer.java:110)
        at org.jacoco.core.internal.analysis.ClassAnalyzer.access$100(ClassAnalyzer.java:31)
        at org.jacoco.core.internal.analysis.ClassAnalyzer$1.accept(ClassAnalyzer.java:99)
        at org.jacoco.core.internal.flow.ClassProbesAdapter$2.visitEnd(ClassProbesAdapter.java:89)
        at org.objectweb.asm.ClassReader.readMethod(ClassReader.java:1279)
        at org.objectweb.asm.ClassReader.accept(ClassReader.java:679)
        at org.objectweb.asm.ClassReader.accept(ClassReader.java:391)
        at org.jacoco.core.analysis.Analyzer.analyzeClass(Analyzer.java:122)
        at org.jacoco.core.analysis.Analyzer.analyzeClass(Analyzer.java:138)
        ... 38 more
    ```

Workarounds:
-----------
Either of the following workarounds will make tests pass again. Only one is needed, not both:

Workaround #1: Revert agp from `7.0.0` to `4.2.2`

Workaround #2: Replace the gradle variable `jacocoVersion` with anything else (`horse` for example)

Explanation:
-----------
* The `Hello` class causes a problem when running tests with an older version of jacoco. It appears this issue was fixed in jacoco `0.8.7`: https://github.com/jacoco/jacoco/issues/1155
* With AGP `4.2.2`, our requested version of jacoco, as specified by the `jacocoVersion` variable which is used in multiple gradle configurations, is used everywhere. This can be confirmed by running `./gradlew :app:dependencies`
    ```
    androidJacocoAnt - The Jacoco agent to use to get coverage data.
    \--- org.jacoco:org.jacoco.ant:0.8.7
    
    ...
    
    debugRuntimeClasspath - Runtime classpath of compilation 'debug' (target  (androidJvm)).
    +--- org.jacoco:org.jacoco.agent:0.8.7
    
    ...
    
    debugUnitTestRuntimeClasspath - Runtime classpath of compilation 'debugUnitTest' (target  (androidJvm)).
    +--- org.jacoco:org.jacoco.agent:0.8.7
    
    ...
    
    jacocoAgent - The Jacoco agent to use to get coverage data.
    \--- org.jacoco:org.jacoco.agent:0.8.7
    
    ...
    
    jacocoAnt - The Jacoco ant tasks to use to get execute Gradle tasks.
    \--- org.jacoco:org.jacoco.ant:0.8.7
    ```
* With AGP `7.0.0`, our requested version of jacoco isn't used. This can be confirmed by running `./gradlew :app:dependencies`: we see that version `0.8.3` is used for some gradle tasks. For example:
    ```
    androidJacocoAnt - The Jacoco agent to use to get coverage data.
    \--- org.jacoco:org.jacoco.ant:0.8.3

    ...

    debugRuntimeClasspath - Runtime classpath of compilation 'debug' (target  (androidJvm)).
    +--- org.jacoco:org.jacoco.agent:0.8.3
    
    ...
    
    debugUnitTestRuntimeClasspath - Runtime classpath of compilation 'debugUnitTest' (target  (androidJvm)).
    +--- org.jacoco:org.jacoco.agent:0.8.3
    
    ...
    
    jacocoAgent - The Jacoco agent to use to get coverage data.
    \--- org.jacoco:org.jacoco.agent:0.8.7
    
    ...
    
    jacocoAnt - The Jacoco ant tasks to use to get execute Gradle tasks.
    \--- org.jacoco:org.jacoco.ant:0.8.7

    ```

