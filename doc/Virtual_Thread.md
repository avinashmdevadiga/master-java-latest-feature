concept covered:
How Os works?
what is process?
- it an instance of computer program that is being executed. It contains the program code and its current activity. A process can have multiple threads of execution, which are the smallest unit of processing that can be scheduled by an operating system.
- its a heavy weight entity because it requires its own memory space, system resources, and scheduling overhead. Each process has its own address space, file descriptors, and other resources that are isolated from other processes.

what thread?
-its a part of process. A process can contain one or more threads, which share the same memory space and resources of the process. Threads are lightweight entities because they share the same address space and resources of the process, which makes them more efficient for concurrent execution.

how Scheduler works?
- The scheduler is a component of the operating system that manages the execution of processes and threads.
- It decides which process or thread to run next based on scheduling algorithms and policies.
- The scheduler maintains a queue of ready processes and threads, and selects the next one to run based on factors such as priority, fairness, and resource availability.
- It also handles context switching, which involves saving the state of the currently running process or thread and restoring the state of the next one

java thread is a wrapper of OS thread. It is a lightweight process that runs within a Java Virtual Machine (JVM) and is managed by the JVM's thread scheduler. Java threads can be created using the Thread class or by implementing the Runnable interface. Each Java thread has its own call stack, program counter, and local variables, but shares the same heap memory space with other threads in the same process. Java threads can be synchronized to prevent race conditions and ensure thread safety when accessing shared resources.

heap vs stack memory:
- Heap memory is a region of memory used for dynamic memory allocation, where objects are created and stored. It is managed by the garbage collector and can grow or shrink in size as needed. Heap memory is shared among all threads in a process, and access to it must be synchronized to prevent data corruption.
  - Stack memory is a region of memory used for storing local variables and function call information.  
  - Each thread has its own stack memory, which is allocated when the thread is created and deallocated when the thread terminates. Stack memory is organized in a last-in-first-out (LIFO) manner, and access to it is fast and efficient. However, stack memory is limited in size and can lead to stack overflow errors if too much memory is used.
  

