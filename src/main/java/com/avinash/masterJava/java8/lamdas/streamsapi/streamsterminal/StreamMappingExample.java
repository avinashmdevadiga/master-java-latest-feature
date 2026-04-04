package com.avinash.masterJava.java8.lamdas.streamsapi.streamsterminal;

import com.avinash.masterJava.java8.lamdas.data.Student;
import com.avinash.masterJava.java8.lamdas.data.StudentDatabase;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Use Case: Demonstrates Collectors.mapping() — a downstream collector that applies a
 * mapping function before collecting into a target collection.
 * It is equivalent to stream().map(fn).collect(toList()) but is composable as a
 * downstream collector inside groupingBy() or other collectors.
 */
public class StreamMappingExample {

    private static final Logger LOGGER = Logger.getLogger(StreamMappingExample.class.getName());

    private static List<String> studentNameList(){
        LOGGER.info("StreamMappingExample :: studentNameList - Collectors.mapping → List");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.mapping(Student::getName, Collectors.toList()));
    }

    private static Set<String> studentNameSet(){
        LOGGER.info("StreamMappingExample :: studentNameSet - Collectors.mapping → Set (dedup)");
        return StudentDatabase.getStudents().stream()
                .collect(Collectors.mapping(Student::getName, Collectors.toSet()));
    }

    public static void main(String[] args) {
        LOGGER.info("StreamMappingExample :: main started");
        System.out.println("StudentNameList : "+studentNameList());
        System.out.println("StudentNameSet : "+studentNameSet());
        LOGGER.info("StreamMappingExample :: main finished");
    }
}
