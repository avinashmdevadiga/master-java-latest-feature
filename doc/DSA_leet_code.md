# Java DSA Mastery Guide — Concepts + 150+ LeetCode Problems
### For Experienced Java Developers (Interview Preparation)

---

## How to Use This Guide

- **Part 1** builds deep intuition for every core DSA concept in Java — implementation, *why* it's used, complexity, and a real-world analogy.
- **Part 2** is a curated bank of **150+ LeetCode-style problems**, grouped by category, each with a well-commented Java solution, the reasoning behind the approach, key tricks/pitfalls, and complexity analysis.
- **Part 3** is a rapid-fire DSA interview Q&A section for final revision.

---

# PART 1: DSA Concepts in Java (Deep Explanation)

## Table of Contents (Part 1)
1. [Arrays](#1-arrays)
2. [Strings](#2-strings)
3. [LinkedList](#3-linkedlist)
4. [Stack](#4-stack)
5. [Queue](#5-queue)
6. [HashMap, HashSet, TreeMap](#6-hashmap-hashset-treemap)
7. [Recursion & Backtracking](#7-recursion--backtracking)
8. [Searching](#8-searching)
9. [Sorting](#9-sorting)
10. [Trees](#10-trees)
11. [Graphs](#11-graphs)
12. [Dynamic Programming](#12-dynamic-programming)
13. [Greedy Algorithms](#13-greedy-algorithms)
14. [Divide and Conquer](#14-divide-and-conquer)

---

## 1. Arrays

**What:** Contiguous, fixed-size (in Java, arrays are fixed-length) memory blocks holding elements of the same type, accessed by index in O(1).

```java
int[] arr = new int[10];          // fixed size, default-initialized to 0
int[] arr2 = {1, 2, 3, 4, 5};     // literal init
int val = arr2[2];                // O(1) random access

// Dynamic array (Java's resizable array): ArrayList
List<Integer> list = new ArrayList<>();
list.add(10);                     // amortized O(1) - doubles backing array when full
```

**Why used:** Best cache locality of any structure (contiguous memory → CPU cache-friendly), O(1) random access. Ideal when the size is known/bounded and random access dominates over insert/delete in the middle.

**Complexity:**
| Operation | Array | ArrayList |
|---|---|---|
| Access by index | O(1) | O(1) |
| Search (unsorted) | O(n) | O(n) |
| Insert/Delete at end | N/A (fixed) | Amortized O(1) |
| Insert/Delete at middle | O(n) shift | O(n) shift |

**Real-world example:** A trade-book snapshot for a fixed set of 500 instruments, indexed by instrument ID — O(1) lookup for price updates, far faster than a linked structure for this access pattern.

---

## 2. Strings

**What:** In Java, `String` is an immutable sequence of `char` (or bytes internally since Java 9's compact strings). Every "mutation" creates a new object.

```java
String s = "hello";
String s2 = s.concat(" world");   // new object; s unchanged
StringBuilder sb = new StringBuilder();
for (char c : "hello".toCharArray()) sb.append(c);
String result = sb.toString();    // mutable builder avoids O(n^2) concatenation in loops
```

**Why immutable:** Safe for use as `HashMap` keys (hashcode cached once), thread-safety without synchronization, security (can't be altered after being passed across trust boundaries), and enables the JVM string pool for memory reuse.

**Complexity:** Concatenating `n` strings in a loop with `+` is **O(n²)** (each `+` creates a new String and copies). Using `StringBuilder.append()` in a loop then one `toString()` is **O(n)**.

**Real-world example:** Building a large CSV/JSON payload for a regulatory report — always use `StringBuilder`, never `String +=` inside a loop over thousands of records.

---

## 3. LinkedList

**What:** A sequence of nodes, each holding data + a reference to the next (singly) or next+previous (doubly) node. No contiguous memory requirement.

```java
class Node<T> {
    T data;
    Node<T> next;
    Node(T data) { this.data = data; }
}

class SinglyLinkedList<T> {
    Node<T> head;
    void addFirst(T data) {
        Node<T> n = new Node<>(data);
        n.next = head;
        head = n;                  // O(1) insert at head
    }
    void reverse() {
        Node<T> prev = null, curr = head;
        while (curr != null) {
            Node<T> next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;                // O(n) time, O(1) space
    }
}
```

**Why used:** O(1) insertion/deletion once you have a reference to the node (no shifting, unlike arrays). Java's built-in `LinkedList` implements both `List` and `Deque`.

**Complexity:** Access by index O(n), insert/delete at known node O(1), search O(n).

**Real-world example:** An LRU cache's internal doubly linked list (paired with a `HashMap`) — moving a recently-accessed node to the front is O(1), which an array-based structure can't match without shifting.

---

## 4. Stack

**What:** LIFO (Last In, First Out) structure. In Java, use `Deque` (`ArrayDeque`) as a stack — **avoid the legacy `Stack` class**, which is synchronized (unneeded overhead) and extends `Vector` (odd, dated design).

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1); stack.push(2);
int top = stack.pop();   // 2, O(1)
int peek = stack.peek(); // 1, O(1)
```

**Why used:** Naturally models "undo" operations, function call frames (the actual JVM call stack), matching/nesting problems (parentheses, expression evaluation), and DFS (iterative).

**Complexity:** Push/pop/peek all O(1).

**Real-world example:** Browser back-button history; call stack unwinding during exception propagation; validating balanced brackets in a config/JSON parser.

---

## 5. Queue

**What:** FIFO (First In, First Out). Variants: simple queue, **Deque** (double-ended), **PriorityQueue** (min/max-heap ordered, not FIFO).

```java
Queue<Integer> queue = new LinkedList<>();
queue.offer(1); queue.offer(2);
int front = queue.poll();  // 1, O(1)

PriorityQueue<Integer> minHeap = new PriorityQueue<>();          // natural ordering
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
minHeap.offer(5); minHeap.offer(1); minHeap.offer(3);
minHeap.poll(); // 1 - smallest first, O(log n)
```

**Why used:** Task scheduling (FIFO fairness), BFS traversal, producer-consumer buffering, and `PriorityQueue` for "always process the most urgent/smallest/largest item next" (Dijkstra, top-K problems, merge-K-lists).

**Complexity:** `offer`/`poll` O(1) for simple queue; O(log n) for `PriorityQueue` (heap-backed).

**Real-world example:** A `PriorityQueue` processing regulatory alerts ordered by severity/deadline — always pop the most urgent item first regardless of arrival order.

---

## 6. HashMap, HashSet, TreeMap

```java
Map<String, Integer> map = new HashMap<>();     // O(1) avg get/put, no ordering
Set<String> set = new HashSet<>();               // O(1) avg, backed by a HashMap internally
Map<String, Integer> sorted = new TreeMap<>();   // O(log n) get/put, keys sorted (Red-Black tree)
Map<String, Integer> insertionOrdered = new LinkedHashMap<>(); // O(1), preserves insertion order
```

**Why used:**
- `HashMap`/`HashSet`: fastest average-case lookup when order doesn't matter (frequency counting, deduplication, caching).
- `TreeMap`/`TreeSet`: when you need sorted order, range queries (`floorKey`, `ceilingKey`, `headMap`), or ordered iteration.
- `LinkedHashMap`: when insertion/access order must be preserved (e.g., implementing an LRU cache via `removeEldestEntry`).

**Complexity:**
| Structure | Get/Put | Ordering |
|---|---|---|
| `HashMap` | O(1) avg, O(n) worst (all in one bucket, pre-treeification) | None |
| `TreeMap` | O(log n) | Sorted by key |
| `LinkedHashMap` | O(1) avg | Insertion or access order |

**Real-world example:** `TreeMap<LocalDate, List<Trade>>` for retrieving all trades within a settlement date range using `subMap()` in O(log n + k) instead of scanning the whole book.

---

## 7. Recursion & Backtracking

**Recursion:** A function calling itself with a smaller subproblem, requiring a **base case** to terminate.

```java
int factorial(int n) {
    if (n <= 1) return 1;          // base case
    return n * factorial(n - 1);   // recursive case
}
```

**Backtracking:** Recursion + explicit "undo" — explore a choice, recurse, then **revert the choice** before trying the next one. Used for combinatorial search (permutations, subsets, N-Queens, Sudoku).

```java
void backtrack(List<Integer> path, boolean[] used, int[] nums, List<List<Integer>> result) {
    if (path.size() == nums.length) { result.add(new ArrayList<>(path)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true;
        path.add(nums[i]);
        backtrack(path, used, nums, result);   // explore
        path.remove(path.size() - 1);          // un-choose (backtrack)
        used[i] = false;
    }
}
```

**Why used:** Any problem framed as "try all valid combinations/orderings and prune invalid branches early" — the pruning (early return on invalid state) is what keeps it tractable versus brute force.

**Complexity:** Varies widely — often exponential (e.g., O(n!) for permutations, O(2ⁿ) for subsets) — backtracking's value is in pruning the search space early, not changing the theoretical worst case.

**Real-world example:** Generating all valid trade-allocation splits across sub-accounts that sum to a total notional, pruning branches early once a partial sum exceeds the target.

---

## 8. Searching

**Linear Search:** O(n) — scan every element. Only choice on unsorted data.

**Binary Search:** O(log n) — requires sorted data; repeatedly halve the search space.

```java
int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;   // avoids overflow vs (lo+hi)/2
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

**Variations:**
- **Lower bound / upper bound** (first/last occurrence, or insertion point) — used in `Arrays.binarySearch` combined with custom comparator logic.
- **Binary search on answer** — when the search space isn't the array itself but a range of possible answers (e.g., "minimum capacity to ship packages within D days") — monotonic predicate + binary search.

**Complexity:** Linear O(n); Binary O(log n) time, O(1) space (iterative).

**Real-world example:** Binary-searching a sorted list of settlement dates to find the first trade on/after a cutoff date — O(log n) versus scanning the whole book.

---

## 9. Sorting

| Algorithm | Time (avg) | Time (worst) | Space | Stable? | Notes |
|---|---|---|---|---|---|
| Bubble Sort | O(n²) | O(n²) | O(1) | Yes | Educational only |
| Selection Sort | O(n²) | O(n²) | O(1) | No | Minimizes swaps |
| Insertion Sort | O(n²) | O(n²) | O(1) | Yes | Fast for nearly-sorted/small data |
| Merge Sort | O(n log n) | O(n log n) | O(n) | Yes | Predictable, good for linked lists |
| Quick Sort | O(n log n) | O(n²) | O(log n) | No | Fast in practice, in-place |
| Heap Sort | O(n log n) | O(n log n) | O(1) | No | In-place, no worst-case blowup |
| Counting Sort | O(n + k) | O(n + k) | O(k) | Yes | k = range of values, non-comparison based |
| Radix Sort | O(d·(n+k)) | O(d·(n+k)) | O(n + k) | Yes | d = number of digits, integers/fixed-length keys |

```java
// Merge Sort
void mergeSort(int[] arr, int lo, int hi) {
    if (lo >= hi) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(arr, lo, mid);
    mergeSort(arr, mid + 1, hi);
    merge(arr, lo, mid, hi);
}
void merge(int[] arr, int lo, int mid, int hi) {
    int[] temp = new int[hi - lo + 1];
    int i = lo, j = mid + 1, k = 0;
    while (i <= mid && j <= hi) temp[k++] = arr[i] <= arr[j] ? arr[i++] : arr[j++];
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= hi) temp[k++] = arr[j++];
    System.arraycopy(temp, 0, arr, lo, temp.length);
}

// Quick Sort (Lomuto partition)
void quickSort(int[] arr, int lo, int hi) {
    if (lo >= hi) return;
    int pivotIdx = partition(arr, lo, hi);
    quickSort(arr, lo, pivotIdx - 1);
    quickSort(arr, pivotIdx + 1, hi);
}
int partition(int[] arr, int lo, int hi) {
    int pivot = arr[hi], i = lo;
    for (int j = lo; j < hi; j++) {
        if (arr[j] < pivot) { swap(arr, i, j); i++; }
    }
    swap(arr, i, hi);
    return i;
}
```

**Why it matters in interviews:** Java's `Arrays.sort()` uses **Dual-Pivot Quicksort** for primitives (not stable, no allocation guarantee needed since primitives have no identity) and **TimSort** (a hybrid merge/insertion sort) for objects (`Collections.sort`, `Arrays.sort(Object[])`) — stable, because object equality/identity matters for consumers.

**Real-world example:** Counting/Radix sort is ideal for sorting millions of trade records by a bounded integer key (e.g., a 6-digit product code) — O(n) instead of O(n log n), a meaningful win at scale in batch reporting jobs.

---

## 10. Trees

### Binary Tree & BST

```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

// BST insert
TreeNode insert(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insert(root.left, val);
    else root.right = insert(root.right, val);
    return root;
}
```
- **BST property:** left subtree < node < right subtree — enables O(log n) search/insert/delete **when balanced**.
- **Worst case:** a degenerate (skewed) BST from sorted insertion order becomes O(n) — like a linked list.

### AVL Tree (Self-Balancing BST)

Maintains a **balance factor** (height difference between left/right subtrees) of at most 1 via rotations (left, right, left-right, right-left) after every insert/delete — guarantees O(log n) worst-case for all operations, unlike a plain BST.

```java
int height(Node n) { return n == null ? 0 : n.height; }
int getBalance(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }

Node rightRotate(Node y) {
    Node x = y.left, T2 = x.right;
    x.right = y; y.left = T2;
    y.height = Math.max(height(y.left), height(y.right)) + 1;
    x.height = Math.max(height(x.left), height(x.right)) + 1;
    return x; // new subtree root
}
```

### Segment Tree

Supports range queries (sum/min/max over a range) and point/range updates in O(log n), versus O(n) for a naive array scan per query.

```java
class SegmentTree {
    int[] tree;
    int n;
    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }
    void build(int[] arr, int node, int lo, int hi) {
        if (lo == hi) { tree[node] = arr[lo]; return; }
        int mid = (lo + hi) / 2;
        build(arr, 2 * node, lo, mid);
        build(arr, 2 * node + 1, mid + 1, hi);
        tree[node] = tree[2 * node] + tree[2 * node + 1]; // sum segment tree
    }
    int query(int node, int lo, int hi, int l, int r) {
        if (r < lo || hi < l) return 0;               // no overlap
        if (l <= lo && hi <= r) return tree[node];      // total overlap
        int mid = (lo + hi) / 2;
        return query(2 * node, lo, mid, l, r) + query(2 * node + 1, mid + 1, hi, l, r);
    }
    void update(int node, int lo, int hi, int idx, int val) {
        if (lo == hi) { tree[node] = val; return; }
        int mid = (lo + hi) / 2;
        if (idx <= mid) update(2 * node, lo, mid, idx, val);
        else update(2 * node + 1, mid + 1, hi, idx, val);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }
}
```

### Trie (Prefix Tree)

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}
class Trie {
    TrieNode root = new TrieNode();
    void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new TrieNode();
            node = node.children[i];
        }
        node.isEnd = true;
    }
    boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isEnd;
    }
    boolean startsWith(String prefix) { return find(prefix) != null; }
    private TrieNode find(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) return null;
        }
        return node;
    }
}
```

**Complexity summary:**
| Structure | Search | Insert | Space |
|---|---|---|---|
| BST (balanced) | O(log n) | O(log n) | O(n) |
| BST (skewed) | O(n) | O(n) | O(n) |
| AVL Tree | O(log n) guaranteed | O(log n) guaranteed | O(n) |
| Segment Tree | O(log n) range query | O(log n) update | O(n) |
| Trie | O(L) where L = word length | O(L) | O(alphabet × nodes) |

**Real-world example:** A Trie powers autocomplete for instrument ticker search in a trading UI; a Segment Tree powers "sum of trade notionals in date range [i, j]" queries updated in real time as new trades arrive.

---

## 11. Graphs

**Representations:**
```java
// Adjacency List (preferred for sparse graphs — most real-world graphs)
Map<Integer, List<Integer>> graph = new HashMap<>();
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// Adjacency Matrix (good for dense graphs, O(1) edge lookup, O(V^2) space)
int[][] matrix = new int[n][n];
```

**DFS (Depth-First Search):**
```java
void dfs(int node, Set<Integer> visited, Map<Integer, List<Integer>> graph) {
    if (visited.contains(node)) return;
    visited.add(node);
    for (int neighbor : graph.getOrDefault(node, List.of())) dfs(neighbor, visited, graph);
}
```
**BFS (Breadth-First Search):**
```java
void bfs(int start, Map<Integer, List<Integer>> graph) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new LinkedList<>();
    queue.offer(start); visited.add(start);
    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (visited.add(neighbor)) queue.offer(neighbor); // add() returns false if already present
        }
    }
}
```

**Dijkstra's Algorithm** (shortest path, non-negative weights):
```java
int[] dijkstra(int src, int n, Map<Integer, List<int[]>> graph) { // graph: node -> [neighbor, weight]
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // [node, dist]
    pq.offer(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        if (curr[1] > dist[curr[0]]) continue; // stale entry
        for (int[] edge : graph.getOrDefault(curr[0], List.of())) {
            int next = edge[0], weight = edge[1];
            if (dist[curr[0]] + weight < dist[next]) {
                dist[next] = dist[curr[0]] + weight;
                pq.offer(new int[]{next, dist[next]});
            }
        }
    }
    return dist;
}
```

**Bellman-Ford** (handles negative weights, detects negative cycles): relax all `E` edges `V-1` times; O(V·E).

**Floyd-Warshall** (all-pairs shortest path): O(V³), simple triple-nested DP over intermediate vertices.

**Union-Find (Disjoint Set Union)** — for connectivity/cycle detection:
```java
class UnionFind {
    int[] parent, rank;
    UnionFind(int n) {
        parent = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }
    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;               // already connected -> cycle if adding this edge
        if (rank[rx] < rank[ry]) { int t = rx; rx = ry; ry = t; }
        parent[ry] = rx;
        if (rank[rx] == rank[ry]) rank[rx]++;
        return true;
    }
}
```

**Complexity summary:**
| Algorithm | Time | Use Case |
|---|---|---|
| DFS/BFS | O(V + E) | Traversal, connectivity, shortest path (unweighted) |
| Dijkstra | O((V+E) log V) with heap | Shortest path, non-negative weights |
| Bellman-Ford | O(V·E) | Negative weights, cycle detection |
| Floyd-Warshall | O(V³) | All-pairs shortest paths, small V |
| Union-Find | ~O(α(n)) per op (near O(1)) | Connectivity, Kruskal's MST, cycle detection |

**Real-world example:** Union-Find detects circular reference chains in a netting/collateral graph (Entity A owes B, B owes C, C owes A) before regulatory netting calculations; Dijkstra models "cheapest settlement route" across correspondent banking networks.

---

## 12. Dynamic Programming

**Core idea:** Break a problem into overlapping subproblems with optimal substructure, and **cache results** to avoid recomputation.

**Memoization (top-down):**
```java
Map<Integer, Long> memo = new HashMap<>();
long fib(int n) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n);
    long result = fib(n - 1) + fib(n - 2);
    memo.put(n, result);
    return result;   // O(n) time (vs O(2^n) naive), O(n) space
}
```
**Tabulation (bottom-up):**
```java
long fibTab(int n) {
    if (n <= 1) return n;
    long[] dp = new long[n + 1];
    dp[1] = 1;
    for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
    return dp[n];    // O(n) time, O(n) space (or O(1) with 2-variable rolling)
}
```

**Common patterns:**
- **0/1 Knapsack** — include/exclude decision per item.
- **Unbounded Knapsack** — coin change style, item reusable.
- **Longest Common Subsequence / Longest Increasing Subsequence** — 2D or O(n log n) with patience sorting for LIS.
- **Kadane's Algorithm** — max subarray sum, O(n).
- **Grid DP** — unique paths, min path sum.
- **State-machine DP** — buy/sell stock with cooldown/fees.
- **Interval DP** — matrix chain multiplication, burst balloons.
- **Digit DP / Bitmask DP** — TSP-style problems with small n.

**Why used:** Turns exponential brute-force recursion into polynomial time by exploiting overlapping subproblems.

**Real-world example:** Optimal trade batching to minimize total transaction cost (a knapsack-style optimization) given a fixed daily processing capacity — classic 0/1 knapsack applied to operational cost minimization.

---

## 13. Greedy Algorithms

**Core idea:** Make the locally optimal choice at each step, hoping (and, for provably greedy-correct problems, guaranteeing) it leads to a globally optimal solution. Requires proving the **greedy-choice property** and **optimal substructure** hold — not all problems are greedy-solvable.

```java
// Activity Selection (max non-overlapping intervals)
int maxActivities(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // sort by end time - key greedy insight
    int count = 1, lastEnd = intervals[0][1];
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] >= lastEnd) { count++; lastEnd = intervals[i][1]; }
    }
    return count; // O(n log n)
}
```

**Classic greedy problems:** Activity selection, Huffman coding, Fractional knapsack, Dijkstra (greedy + heap), Kruskal's/Prim's MST, Job sequencing with deadlines.

**Real-world example:** Scheduling the maximum number of non-overlapping trade settlement windows on a single processing thread — sort by end time, greedily pick the earliest-finishing compatible window.

---

## 14. Divide and Conquer

**Core idea:** Split the problem into independent subproblems, solve recursively, and **combine** results. Differs from DP in that subproblems are typically **non-overlapping** (no need to cache).

```java
// Merge Sort and Quick Sort (above) are classic D&C.
// Another example: finding max subarray sum via D&C (Kadane's is simpler, but D&C teaches the pattern)
int maxSubArray(int[] nums, int lo, int hi) {
    if (lo == hi) return nums[lo];
    int mid = (lo + hi) / 2;
    int leftMax = maxSubArray(nums, lo, mid);
    int rightMax = maxSubArray(nums, mid + 1, hi);
    int crossMax = maxCrossingSum(nums, lo, mid, hi);
    return Math.max(Math.max(leftMax, rightMax), crossMax); // O(n log n)
}
```

**Classic D&C problems:** Merge sort, Quick sort, Binary search, Closest pair of points, Karatsuba multiplication, Strassen's matrix multiplication, Majority element (Boyer-Moore is O(n) simpler, but D&C variant exists).

**Real-world example:** Parallel merge sort of large trade files across multiple threads/cores (via `ForkJoinPool`) — split, sort each half independently in parallel, then merge — directly leverages the D&C structure for parallelism.

---


# PART 2: 150+ LeetCode Problems (Java Solutions)

> **How to use this section:** Read the **Problem** and **Example**, try solving it yourself first, then scroll down to check the **Java Solution**, **Approach**, **Tricks/Pitfalls**, and **Complexity**.

## Table of Contents (Part 2)
0. [Warm-Up: 25 Basic String Programs](#warm-up-25-basic-string-programs-basic--advanced) (basic → advanced)
1. [Arrays & Strings](#category-1-arrays--strings) (27 problems)
2. [Linked List](#category-2-linked-list) (13 problems)
3. [Stack & Queue](#category-3-stack--queue) (13 problems)
4. [Hashing](#category-4-hashing) (13 problems)
5. [Binary Tree & BST](#category-5-binary-tree--bst) (20 problems)
6. [Graphs](#category-6-graphs) (16 problems)
7. [Dynamic Programming](#category-7-dynamic-programming) (20 problems)
8. [Greedy](#category-8-greedy) (11 problems)
9. [Backtracking](#category-9-backtracking) (12 problems)
10. [Advanced (Segment Tree, Trie, Union-Find, BIT)](#category-10-advanced-segment-tree-trie-union-find-bit) (13 problems)

---

## Warm-Up: 25 Basic String Programs (Basic → Advanced)

> Before diving into the 158 LeetCode-style problems, these 25 foundational string programs build the muscle memory (char arrays, `StringBuilder`, two pointers, frequency maps) that almost every string-based LeetCode problem later relies on. They're ordered from basic to advanced. **Try each one yourself first**, then check the solution below it.

### W1. Reverse a String
**Problem:** Given a string, return it reversed.
**Example:**
```
Input: s = "hello"
Output: "olleh"
```
```java
public String reverse(String s) {
    StringBuilder sb = new StringBuilder(s);
    return sb.reverse().toString();
}
// Manual two-pointer version (what interviewers usually want):
public String reverseManual(String s) {
    char[] chars = s.toCharArray();
    int left = 0, right = chars.length - 1;
    while (left < right) {
        char temp = chars[left]; chars[left] = chars[right]; chars[right] = temp;
        left++; right--;
    }
    return new String(chars);
}
```
**Approach:** Two pointers swap characters from both ends moving inward.
**Complexity:** Time O(n), Space O(n) for the char array (O(1) extra beyond the output).

### W2. Check if a String is a Palindrome
**Problem:** Determine if a string reads the same forwards and backwards.
**Example:**
```
Input: s = "madam"
Output: true
```
```java
public boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) return false;
        left++; right--;
    }
    return true;
}
```
**Approach:** Two pointers compare characters from both ends; any mismatch fails immediately.
**Complexity:** Time O(n), Space O(1).

### W3. Count Vowels and Consonants
**Problem:** Count the number of vowels and consonants in a string.
**Example:**
```
Input: s = "Hello World"
Output: vowels = 3, consonants = 7
```
```java
public void countVowelsConsonants(String s) {
    int vowels = 0, consonants = 0;
    String vowelSet = "aeiouAEIOU";
    for (char c : s.toCharArray()) {
        if (!Character.isLetter(c)) continue;
        if (vowelSet.indexOf(c) != -1) vowels++;
        else consonants++;
    }
    System.out.println("vowels = " + vowels + ", consonants = " + consonants);
}
```
**Approach:** Iterate once, classify each letter using a lookup string.
**Complexity:** Time O(n), Space O(1).

### W4. Count Occurrences of Each Character
**Problem:** Count how many times each character appears in a string.
**Example:**
```
Input: s = "programming"
Output: {p=1, r=2, o=1, g=2, a=1, m=2, i=1, n=1}
```
```java
public Map<Character, Integer> countChars(String s) {
    Map<Character, Integer> freq = new LinkedHashMap<>(); // preserves first-seen order for readable output
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    return freq;
}
```
**Approach:** A frequency map built in a single pass; `merge` handles the "insert or increment" logic in one call.
**Complexity:** Time O(n), Space O(k) where k = distinct characters.

### W5. Remove Duplicate Characters from a String
**Problem:** Remove duplicate characters, keeping only the first occurrence of each.
**Example:**
```
Input: s = "programming"
Output: "progamin"
```
```java
public String removeDuplicates(String s) {
    Set<Character> seen = new LinkedHashSet<>();
    for (char c : s.toCharArray()) seen.add(c);
    StringBuilder sb = new StringBuilder();
    for (char c : seen) sb.append(c);
    return sb.toString();
}
```
**Approach:** `LinkedHashSet` naturally deduplicates while preserving insertion order.
**Complexity:** Time O(n), Space O(k).

### W6. Find the First Non-Repeating Character
**Problem:** Find the first character in a string that doesn't repeat.
**Example:**
```
Input: s = "swiss"
Output: 'w'
```
```java
public char firstNonRepeating(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    for (char c : s.toCharArray()) if (freq.get(c) == 1) return c;
    throw new IllegalArgumentException("No non-repeating character");
}
```
**Approach:** Count frequencies first, then scan again in original order to find the first count-of-1 character.
**Complexity:** Time O(n), Space O(k).

### W7. Check if Two Strings are Anagrams
**Problem:** Determine if two strings are anagrams of each other (same letters, same counts, any order).
**Example:**
```
Input: s1 = "listen", s2 = "silent"
Output: true
```
```java
public boolean areAnagrams(String s1, String s2) {
    if (s1.length() != s2.length()) return false;
    int[] counts = new int[26];
    for (int i = 0; i < s1.length(); i++) { counts[s1.charAt(i) - 'a']++; counts[s2.charAt(i) - 'a']--; }
    for (int c : counts) if (c != 0) return false;
    return true;
}
```
**Approach:** Increment for one string, decrement for the other; if they're anagrams, every count returns to zero.
**Complexity:** Time O(n), Space O(1).

### W8. Count Words in a Sentence
**Problem:** Count the number of words in a sentence.
**Example:**
```
Input: s = "  The quick  brown fox  "
Output: 4
```
```java
public int countWords(String s) {
    String trimmed = s.trim();
    if (trimmed.isEmpty()) return 0;
    return trimmed.split("\\s+").length; // handles multiple/irregular spaces
}
```
**Approach:** Trim leading/trailing whitespace, then split on one-or-more whitespace characters (regex `\s+`) to handle irregular spacing.
**Complexity:** Time O(n), Space O(n) for the split array.

### W9. Reverse Each Word in a Sentence (Keep Word Order)
**Problem:** Reverse the letters of each word individually, but keep the word order unchanged.
**Example:**
```
Input: s = "Hello World"
Output: "olleH dlroW"
```
```java
public String reverseEachWord(String s) {
    String[] words = s.split(" ");
    StringBuilder result = new StringBuilder();
    for (String word : words) {
        result.append(new StringBuilder(word).reverse());
        result.append(" ");
    }
    return result.toString().trim();
}
```
**Approach:** Split into words, reverse each independently using `StringBuilder.reverse()`, then rejoin with spaces.
**Complexity:** Time O(n), Space O(n).

### W10. Check if a String Contains Only Digits
**Problem:** Determine if a string consists only of numeric digits.
**Example:**
```
Input: s = "12345"
Output: true

Input: s = "123a5"
Output: false
```
```java
public boolean isNumeric(String s) {
    if (s.isEmpty()) return false;
    for (char c : s.toCharArray()) if (!Character.isDigit(c)) return false;
    return true;
}
```
**Approach:** Check every character against `Character.isDigit()`; a regex (`s.matches("\\d+")`) is a one-line alternative worth mentioning.
**Complexity:** Time O(n), Space O(1).

### W11. Convert String to Title Case
**Problem:** Capitalize the first letter of every word.
**Example:**
```
Input: s = "the quick brown fox"
Output: "The Quick Brown Fox"
```
```java
public String toTitleCase(String s) {
    String[] words = s.split(" ");
    StringBuilder result = new StringBuilder();
    for (String word : words) {
        if (word.isEmpty()) continue;
        result.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1).toLowerCase())
              .append(" ");
    }
    return result.toString().trim();
}
```
**Approach:** Capitalize each word's first character, lowercase the rest, rejoin with spaces.
**Complexity:** Time O(n), Space O(n).

### W12. Check if a String is a Rotation of Another
**Problem:** Given two strings, determine if one is a rotation of the other.
**Example:**
```
Input: s1 = "waterbottle", s2 = "erbottlewat"
Output: true
```
```java
public boolean isRotation(String s1, String s2) {
    if (s1.length() != s2.length()) return false;
    return (s1 + s1).contains(s2);
}
```
**Approach:** Any rotation of `s1` is a substring of `s1` concatenated with itself — a classic trick that avoids manually trying every rotation offset.
**Complexity:** Time O(n) (amortized, given Java's substring search), Space O(n).

### W13. Find All Permutations of a String
**Problem:** Print/return all permutations of a string.
**Example:**
```
Input: s = "abc"
Output: ["abc","acb","bac","bca","cab","cba"]
```
```java
public List<String> permutations(String s) {
    List<String> result = new ArrayList<>();
    backtrack(s.toCharArray(), 0, result);
    return result;
}
private void backtrack(char[] chars, int start, List<String> result) {
    if (start == chars.length) { result.add(new String(chars)); return; }
    for (int i = start; i < chars.length; i++) {
        swap(chars, start, i);
        backtrack(chars, start + 1, result);
        swap(chars, start, i); // backtrack
    }
}
private void swap(char[] chars, int i, int j) { char t = chars[i]; chars[i] = chars[j]; chars[j] = t; }
```
**Approach:** In-place backtracking — swap each character into the current position, recurse on the rest, then swap back.
**Complexity:** Time O(n · n!), Space O(n) recursion depth.

### W14. Check if a String Has All Unique Characters
**Problem:** Determine if a string has no repeated characters.
**Example:**
```
Input: s = "algorithm"
Output: true

Input: s = "hello"
Output: false
```
```java
public boolean hasAllUniqueChars(String s) {
    Set<Character> seen = new HashSet<>();
    for (char c : s.toCharArray()) if (!seen.add(c)) return false;
    return true;
}
```
**Approach:** `Set.add()` returns false on a duplicate, giving an early exit.
**Complexity:** Time O(n), Space O(min(n, charset size)).

### W15. Find the Longest Word in a Sentence
**Problem:** Find the longest word in a sentence.
**Example:**
```
Input: s = "The quick brown fox jumped"
Output: "quick" (or "brown"/"jumped" — first-longest found, all length 5)
```
```java
public String longestWord(String s) {
    String[] words = s.split(" ");
    String longest = "";
    for (String word : words) if (word.length() > longest.length()) longest = word;
    return longest;
}
```
**Approach:** Single pass tracking the longest word seen so far.
**Complexity:** Time O(n), Space O(n) for the split array.

### W16. String Compression (Run-Length Encoding)
**Problem:** Compress a string using counts of repeated characters (e.g., "aaabbc" → "a3b2c1").
**Example:**
```
Input: s = "aaabbbccd"
Output: "a3b3c2d1"
```
```java
public String compress(String s) {
    StringBuilder result = new StringBuilder();
    int i = 0;
    while (i < s.length()) {
        char curr = s.charAt(i);
        int count = 0;
        while (i < s.length() && s.charAt(i) == curr) { count++; i++; }
        result.append(curr).append(count);
    }
    return result.toString();
}
```
**Approach:** Scan the string once, counting consecutive runs of the same character, appending `char + count` per run.
**Complexity:** Time O(n), Space O(n).

### W17. Check if Two Strings are One Edit Away
**Problem:** Determine if two strings differ by at most one edit (insert, delete, or replace).
**Example:**
```
Input: s1 = "pale", s2 = "ple"
Output: true (one deletion)
```
```java
public boolean isOneEditAway(String s1, String s2) {
    if (Math.abs(s1.length() - s2.length()) > 1) return false;
    String shorter = s1.length() < s2.length() ? s1 : s2;
    String longer = s1.length() < s2.length() ? s2 : s1;
    int i = 0, j = 0;
    boolean foundDifference = false;
    while (i < shorter.length() && j < longer.length()) {
        if (shorter.charAt(i) != longer.charAt(j)) {
            if (foundDifference) return false;
            foundDifference = true;
            if (shorter.length() == longer.length()) i++; // replace: advance both
        } else i++;
        j++;
    }
    return true;
}
```
**Approach:** Walk both strings simultaneously; allow exactly one mismatch, advancing pointers according to whether it's a same-length (replace) or different-length (insert/delete) comparison.
**Complexity:** Time O(n), Space O(1).

### W18. Find the Longest Common Prefix Among an Array of Strings
**Problem:** Find the longest common prefix string among an array of strings.
**Example:**
```
Input: strs = ["flower","flow","flight"]
Output: "fl"
```
```java
public String longestCommonPrefix(String[] strs) {
    if (strs.length == 0) return "";
    String prefix = strs[0];
    for (int i = 1; i < strs.length; i++) {
        while (!strs[i].startsWith(prefix)) {
            prefix = prefix.substring(0, prefix.length() - 1);
            if (prefix.isEmpty()) return "";
        }
    }
    return prefix;
}
```
**Approach:** Start with the first string as the candidate prefix; shrink it whenever the next string doesn't start with it.
**Complexity:** Time O(n·m) where n = number of strings, m = shortest string length, Space O(1).

### W19. Check if a String is a Valid Shuffle of Two Other Strings
**Problem:** Given strings `s1`, `s2`, and `result`, determine if `result` is a valid interleaving (shuffle) of `s1` and `s2`.
**Example:**
```
Input: s1 = "abc", s2 = "def", result = "adbcef"
Output: true
```
```java
public boolean isInterleaving(String s1, String s2, String result) {
    if (s1.length() + s2.length() != result.length()) return false;
    Boolean[][] memo = new Boolean[s1.length() + 1][s2.length() + 1];
    return solve(s1, s2, result, 0, 0, memo);
}
private boolean solve(String s1, String s2, String result, int i, int j, Boolean[][] memo) {
    if (i == s1.length() && j == s2.length()) return true;
    if (memo[i][j] != null) return memo[i][j];
    boolean ans = false;
    int k = i + j;
    if (i < s1.length() && s1.charAt(i) == result.charAt(k)) ans = solve(s1, s2, result, i + 1, j, memo);
    if (!ans && j < s2.length() && s2.charAt(j) == result.charAt(k)) ans = solve(s1, s2, result, i, j + 1, memo);
    return memo[i][j] = ans;
}
```
**Approach:** Memoized recursion (DP) over positions `(i, j)` in `s1` and `s2`; at each step try consuming the next character from either string if it matches `result`'s next character.
**Complexity:** Time O(m·n), Space O(m·n).

### W20. Find All Substrings of a String
**Problem:** Generate all substrings of a given string.
**Example:**
```
Input: s = "abc"
Output: ["a","ab","abc","b","bc","c"]
```
```java
public List<String> allSubstrings(String s) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < s.length(); i++)
        for (int j = i + 1; j <= s.length(); j++)
            result.add(s.substring(i, j));
    return result;
}
```
**Approach:** Two nested loops over every valid (start, end) pair generate every contiguous substring.
**Complexity:** Time O(n²) substrings, O(n³) if counting character-copy cost of each substring, Space O(n²).

### W21. Convert a String to an Integer (Manual, No `Integer.parseInt`)
**Problem:** Implement basic string-to-integer conversion manually (handling sign, no overflow protection needed for this warm-up version).
**Example:**
```
Input: s = "-123"
Output: -123
```
```java
public int stringToInt(String s) {
    int i = 0, sign = 1, result = 0;
    if (s.charAt(0) == '-') { sign = -1; i = 1; }
    else if (s.charAt(0) == '+') { i = 1; }
    for (; i < s.length(); i++) result = result * 10 + (s.charAt(i) - '0');
    return sign * result;
}
```
**Approach:** Process an optional leading sign, then accumulate digits left to right using base-10 place-value math.
**Complexity:** Time O(n), Space O(1).

### W22. Find the Most Frequent Character in a String
**Problem:** Find the character that occurs most frequently in a string.
**Example:**
```
Input: s = "programming"
Output: 'r' or 'g' or 'm' (tied at 2 occurrences — return any per this simple version)
```
```java
public char mostFrequentChar(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    char result = s.charAt(0);
    int maxCount = 0;
    for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
        if (entry.getValue() > maxCount) { maxCount = entry.getValue(); result = entry.getKey(); }
    }
    return result;
}
```
**Approach:** Count frequencies, then scan the map once to find the max.
**Complexity:** Time O(n), Space O(k).

### W23. Check if a String Follows Balanced Bracket Rules (Basic Version)
**Problem:** Check if a string of only `(` and `)` is balanced.
**Example:**
```
Input: s = "(()())"
Output: true

Input: s = "(()"
Output: false
```
```java
public boolean isBalanced(String s) {
    int balance = 0;
    for (char c : s.toCharArray()) {
        if (c == '(') balance++;
        else balance--;
        if (balance < 0) return false; // more closes than opens at this point
    }
    return balance == 0;
}
```
**Approach:** A running counter suffices for single-bracket-type validation (no need for a full stack, unlike the multi-bracket-type Valid Parentheses problem later).
**Complexity:** Time O(n), Space O(1).

### W24. Find the Length of the Longest Substring With All Unique Characters
**Problem:** A simplified warm-up version of the sliding-window "longest substring without repeating characters" pattern.
**Example:**
```
Input: s = "abcabcbb"
Output: 3
```
```java
public int longestUniqueSubstring(String s) {
    Set<Character> window = new HashSet<>();
    int left = 0, maxLen = 0;
    for (int right = 0; right < s.length(); right++) {
        while (window.contains(s.charAt(right))) { window.remove(s.charAt(left)); left++; }
        window.add(s.charAt(right));
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```
**Approach:** Sliding window with a `Set`; shrink from the left one character at a time whenever a duplicate is about to enter the window.
**Complexity:** Time O(n), Space O(min(n, charset size)). *(This is the foundational warm-up for LeetCode Problem #19 later, which uses a faster last-seen-index jump instead of shrinking one step at a time.)*

### W25. Check if a String Can Be Formed by Another String's Characters (Basic Anagram-of-Subset Check)
**Problem:** Given `magazine` and `note`, determine if `note` can be constructed from `magazine`'s letters (each letter usable once) — the foundational warm-up for the Ransom Note pattern used later.
**Example:**
```
Input: magazine = "aabbcc", note = "abc"
Output: true

Input: magazine = "aabbcc", note = "abcabc"
Output: true

Input: magazine = "aabbcc", note = "aabbccc"
Output: false
```
```java
public boolean canConstruct(String note, String magazine) {
    int[] counts = new int[26];
    for (char c : magazine.toCharArray()) counts[c - 'a']++;
    for (char c : note.toCharArray()) {
        if (--counts[c - 'a'] < 0) return false;
    }
    return true;
}
```
**Approach:** Count available letters, then decrement as `note` consumes them — a shortage triggers an early false.
**Complexity:** Time O(n + m), Space O(1).

---

## Category 1: Arrays & Strings

### 1. Two Sum
**Problem:** Given an array of integers `nums` and an integer `target`, return the indices of the two numbers such that they add up to `target`. Each input has exactly one solution, and you may not use the same element twice.

**Example:**
```
Input: nums = [2, 7, 11, 15], target = 9
Output: [0, 1]
Explanation: nums[0] + nums[1] = 2 + 7 = 9
```
```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>(); // value -> index
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (seen.containsKey(complement)) return new int[]{seen.get(complement), i};
        seen.put(nums[i], i);
    }
    throw new IllegalArgumentException("No solution");
}
```
**Approach:** Single pass, store each seen value with its index; check for the complement before inserting.
**Tricks/Pitfalls:** Avoid nested loops (O(n²)); checking-then-inserting order prevents matching an element with itself.
**Complexity:** Time O(n), Space O(n).

### 2. Best Time to Buy and Sell Stock
**Problem:** Given an array `prices` where `prices[i]` is the price of a stock on day `i`, find the maximum profit from buying on one day and selling on a later day. Return 0 if no profit is possible.

**Example:**
```
Input: prices = [7, 1, 5, 3, 6, 4]
Output: 5
Explanation: Buy on day 2 (price=1), sell on day 5 (price=6), profit = 6 - 1 = 5.
```
```java
public int maxProfit(int[] prices) {
    int minPrice = Integer.MAX_VALUE, maxProfit = 0;
    for (int price : prices) {
        minPrice = Math.min(minPrice, price);
        maxProfit = Math.max(maxProfit, price - minPrice);
    }
    return maxProfit;
}
```
**Approach:** Track the minimum price seen so far; at each day compute profit if sold today.
**Tricks/Pitfalls:** Don't sort (loses day order); single pass beats O(n²) brute force.
**Complexity:** Time O(n), Space O(1).

### 3. Contains Duplicate
**Problem:** Given an integer array `nums`, return `true` if any value appears at least twice.

**Example:**
```
Input: nums = [1, 2, 3, 1]
Output: true
```
```java
public boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int n : nums) if (!seen.add(n)) return true; // add() returns false if already present
    return false;
}
```
**Approach:** `HashSet.add()`'s boolean return avoids a separate `contains()` check.
**Tricks/Pitfalls:** Sorting works too (O(n log n)) but HashSet is O(n) average.
**Complexity:** Time O(n), Space O(n).

### 4. Product of Array Except Self
**Problem:** Given an array `nums`, return an array where each element is the product of all other elements, without using division and in O(n) time.

**Example:**
```
Input: nums = [1, 2, 3, 4]
Output: [24, 12, 8, 6]
Explanation: result[0] = 2*3*4=24, result[1] = 1*3*4=12, etc.
```
```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1]; // prefix products
    int suffix = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= suffix;      // multiply by suffix product
        suffix *= nums[i];
    }
    return result;
}
```
**Approach:** Two passes — prefix products left-to-right, then multiply in suffix products right-to-left.
**Tricks/Pitfalls:** No division avoids the zero-element edge case entirely.
**Complexity:** Time O(n), Space O(1) excluding output array.

### 5. Maximum Subarray (Kadane's Algorithm)
**Problem:** Given an integer array `nums`, find the contiguous subarray (containing at least one number) with the largest sum, and return that sum.

**Example:**
```
Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
Output: 6
Explanation: The subarray [4, -1, 2, 1] has the largest sum = 6.
```
```java
public int maxSubArray(int[] nums) {
    int maxSoFar = nums[0], currentMax = nums[0];
    for (int i = 1; i < nums.length; i++) {
        currentMax = Math.max(nums[i], currentMax + nums[i]); // extend or restart
        maxSoFar = Math.max(maxSoFar, currentMax);
    }
    return maxSoFar;
}
```
**Approach:** At each index decide: extend the previous subarray, or start fresh from here (if previous sum is negative/unhelpful).
**Tricks/Pitfalls:** Classic DP-flavored greedy; O(n²)/O(n³) brute force is the naive trap.
**Complexity:** Time O(n), Space O(1).

### 6. Maximum Product Subarray
**Problem:** Given an integer array `nums`, find the contiguous subarray with the largest product, and return that product.

**Example:**
```
Input: nums = [2, 3, -2, 4]
Output: 6
Explanation: [2, 3] has the largest product = 6.
```
```java
public int maxProduct(int[] nums) {
    int maxP = nums[0], minP = nums[0], result = nums[0];
    for (int i = 1; i < nums.length; i++) {
        int curr = nums[i];
        if (curr < 0) { int t = maxP; maxP = minP; minP = t; } // swap on negative
        maxP = Math.max(curr, maxP * curr);
        minP = Math.min(curr, minP * curr);
        result = Math.max(result, maxP);
    }
    return result;
}
```
**Approach:** Track both max and min product ending at i, since a negative number can flip min to max.
**Tricks/Pitfalls:** Forgetting to track the minimum (for negative flips) is the classic bug.
**Complexity:** Time O(n), Space O(1).

### 7. Find Minimum in Rotated Sorted Array
**Problem:** Given a rotated sorted array `nums` (ascending, then rotated at some pivot, no duplicates), find the minimum element in O(log n).

**Example:**
```
Input: nums = [4, 5, 6, 7, 0, 1, 2]
Output: 0
```
```java
public int findMin(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) lo = mid + 1;  // min is in right half
        else hi = mid;                            // min is in left half (incl. mid)
    }
    return nums[lo];
}
```
**Approach:** Compare `mid` to `hi` to decide which half is unsorted (contains the rotation point).
**Tricks/Pitfalls:** Comparing to `hi`, not `lo`, correctly handles all rotation cases.
**Complexity:** Time O(log n), Space O(1).

### 8. Search in Rotated Sorted Array
**Problem:** Given a rotated sorted array `nums` and a `target`, search for the target and return its index, or -1 if not found, in O(log n).

**Example:**
```
Input: nums = [4, 5, 6, 7, 0, 1, 2], target = 0
Output: 4
```
```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) {              // left half sorted
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {                                   // right half sorted
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```
**Approach:** At each step, one half is guaranteed sorted — check if target lies in that sorted half.
**Tricks/Pitfalls:** Determine which half is sorted first, then decide direction — don't try to binary search "the rotation" separately.
**Complexity:** Time O(log n), Space O(1).

### 9. 3Sum
**Problem:** Given an integer array `nums`, return all unique triplets `[a, b, c]` such that `a + b + c == 0`.

**Example:**
```
Input: nums = [-1, 0, 1, 2, -1, -4]
Output: [[-1, -1, 2], [-1, 0, 1]]
```
```java
public List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicate anchors
        int lo = i + 1, hi = nums.length - 1;
        while (lo < hi) {
            int sum = nums[i] + nums[lo] + nums[hi];
            if (sum == 0) {
                result.add(List.of(nums[i], nums[lo], nums[hi]));
                while (lo < hi && nums[lo] == nums[lo + 1]) lo++; // skip dup
                while (lo < hi && nums[hi] == nums[hi - 1]) hi--; // skip dup
                lo++; hi--;
            } else if (sum < 0) lo++;
            else hi--;
        }
    }
    return result;
}
```
**Approach:** Sort, fix one element, two-pointer scan for the remaining pair summing to its negation.
**Tricks/Pitfalls:** Duplicate skipping at all three pointer levels is essential to avoid duplicate triplets.
**Complexity:** Time O(n²), Space O(1) excluding output/sort.

### 10. Container With Most Water
**Problem:** Given `n` non-negative integers `height` representing vertical lines at each index, find two lines that, together with the x-axis, form a container holding the most water.

**Example:**
```
Input: height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
Output: 49
Explanation: Lines at index 1 (height=8) and index 8 (height=7): area = min(8,7) * (8-1) = 49.
```
```java
public int maxArea(int[] height) {
    int lo = 0, hi = height.length - 1, max = 0;
    while (lo < hi) {
        int area = Math.min(height[lo], height[hi]) * (hi - lo);
        max = Math.max(max, area);
        if (height[lo] < height[hi]) lo++; else hi--; // move the shorter pointer
    }
    return max;
}
```
**Approach:** Two pointers from both ends; always move the shorter line inward since moving the taller one can't increase area.
**Tricks/Pitfalls:** The greedy "move shorter pointer" step is the key insight that avoids O(n²).
**Complexity:** Time O(n), Space O(1).

### 11. Trapping Rain Water
**Problem:** Given `n` non-negative integers representing an elevation map, compute how much water it can trap after raining.

**Example:**
```
Input: height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]
Output: 6
```
```java
public int trap(int[] height) {
    int lo = 0, hi = height.length - 1, leftMax = 0, rightMax = 0, water = 0;
    while (lo < hi) {
        if (height[lo] < height[hi]) {
            leftMax = Math.max(leftMax, height[lo]);
            water += leftMax - height[lo];
            lo++;
        } else {
            rightMax = Math.max(rightMax, height[hi]);
            water += rightMax - height[hi];
            hi--;
        }
    }
    return water;
}
```
**Approach:** Water trapped at index i = min(maxLeft, maxRight) - height[i]; two pointers avoid precomputing both arrays.
**Tricks/Pitfalls:** Moving the pointer on the smaller-height side guarantees the bound (leftMax/rightMax) used is valid.
**Complexity:** Time O(n), Space O(1).

### 12. Merge Intervals
**Problem:** Given an array of intervals where `intervals[i] = [start, end]`, merge all overlapping intervals and return an array of the non-overlapping intervals that cover all the intervals in the input.

**Example:**
```
Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]
Explanation: Intervals [1,3] and [2,6] overlap, merge into [1,6].
```
```java
public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> result = new ArrayList<>();
    for (int[] interval : intervals) {
        if (result.isEmpty() || result.get(result.size() - 1)[1] < interval[0]) {
            result.add(interval);
        } else {
            result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], interval[1]);
        }
    }
    return result.toArray(new int[0][]);
}
```
**Approach:** Sort by start time; merge into the last interval in the result if it overlaps.
**Tricks/Pitfalls:** Sorting by start (not end) is required here, unlike activity selection.
**Complexity:** Time O(n log n), Space O(n).

### 13. Insert Interval
**Problem:** Given a set of non-overlapping intervals sorted by start time, and a new interval, insert it into the intervals, merging as needed.

**Example:**
```
Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
Output: [[1,5],[6,9]]
```
```java
public int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0, n = intervals.length;
    while (i < n && intervals[i][1] < newInterval[0]) result.add(intervals[i++]); // before
    while (i < n && intervals[i][0] <= newInterval[1]) {                          // overlapping
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);
    while (i < n) result.add(intervals[i++]);                                     // after
    return result.toArray(new int[0][]);
}
```
**Approach:** Three phases: copy non-overlapping intervals before, merge all overlapping ones, copy the rest.
**Tricks/Pitfalls:** Since input is already sorted, no re-sort is needed — O(n) instead of O(n log n).
**Complexity:** Time O(n), Space O(n).

### 14. Non-overlapping Intervals
**Problem:** Given an array of intervals, find the minimum number of intervals you need to remove to make the rest non-overlapping.

**Example:**
```
Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
Output: 1
Explanation: Remove [1,3], the rest [[1,2],[2,3],[3,4]] are non-overlapping.
```
```java
public int eraseOverlapIntervals(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1])); // sort by end time
    int count = 0, lastEnd = Integer.MIN_VALUE;
    for (int[] interval : intervals) {
        if (interval[0] >= lastEnd) lastEnd = interval[1];  // keep
        else count++;                                        // remove this one
    }
    return count;
}
```
**Approach:** Greedy activity-selection variant — sort by end time, keep the interval that frees up room soonest.
**Tricks/Pitfalls:** Sorting by start time instead of end time is the most common wrong-approach mistake here.
**Complexity:** Time O(n log n), Space O(1).

### 15. Set Matrix Zeroes
**Problem:** Given an `m x n` matrix, if an element is 0, set its entire row and column to 0, in-place.

**Example:**
```
Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]
```
```java
public void setZeroes(int[][] matrix) {
    int rows = matrix.length, cols = matrix[0].length;
    boolean firstRowZero = false, firstColZero = false;
    for (int j = 0; j < cols; j++) if (matrix[0][j] == 0) firstRowZero = true;
    for (int i = 0; i < rows; i++) if (matrix[i][0] == 0) firstColZero = true;
    for (int i = 1; i < rows; i++)
        for (int j = 1; j < cols; j++)
            if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0; } // markers
    for (int i = 1; i < rows; i++)
        for (int j = 1; j < cols; j++)
            if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
    if (firstRowZero) for (int j = 0; j < cols; j++) matrix[0][j] = 0;
    if (firstColZero) for (int i = 0; i < rows; i++) matrix[i][0] = 0;
}
```
**Approach:** Use the first row/column as marker storage instead of an extra O(m+n) array.
**Tricks/Pitfalls:** Must track first-row/first-col zero status separately before overwriting them as markers.
**Complexity:** Time O(m·n), Space O(1).

### 16. Spiral Matrix
**Problem:** Given an `m x n` matrix, return all elements of the matrix in spiral order.

**Example:**
```
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]
```
```java
public List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
    while (top <= bottom && left <= right) {
        for (int j = left; j <= right; j++) result.add(matrix[top][j]);
        top++;
        for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
        right--;
        if (top <= bottom) { for (int j = right; j >= left; j--) result.add(matrix[bottom][j]); bottom--; }
        if (left <= right) { for (int i = bottom; i >= top; i--) result.add(matrix[i][left]); left++; }
    }
    return result;
}
```
**Approach:** Maintain 4 boundaries, shrink them inward after traversing each side.
**Tricks/Pitfalls:** Re-checking `top <= bottom` / `left <= right` before the last two sides avoids duplicate traversal on non-square matrices.
**Complexity:** Time O(m·n), Space O(1) excluding output.

### 17. Rotate Image
**Problem:** Given an `n x n` 2D matrix representing an image, rotate the image by 90 degrees clockwise, in-place.

**Example:**
```
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [[7,4,1],[8,5,2],[9,6,3]]
```
```java
public void rotate(int[][] matrix) {
    int n = matrix.length;
    for (int i = 0; i < n; i++)                          // transpose
        for (int j = i + 1; j < n; j++) {
            int t = matrix[i][j]; matrix[i][j] = matrix[j][i]; matrix[j][i] = t;
        }
    for (int[] row : matrix) {                            // reverse each row
        for (int l = 0, r = n - 1; l < r; l++, r--) {
            int t = row[l]; row[l] = row[r]; row[r] = t;
        }
    }
}
```
**Approach:** Transpose then reverse each row — equivalent to a 90° clockwise rotation.
**Tricks/Pitfalls:** Transposing in-place must only swap upper triangle (`j = i+1`) to avoid double-swapping.
**Complexity:** Time O(n²), Space O(1).

### 18. Word Search
**Problem:** Given an `m x n` grid of characters and a `word`, return true if the word exists in the grid via adjacent (up/down/left/right) cells, using each cell at most once.

**Example:**
```
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
Output: true
```
```java
public boolean exist(char[][] board, String word) {
    int rows = board.length, cols = board[0].length;
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            if (dfs(board, word, i, j, 0)) return true;
    return false;
}
private boolean dfs(char[][] board, String word, int i, int j, int idx) {
    if (idx == word.length()) return true;
    if (i < 0 || j < 0 || i >= board.length || j >= board[0].length || board[i][j] != word.charAt(idx))
        return false;
    char temp = board[i][j];
    board[i][j] = '#';   // mark visited
    boolean found = dfs(board, word, i + 1, j, idx + 1) || dfs(board, word, i - 1, j, idx + 1)
                 || dfs(board, word, i, j + 1, idx + 1) || dfs(board, word, i, j - 1, idx + 1);
    board[i][j] = temp;  // backtrack
    return found;
}
```
**Approach:** DFS/backtracking from every cell, marking visited cells temporarily to prevent reuse.
**Tricks/Pitfalls:** Must restore the cell (`board[i][j] = temp`) after exploring — forgetting this breaks subsequent searches from other start cells.
**Complexity:** Time O(m·n·4^L) where L = word length, Space O(L) recursion stack.

### 19. Longest Substring Without Repeating Characters
**Problem:** Given a string `s`, find the length of the longest substring without repeating characters.

**Example:**
```
Input: s = "abcabcbb"
Output: 3
Explanation: The answer is "abc", with the length of 3.
```
```java
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int maxLen = 0, start = 0;
    for (int end = 0; end < s.length(); end++) {
        char c = s.charAt(end);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= start) start = lastSeen.get(c) + 1;
        lastSeen.put(c, end);
        maxLen = Math.max(maxLen, end - start + 1);
    }
    return maxLen;
}
```
**Approach:** Sliding window; jump `start` past the previous occurrence of a repeated character instead of shrinking one step at a time.
**Tricks/Pitfalls:** The `>= start` check avoids incorrectly jumping backward due to a stale (already out-of-window) last-seen index.
**Complexity:** Time O(n), Space O(min(n, charset size)).

### 20. Longest Repeating Character Replacement
**Problem:** Given a string `s` and integer `k`, find the length of the longest substring containing the same letter after replacing at most `k` characters.

**Example:**
```
Input: s = "AABABBA", k = 1
Output: 4
Explanation: Replace one 'B' at index 3 to get "AAAABBA" (or similar), longest run = 4.
```
```java
public int characterReplacement(String s, int k) {
    int[] counts = new int[26];
    int left = 0, maxCount = 0, result = 0;
    for (int right = 0; right < s.length(); right++) {
        maxCount = Math.max(maxCount, ++counts[s.charAt(right) - 'A']);
        while (right - left + 1 - maxCount > k) counts[s.charAt(left++) - 'A']--; // shrink
        result = Math.max(result, right - left + 1);
    }
    return result;
}
```
**Approach:** Sliding window; window is valid if (window size - most frequent char count) <= k.
**Tricks/Pitfalls:** `maxCount` is never decremented even when shrinking — it's fine because we only care about the window ever reaching a new maximum size, not shrinking correctness.
**Complexity:** Time O(n), Space O(1) (fixed 26-size array).

### 21. Minimum Window Substring
**Problem:** Given two strings `s` and `t`, return the minimum window substring of `s` such that every character in `t` (including duplicates) is included in the window. Return `""` if no such window exists.

**Example:**
```
Input: s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
```
```java
public String minWindow(String s, String t) {
    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
    Map<Character, Integer> window = new HashMap<>();
    int have = 0, required = need.size();
    int[] best = {-1, 0, 0}; // length, left, right
    int left = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        window.merge(c, 1, Integer::sum);
        if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) have++;
        while (have == required) {
            if (best[0] == -1 || right - left + 1 < best[0]) best = new int[]{right - left + 1, left, right};
            char lc = s.charAt(left);
            window.put(lc, window.get(lc) - 1);
            if (need.containsKey(lc) && window.get(lc) < need.get(lc)) have--;
            left++;
        }
    }
    return best[0] == -1 ? "" : s.substring(best[1], best[2] + 1);
}
```
**Approach:** Sliding window with a frequency map; expand right until all required chars are covered, then shrink left while still valid to find the minimum.
**Tricks/Pitfalls:** Track `have`/`required` (count of *distinct satisfied characters*) rather than re-comparing whole maps every iteration — this is what keeps it O(n).
**Complexity:** Time O(|s| + |t|), Space O(|s| + |t|).

### 22. Valid Anagram
**Problem:** Given two strings `s` and `t`, return true if `t` is an anagram of `s`.

**Example:**
```
Input: s = "anagram", t = "nagaram"
Output: true
```
```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] counts = new int[26];
    for (int i = 0; i < s.length(); i++) { counts[s.charAt(i) - 'a']++; counts[t.charAt(i) - 'a']--; }
    for (int c : counts) if (c != 0) return false;
    return true;
}
```
**Approach:** Count character frequencies for both strings simultaneously using one array (increment for `s`, decrement for `t`).
**Tricks/Pitfalls:** Sorting both strings works too (O(n log n)) but the counting array is O(n).
**Complexity:** Time O(n), Space O(1) (fixed 26-size array).

### 23. Group Anagrams
**Problem:** Given an array of strings, group the anagrams together. Return the answer in any order.

**Example:**
```
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
```
```java
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        groups.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```
**Approach:** Use the sorted version of each string as a canonical hash-map key to group anagrams.
**Tricks/Pitfalls:** For very large inputs, a character-count-based key (avoiding O(k log k) sort per string) is a further optimization worth mentioning.
**Complexity:** Time O(n·k log k) where k = max string length, Space O(n·k).

### 24. Valid Palindrome
**Problem:** Given a string `s`, return true if it is a palindrome, considering only alphanumeric characters and ignoring case.

**Example:**
```
Input: s = "A man, a plan, a canal: Panama"
Output: true
```
```java
public boolean isPalindrome(String s) {
    int lo = 0, hi = s.length() - 1;
    while (lo < hi) {
        while (lo < hi && !Character.isLetterOrDigit(s.charAt(lo))) lo++;
        while (lo < hi && !Character.isLetterOrDigit(s.charAt(hi))) hi--;
        if (Character.toLowerCase(s.charAt(lo)) != Character.toLowerCase(s.charAt(hi))) return false;
        lo++; hi--;
    }
    return true;
}
```
**Approach:** Two pointers from both ends, skipping non-alphanumeric characters as they move inward.
**Tricks/Pitfalls:** Avoid building a cleaned copy of the string (extra O(n) space) — filtering in-place with two pointers is O(1) space.
**Complexity:** Time O(n), Space O(1).

### 25. Longest Palindromic Substring
**Problem:** Given a string `s`, return the longest palindromic substring in `s`.

**Example:**
```
Input: s = "babad"
Output: "bab"
Explanation: "aba" is also a valid answer.
```
```java
public String longestPalindrome(String s) {
    if (s.isEmpty()) return "";
    int start = 0, maxLen = 1;
    for (int center = 0; center < s.length(); center++) {
        int len1 = expand(s, center, center);       // odd length
        int len2 = expand(s, center, center + 1);    // even length
        int len = Math.max(len1, len2);
        if (len > maxLen) {
            maxLen = len;
            start = center - (len - 1) / 2;
        }
    }
    return s.substring(start, start + maxLen);
}
private int expand(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) { left--; right++; }
    return right - left - 1;
}
```
**Approach:** Expand-around-center for every possible center (both odd and even length palindromes).
**Tricks/Pitfalls:** Must check both odd-center (`center, center`) and even-center (`center, center+1`) cases — Manacher's algorithm gives O(n) if needed for very large inputs.
**Complexity:** Time O(n²), Space O(1). (Manacher's: O(n) time, O(n) space.)

### 26. Palindromic Substrings (Count)
**Problem:** Given a string `s`, return the number of palindromic substrings in it.

**Example:**
```
Input: s = "aaa"
Output: 6
Explanation: "a", "a", "a", "aa", "aa", "aaa" — six palindromic substrings.
```
```java
public int countSubstrings(String s) {
    int count = 0;
    for (int center = 0; center < s.length(); center++) {
        count += expandCount(s, center, center);
        count += expandCount(s, center, center + 1);
    }
    return count;
}
private int expandCount(String s, int left, int right) {
    int count = 0;
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) { count++; left--; right++; }
    return count;
}
```
**Approach:** Same expand-around-center technique, counting every valid expansion instead of tracking only the longest.
**Tricks/Pitfalls:** Reusing the same center-expansion helper as problem 25 shows pattern recognition across problems — a good thing to point out in an interview.
**Complexity:** Time O(n²), Space O(1).

### 27. String to Integer (atoi)
**Problem:** Implement `atoi` which converts a string to a 32-bit signed integer, skipping leading whitespace, handling an optional sign, reading digits, and clamping to `[INT_MIN, INT_MAX]` on overflow.

**Example:**
```
Input: s = "   -42"
Output: -42
```
```java
public int myAtoi(String s) {
    int i = 0, n = s.length();
    while (i < n && s.charAt(i) == ' ') i++;
    if (i == n) return 0;
    int sign = 1;
    if (s.charAt(i) == '+' || s.charAt(i) == '-') { sign = s.charAt(i) == '-' ? -1 : 1; i++; }
    long result = 0;
    while (i < n && Character.isDigit(s.charAt(i))) {
        result = result * 10 + (s.charAt(i) - '0');
        if (result * sign > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (result * sign < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        i++;
    }
    return (int) (result * sign);
}
```
**Approach:** Manually parse: skip whitespace, read optional sign, read digits while clamping to int bounds as you go.
**Tricks/Pitfalls:** Using a `long` accumulator sidesteps intermediate overflow while still clamping to `Integer.MIN/MAX_VALUE` per the spec.
**Complexity:** Time O(n), Space O(1).


## Category 2: Linked List

> Note: Linked lists are shown as arrays for readability (e.g., `[1,2,3]` means `1->2->3`).

### 28. Reverse Linked List
**Problem:** Given the head of a singly linked list, reverse the list and return the new head.

**Example:**
```
Input: head = [1,2,3,4,5]
Output: [5,4,3,2,1]
```
```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```
**Approach:** Iteratively re-point each node's `next` to the previous node, tracking `prev` and `next` to avoid losing the chain.
**Tricks/Pitfalls:** Save `curr.next` *before* overwriting it — the single most common bug in linked list problems.
**Complexity:** Time O(n), Space O(1) iterative (O(n) if done recursively, due to call stack).

### 29. Merge Two Sorted Lists
**Problem:** Merge two sorted linked lists `list1` and `list2` into one sorted list and return its head.

**Example:**
```
Input: list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]
```
```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(-1), curr = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
        else { curr.next = l2; l2 = l2.next; }
        curr = curr.next;
    }
    curr.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```
**Approach:** Use a dummy head node to simplify edge cases (no special-casing the first node); splice nodes directly rather than creating new ones.
**Tricks/Pitfalls:** The dummy-node pattern eliminates a whole class of null-check bugs in list-building problems.
**Complexity:** Time O(n + m), Space O(1).

### 30. Linked List Cycle
**Problem:** Given the head of a linked list, determine if the list has a cycle.

**Example:**
```
Input: head = [3,2,0,-4], with a cycle where the tail connects back to index 1 (node with value 2)
Output: true
```
```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```
**Approach:** Floyd's Tortoise and Hare — a fast pointer moving 2x speed will eventually lap a slow pointer if and only if there's a cycle.
**Tricks/Pitfalls:** Checking `fast != null && fast.next != null` prevents NPE when there's no cycle and the list ends.
**Complexity:** Time O(n), Space O(1) — beats the O(n) space `HashSet` approach.

### 31. Linked List Cycle II (Find Cycle Start)
**Problem:** Given the head of a linked list, return the node where the cycle begins, or null if there is no cycle.

**Example:**
```
Input: head = [3,2,0,-4], cycle connects tail back to index 1
Output: the node with value 2 (index 1)
```
```java
public ListNode detectCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next; fast = fast.next.next;
        if (slow == fast) {
            ListNode ptr = head;
            while (ptr != slow) { ptr = ptr.next; slow = slow.next; }
            return ptr;
        }
    }
    return null;
}
```
**Approach:** After detecting the meeting point, math shows moving one pointer from `head` and one from the meeting point at equal speed converges exactly at the cycle start.
**Tricks/Pitfalls:** This is a memorized-proof pattern — deriving it live is hard; know the "reset one pointer to head" trick.
**Complexity:** Time O(n), Space O(1).

### 32. Remove Nth Node From End of List
**Problem:** Given the head of a linked list, remove the nth node from the end and return the head.

**Example:**
```
Input: head = [1,2,3,4,5], n = 2
Output: [1,2,3,5]
```
```java
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode fast = dummy, slow = dummy;
    for (int i = 0; i < n; i++) fast = fast.next;      // advance fast n steps
    while (fast.next != null) { fast = fast.next; slow = slow.next; }
    slow.next = slow.next.next;                         // remove target
    return dummy.next;
}
```
**Approach:** Two pointers with a gap of `n`; when `fast` reaches the end, `slow` is right before the node to remove.
**Tricks/Pitfalls:** The dummy node handles removing the head itself (when n == list length) cleanly.
**Complexity:** Time O(n) single pass, Space O(1).

### 33. Reorder List
**Problem:** Given the head of a singly linked list `L0→L1→…→Ln-1→Ln`, reorder it to `L0→Ln→L1→Ln-1→L2→Ln-2→…` in place.

**Example:**
```
Input: head = [1,2,3,4,5]
Output: [1,5,2,4,3]
```
```java
public void reorderList(ListNode head) {
    // 1. find middle
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
    // 2. reverse second half
    ListNode second = reverse(slow.next);
    slow.next = null;
    // 3. merge two halves alternately
    ListNode first = head;
    while (second != null) {
        ListNode t1 = first.next, t2 = second.next;
        first.next = second; second.next = t1;
        first = t1; second = t2;
    }
}
private ListNode reverse(ListNode head) {
    ListNode prev = null;
    while (head != null) { ListNode next = head.next; head.next = prev; prev = head; head = next; }
    return prev;
}
```
**Approach:** Combine three sub-skills — find middle (slow/fast), reverse a list, and merge two lists by interleaving.
**Tricks/Pitfalls:** Great interview question because it tests whether you can compose simpler linked-list primitives you already know.
**Complexity:** Time O(n), Space O(1).

### 34. Merge K Sorted Lists
**Problem:** You are given an array of `k` linked lists, each sorted in ascending order. Merge all the linked lists into one sorted linked list and return it.

**Example:**
```
Input: lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
```
```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    for (ListNode node : lists) if (node != null) pq.offer(node);
    ListNode dummy = new ListNode(-1), curr = dummy;
    while (!pq.isEmpty()) {
        ListNode min = pq.poll();
        curr.next = min;
        curr = curr.next;
        if (min.next != null) pq.offer(min.next);
    }
    return dummy.next;
}
```
**Approach:** Min-heap holding the current head of each list; always extract the global minimum and push its successor.
**Tricks/Pitfalls:** Alternative divide-and-conquer (pairwise merge) approach gives the same O(N log k) but avoids heap overhead — worth mentioning both.
**Complexity:** Time O(N log k) where N = total nodes, k = number of lists, Space O(k).

### 35. Copy List with Random Pointer
**Problem:** A linked list where each node has an additional `random` pointer to any node in the list or null. Return a deep copy of the list.

**Example:**
```
Input: head = [[7,null],[13,0],[11,4],[10,2],[1,0]]  (each pair is [val, random_index])
Output: A deep copy with identical structure and random pointers, but entirely new node objects.
```
```java
public Node copyRandomList(Node head) {
    if (head == null) return null;
    Map<Node, Node> map = new HashMap<>();
    Node curr = head;
    while (curr != null) { map.put(curr, new Node(curr.val)); curr = curr.next; } // create copies
    curr = head;
    while (curr != null) {
        map.get(curr).next = map.get(curr.next);
        map.get(curr).random = map.get(curr.random);
        curr = curr.next;
    }
    return map.get(head);
}
```
**Approach:** Two passes with a HashMap from original node → copy node, so `random` pointers can be resolved after all copies exist.
**Tricks/Pitfalls:** An O(1)-space variant interleaves copy nodes directly into the original list (`A→A'→B→B'...`) then splits them apart — good follow-up to mention.
**Complexity:** Time O(n), Space O(n) (O(1) with the interleaving trick).

### 36. Add Two Numbers
**Problem:** Two non-empty linked lists represent two non-negative integers, digits stored in reverse order. Add the two numbers and return the sum as a linked list.

**Example:**
```
Input: l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]
Explanation: 342 + 465 = 807
```
```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0), curr = dummy;
    int carry = 0;
    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry + (l1 != null ? l1.val : 0) + (l2 != null ? l2.val : 0);
        carry = sum / 10;
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
        if (l1 != null) l1 = l1.next;
        if (l2 != null) l2 = l2.next;
    }
    return dummy.next;
}
```
**Approach:** Simulate elementary-school addition digit by digit, carrying over remainder; loop condition includes `carry != 0` to handle a final carry-out digit.
**Tricks/Pitfalls:** Forgetting the trailing carry (e.g., 5+5=10 needs an extra node) is the most common bug.
**Complexity:** Time O(max(n, m)), Space O(max(n, m)) for the result list.

### 37. Palindrome Linked List
**Problem:** Given the head of a singly linked list, return true if it is a palindrome.

**Example:**
```
Input: head = [1,2,2,1]
Output: true
```
```java
public boolean isPalindrome(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; }
    ListNode secondHalf = reverse(slow);
    ListNode firstHalf = head;
    while (secondHalf != null) {
        if (firstHalf.val != secondHalf.val) return false;
        firstHalf = firstHalf.next; secondHalf = secondHalf.next;
    }
    return true;
}
private ListNode reverse(ListNode head) {
    ListNode prev = null;
    while (head != null) { ListNode next = head.next; head.next = prev; prev = head; head = next; }
    return prev;
}
```
**Approach:** Find the middle, reverse the second half in place, then compare both halves node by node.
**Tricks/Pitfalls:** Reversing in-place (rather than copying to an array) achieves true O(1) space — a common follow-up ask.
**Complexity:** Time O(n), Space O(1).

### 38. Intersection of Two Linked Lists
**Problem:** Given the heads of two singly linked lists, return the node at which the two lists intersect, or null if they don't.

**Example:**
```
Input: listA = [4,1,8,4,5], listB = [5,6,1,8,4,5], intersecting at the node with value 8
Output: the node with value 8
```
```java
public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    ListNode a = headA, b = headB;
    while (a != b) {
        a = (a == null) ? headB : a.next;
        b = (b == null) ? headA : b.next;
    }
    return a; // either the intersection node, or null if no intersection
}
```
**Approach:** Two pointers each traverse both lists in sequence (switching to the other list's head upon reaching the end); this equalizes the total distance traveled so they meet exactly at the intersection.
**Tricks/Pitfalls:** Elegant O(1) space alternative to computing lengths and aligning start points manually.
**Complexity:** Time O(n + m), Space O(1).

### 39. Odd Even Linked List
**Problem:** Given a singly linked list, group all nodes with odd indices together followed by nodes with even indices (1-indexed), and return the reordered list.

**Example:**
```
Input: head = [1,2,3,4,5]
Output: [1,3,5,2,4]
```
```java
public ListNode oddEvenList(ListNode head) {
    if (head == null) return null;
    ListNode odd = head, even = head.next, evenHead = even;
    while (even != null && even.next != null) {
        odd.next = even.next;
        odd = odd.next;
        even.next = odd.next;
        even = even.next;
    }
    odd.next = evenHead;
    return head;
}
```
**Approach:** Maintain two separate chains (odd, even) while traversing once, then join odd's tail to even's head.
**Tricks/Pitfalls:** Save `evenHead` before the loop mutates pointers, or you lose the reference to reconnect at the end.
**Complexity:** Time O(n), Space O(1).

### 40. LRU Cache
**Problem:** Design a data structure for a Least Recently Used (LRU) cache, supporting `get(key)` and `put(key, value)` in O(1), evicting the least recently used item when capacity is exceeded.

**Example:**
```
Input: capacity = 2
put(1,1); put(2,2); get(1) -> 1; put(3,3) evicts key 2; get(2) -> -1; put(4,4) evicts key 1; get(1) -> -1; get(3) -> 3; get(4) -> 4
Output: [1, -1, -1, 3, 4]
```
```java
class LRUCache extends LinkedHashMap<Integer, Integer> {
    private final int capacity;
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // accessOrder = true -> tracks recency automatically
        this.capacity = capacity;
    }
    public int get(int key) { return super.getOrDefault(key, -1); }
    public void put(int key, int value) { super.put(key, value); }
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity; // auto-evict least recently used
    }
}
// Hand-rolled version (what interviewers usually want to see you derive):
class LRUCacheManual {
    class Node { int key, val; Node prev, next; Node(int k, int v) { key = k; val = v; } }
    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0), tail = new Node(0, 0); // dummy sentinels
    LRUCacheManual(int capacity) { this.capacity = capacity; head.next = tail; tail.prev = head; }
    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        remove(node); insertFront(node);
        return node.val;
    }
    public void put(int key, int value) {
        if (map.containsKey(key)) remove(map.get(key));
        else if (map.size() == capacity) { Node lru = tail.prev; remove(lru); map.remove(lru.key); }
        Node node = new Node(key, value);
        map.put(key, node);
        insertFront(node);
    }
    private void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
    private void insertFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
}
```
**Approach:** `HashMap` (key → node) for O(1) lookup, paired with a **doubly linked list** for O(1) reordering/eviction — the map alone can't track recency; the list alone can't do O(1) lookup by key.
**Tricks/Pitfalls:** Using dummy `head`/`tail` sentinel nodes removes all null-checks for edge insert/remove operations — always show the hand-rolled version in an interview, not just the `LinkedHashMap` trick, since the interviewer usually wants to see you build the underlying data structure.
**Complexity:** Time O(1) for both `get` and `put`, Space O(capacity).


## Category 3: Stack & Queue

### 41. Valid Parentheses
**Problem:** Given a string containing just the characters `'(' ')' '{' '}' '[' ']'`, determine if the input string is valid (every bracket is closed by the same type in the correct order).

**Example:**
```
Input: s = "()[]{}"
Output: true

Input: s = "(]"
Output: false
```
```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
    for (char c : s.toCharArray()) {
        if (pairs.containsKey(c)) {
            if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
        } else stack.push(c);
    }
    return stack.isEmpty();
}
```
**Approach:** Push opening brackets; on a closing bracket, pop and check it matches the expected opener.
**Tricks/Pitfalls:** Must check `stack.isEmpty()` before popping, and confirm the stack is empty at the end (unmatched openers left over).
**Complexity:** Time O(n), Space O(n).

### 42. Min Stack
**Problem:** Design a stack supporting `push`, `pop`, `top`, and retrieving the minimum element, all in O(1).

**Example:**
```
Input: push(-2); push(0); push(-3); getMin() -> -3; pop(); top() -> 0; getMin() -> -2
Output: [-3, 0, -2]
```
```java
class MinStack {
    private final Deque<int[]> stack = new ArrayDeque<>(); // [value, minSoFar]
    public void push(int val) {
        int currMin = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
        stack.push(new int[]{val, currMin});
    }
    public void pop() { stack.pop(); }
    public int top() { return stack.peek()[0]; }
    public int getMin() { return stack.peek()[1]; }
}
```
**Approach:** Store the running minimum alongside each value, so popping never "loses" the previous minimum.
**Tricks/Pitfalls:** A single extra min-tracking variable breaks on pop (you'd lose the prior min) — storing it per-frame is what makes `getMin()` O(1) correctly.
**Complexity:** Time O(1) all operations, Space O(n).

### 43. Evaluate Reverse Polish Notation
**Problem:** Evaluate the value of an arithmetic expression given in Reverse Polish (postfix) Notation.

**Example:**
```
Input: tokens = ["2","1","+","3","*"]
Output: 9
Explanation: ((2 + 1) * 3) = 9
```
```java
public int evalRPN(String[] tokens) {
    Deque<Integer> stack = new ArrayDeque<>();
    Set<String> ops = Set.of("+", "-", "*", "/");
    for (String token : tokens) {
        if (ops.contains(token)) {
            int b = stack.pop(), a = stack.pop();
            stack.push(switch (token) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                default -> a / b;
            });
        } else stack.push(Integer.parseInt(token));
    }
    return stack.pop();
}
```
**Approach:** Push operands; on an operator, pop the two most recent operands (order matters for `-`/`/`), compute, push result back.
**Tricks/Pitfalls:** Pop order is `b` then `a` — `a - b`, not `b - a` — a very common sign-flip bug.
**Complexity:** Time O(n), Space O(n).

### 44. Daily Temperatures
**Problem:** Given an array of daily temperatures, return an array where `answer[i]` is the number of days until a warmer temperature; if none, put 0.

**Example:**
```
Input: temperatures = [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
```
```java
public int[] dailyTemperatures(int[] temperatures) {
    int[] result = new int[temperatures.length];
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices, decreasing temp order
    for (int i = 0; i < temperatures.length; i++) {
        while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
            int idx = stack.pop();
            result[idx] = i - idx;
        }
        stack.push(i);
    }
    return result;
}
```
**Approach:** Monotonic decreasing stack of indices — when a warmer day is found, resolve all colder days waiting on the stack at once.
**Tricks/Pitfalls:** Storing indices (not temperatures) is what lets you compute the day-gap directly.
**Complexity:** Time O(n) — each index is pushed/popped at most once, Space O(n).

### 45. Next Greater Element I
**Problem:** For each element of `nums1` (a subset of `nums2`), find its next greater element to the right in `nums2`, or -1 if none exists.

**Example:**
```
Input: nums1 = [4,1,2], nums2 = [1,3,4,2]
Output: [-1,3,-1]
```
```java
public int[] nextGreaterElement(int[] nums1, int[] nums2) {
    Map<Integer, Integer> nextGreater = new HashMap<>();
    Deque<Integer> stack = new ArrayDeque<>();
    for (int num : nums2) {
        while (!stack.isEmpty() && stack.peek() < num) nextGreater.put(stack.pop(), num);
        stack.push(num);
    }
    int[] result = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) result[i] = nextGreater.getOrDefault(nums1[i], -1);
    return result;
}
```
**Approach:** Precompute "next greater" for every element of nums2 once using a monotonic stack, then look up answers for nums1 in O(1) each.
**Tricks/Pitfalls:** Precomputing once for nums2 avoids redoing the stack scan per nums1 element (O(n·m) naive trap).
**Complexity:** Time O(n + m), Space O(n).

### 46. Largest Rectangle in Histogram
**Problem:** Given an array of integers representing histogram bar heights (width 1 each), find the area of the largest rectangle in the histogram.

**Example:**
```
Input: heights = [2,1,5,6,2,3]
Output: 10
Explanation: The largest rectangle has height 5 and 6, spanning width 2 -> area = 5*2=10 (bars at index 2,3).
```
```java
public int largestRectangleArea(int[] heights) {
    Deque<Integer> stack = new ArrayDeque<>(); // indices, increasing height order
    int maxArea = 0;
    for (int i = 0; i <= heights.length; i++) {
        int h = (i == heights.length) ? 0 : heights[i]; // sentinel to flush stack at end
        while (!stack.isEmpty() && heights[stack.peek()] > h) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```
**Approach:** Monotonic increasing stack of indices; when a shorter bar is found, that's the right boundary for all taller bars on the stack — pop and compute their max possible rectangle width.
**Tricks/Pitfalls:** Appending a sentinel `0` height at the end forces the stack to fully flush, avoiding a separate post-loop cleanup pass.
**Complexity:** Time O(n), Space O(n).

### 47. Implement Queue using Stacks
**Problem:** Implement a first-in-first-out (FIFO) queue using only two stacks.

**Example:**
```
Input: push(1); push(2); peek() -> 1; pop() -> 1; empty() -> false
Output: [1, 1, false]
```
```java
class MyQueue {
    private final Deque<Integer> in = new ArrayDeque<>(), out = new ArrayDeque<>();
    public void push(int x) { in.push(x); }
    public int pop() { transfer(); return out.pop(); }
    public int peek() { transfer(); return out.peek(); }
    public boolean empty() { return in.isEmpty() && out.isEmpty(); }
    private void transfer() { if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop()); }
}
```
**Approach:** `in` stack accepts pushes; when `out` is empty, dump all of `in` into `out`, which reverses order to achieve FIFO.
**Tricks/Pitfalls:** Only transfer when `out` is empty — this gives amortized O(1) per operation instead of O(n) every time.
**Complexity:** Time O(1) amortized per operation, Space O(n).

### 48. Sliding Window Maximum
**Problem:** Given an array `nums` and window size `k`, return an array of the maximum value in each sliding window as it moves from left to right.

**Example:**
```
Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]
```
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>(); // stores indices, decreasing value order
    int[] result = new int[nums.length - k + 1];
    for (int i = 0; i < nums.length; i++) {
        if (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst(); // out of window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast(); // maintain decreasing
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
    return result;
}
```
**Approach:** Monotonic decreasing deque of indices — front always holds the max for the current window.
**Tricks/Pitfalls:** Removing indices that fall out of the window *before* checking for a new max is essential ordering.
**Complexity:** Time O(n) — each index pushed/popped at most once, Space O(k).

### 49. Basic Calculator II
**Problem:** Evaluate a simple math expression string containing non-negative integers, `+ - * /`, and spaces (no parentheses).

**Example:**
```
Input: s = "3+2*2"
Output: 7
```
```java
public int calculate(String s) {
    Deque<Integer> stack = new ArrayDeque<>();
    int num = 0;
    char sign = '+';
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (Character.isDigit(c)) num = num * 10 + (c - '0');
        if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
            switch (sign) {
                case '+' -> stack.push(num);
                case '-' -> stack.push(-num);
                case '*' -> stack.push(stack.pop() * num);
                case '/' -> stack.push(stack.pop() / num);
            }
            sign = c;
            num = 0;
        }
    }
    int result = 0;
    for (int n : stack) result += n;
    return result;
}
```
**Approach:** Track the previous operator; for `*`/`/`, resolve immediately against the top of the stack (higher precedence), for `+`/`-` just push the signed number.
**Tricks/Pitfalls:** Handling the last number requires checking `i == s.length() - 1` inside the loop, since there's no trailing operator to trigger the push.
**Complexity:** Time O(n), Space O(n).

### 50. Decode String
**Problem:** Given an encoded string with the pattern `k[encoded_string]` (repeat `encoded_string` k times), return its decoded string.

**Example:**
```
Input: s = "3[a2[c]]"
Output: "accaccacc"
```
```java
public String decodeString(String s) {
    Deque<Integer> countStack = new ArrayDeque<>();
    Deque<StringBuilder> stringStack = new ArrayDeque<>();
    StringBuilder current = new StringBuilder();
    int count = 0;
    for (char c : s.toCharArray()) {
        if (Character.isDigit(c)) count = count * 10 + (c - '0');
        else if (c == '[') { countStack.push(count); stringStack.push(current); count = 0; current = new StringBuilder(); }
        else if (c == ']') {
            StringBuilder decoded = stringStack.pop();
            int repeat = countStack.pop();
            for (int i = 0; i < repeat; i++) decoded.append(current);
            current = decoded;
        } else current.append(c);
    }
    return current.toString();
}
```
**Approach:** Two stacks — one for pending repeat counts, one for the string built so far before entering each bracket — classic nested-structure stack pattern.
**Tricks/Pitfalls:** On `]`, build the repeated segment onto the *outer* saved string, not a fresh buffer, to correctly handle nested brackets.
**Complexity:** Time O(n·maxRepeat), Space O(n).

### 51. Asteroid Collision
**Problem:** Given an array of integers representing asteroids in a row (positive = moving right, negative = moving left), simulate collisions and return the state after all collisions.

**Example:**
```
Input: asteroids = [5,10,-5]
Output: [5,10]
Explanation: 10 and -5 collide, 10 survives (bigger); 5 and 10 move same direction so never collide.
```
```java
public int[] asteroidCollision(int[] asteroids) {
    Deque<Integer> stack = new ArrayDeque<>();
    for (int a : asteroids) {
        boolean alive = true;
        while (alive && a < 0 && !stack.isEmpty() && stack.peek() > 0) {
            if (stack.peek() < -a) stack.pop();          // top explodes
            else if (stack.peek() == -a) { stack.pop(); alive = false; } // both explode
            else alive = false;                            // current asteroid explodes
        }
        if (alive) stack.push(a);
    }
    int[] result = new int[stack.size()];
    for (int i = result.length - 1; i >= 0; i--) result[i] = stack.pop();
    return result;
}
```
**Approach:** A collision only happens when a right-moving asteroid (on stack) meets a left-moving one (current) — use a stack to simulate this, resolving multiple chained collisions in the `while` loop.
**Tricks/Pitfalls:** All three collision outcomes (top explodes, both explode, current explodes) must be handled explicitly — missing the "equal size, both explode" case is the classic bug.
**Complexity:** Time O(n), Space O(n).

### 52. Design Circular Queue
**Problem:** Design a circular queue supporting `enQueue`, `deQueue`, `Front`, `Rear`, `isEmpty`, `isFull`, all O(1).

**Example:**
```
Input: MyCircularQueue(3); enQueue(1); enQueue(2); enQueue(3); enQueue(4); Rear() -> 3; isFull() -> true
Output: [true, true, true, false, 3, true]
```
```java
class MyCircularQueue {
    private final int[] queue;
    private int head, size, capacity;
    public MyCircularQueue(int k) { queue = new int[k]; capacity = k; }
    public boolean enQueue(int value) {
        if (isFull()) return false;
        queue[(head + size) % capacity] = value;
        size++;
        return true;
    }
    public boolean deQueue() {
        if (isEmpty()) return false;
        head = (head + 1) % capacity;
        size--;
        return true;
    }
    public int Front() { return isEmpty() ? -1 : queue[head]; }
    public int Rear() { return isEmpty() ? -1 : queue[(head + size - 1) % capacity]; }
    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
}
```
**Approach:** A fixed array with modular arithmetic to wrap indices, avoiding shifting elements on dequeue.
**Tricks/Pitfalls:** Tracking `size` explicitly (rather than trying to distinguish full vs. empty via head/tail equality alone) simplifies the full/empty checks significantly.
**Complexity:** Time O(1) for all operations, Space O(k).

### 53. Sliding Window Maximum via Two Deques (Monotonic Queue) — Conceptual Extension
*(See Problem 48 above — included here as the canonical "monotonic queue" pattern reference used across many variations like max of every window, shortest subarray with sum ≥ K, etc.)*
**Key takeaway:** Whenever a problem needs "max/min over a moving window" in O(n) instead of O(n·k), reach for a monotonic deque.


## Category 4: Hashing

### 54. Count Pairs With Given Difference
**Problem:** Given an array `nums` and integer `k`, count the number of pairs `(i, j)` such that `nums[j] - nums[i] == k`.

**Example:**
```
Input: nums = [1,5,3,4,2], k = 2
Output: 3
Explanation: Pairs (1,3),(3,5),(2,4) have difference 2.
```
```java
public int countPairsWithDiff(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);
    int count = 0;
    for (int n : freq.keySet()) if (freq.containsKey(n + k)) count += freq.get(n) * freq.get(n + k);
    return count;
}
```
**Approach:** Frequency map lets you count all pairs with a fixed difference in one pass over distinct values instead of O(n²) pairwise comparison.
**Tricks/Pitfalls:** If k == 0, this logic double-counts unless handled separately (pairs within same value need combinations, not multiplication) — a good clarifying question to ask the interviewer.
**Complexity:** Time O(n), Space O(n).

### 55. Longest Consecutive Sequence
**Problem:** Given an unsorted array of integers, find the length of the longest consecutive elements sequence, in O(n) time.

**Example:**
```
Input: nums = [100,4,200,1,3,2]
Output: 4
Explanation: The longest consecutive sequence is [1,2,3,4].
```
```java
public int longestConsecutive(int[] nums) {
    Set<Integer> set = new HashSet<>();
    for (int n : nums) set.add(n);
    int longest = 0;
    for (int n : set) {
        if (!set.contains(n - 1)) {           // only start counting from sequence starts
            int length = 1;
            while (set.contains(n + length)) length++;
            longest = Math.max(longest, length);
        }
    }
    return longest;
}
```
**Approach:** Only expand a sequence from its true start (no predecessor in the set) — this guarantees each number is visited a bounded number of times overall.
**Tricks/Pitfalls:** Without the `!set.contains(n - 1)` guard, this degrades to O(n²) by re-scanning the same sequence from every element.
**Complexity:** Time O(n), Space O(n).

### 56. Subarray Sum Equals K
**Problem:** Given an array of integers `nums` and an integer `k`, return the total number of contiguous subarrays whose sum equals `k`.

**Example:**
```
Input: nums = [1,1,1], k = 2
Output: 2
```
```java
public int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1); // empty prefix
    int sum = 0, count = 0;
    for (int n : nums) {
        sum += n;
        count += prefixCount.getOrDefault(sum - k, 0);
        prefixCount.merge(sum, 1, Integer::sum);
    }
    return count;
}
```
**Approach:** If `prefixSum[j] - prefixSum[i] == k`, then subarray (i, j] sums to k — track how many times each prefix sum has occurred.
**Tricks/Pitfalls:** Initializing `prefixCount.put(0, 1)` handles subarrays starting at index 0 correctly.
**Complexity:** Time O(n), Space O(n).

### 57. Top K Frequent Elements
**Problem:** Given an integer array `nums` and integer `k`, return the `k` most frequent elements.

**Example:**
```
Input: nums = [1,1,1,2,2,3], k = 2
Output: [1,2]
```
```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);
    PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> freq.get(a) - freq.get(b)); // min-heap by freq
    for (int n : freq.keySet()) {
        heap.offer(n);
        if (heap.size() > k) heap.poll();
    }
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) result[i] = heap.poll();
    return result;
}
```
**Approach:** Count frequencies, then maintain a size-k min-heap so only the top k survive — avoids sorting all distinct values.
**Tricks/Pitfalls:** Bucket sort by frequency (index = frequency, value = list of numbers) gives O(n) instead of O(n log k) — worth mentioning as a follow-up optimization.
**Complexity:** Time O(n log k), Space O(n).

### 58. Encode and Decode Strings
**Problem:** Design an algorithm to encode a list of strings into one string and decode it back into the original list of strings.

**Example:**
```
Input: strs = ["Hello","World"]
Output (encoded): "5#Hello5#World"
Output (decoded back): ["Hello","World"]
```
```java
public String encode(List<String> strs) {
    StringBuilder sb = new StringBuilder();
    for (String s : strs) sb.append(s.length()).append('#').append(s); // length-prefixed
    return sb.toString();
}
public List<String> decode(String s) {
    List<String> result = new ArrayList<>();
    int i = 0;
    while (i < s.length()) {
        int j = i;
        while (s.charAt(j) != '#') j++;
        int len = Integer.parseInt(s.substring(i, j));
        result.add(s.substring(j + 1, j + 1 + len));
        i = j + 1 + len;
    }
    return result;
}
```
**Approach:** Length-prefixing (`"5#hello"`) avoids ambiguity from delimiter characters that might appear inside the strings themselves.
**Tricks/Pitfalls:** Using a simple delimiter like `,` breaks if strings contain commas — length-prefixing is the interview-expected robust solution.
**Complexity:** Time O(total characters), Space O(total characters).

### 59. Valid Sudoku
**Problem:** Determine if a 9x9 Sudoku board is valid: each row, column, and 3x3 sub-box must contain digits 1-9 without repetition (only filled cells need to be validated).

**Example:**
```
Input: board = [["5","3",".",".","7",".",".",".","."], ...(8 more rows)]
Output: true
```
```java
public boolean isValidSudoku(char[][] board) {
    Set<String> seen = new HashSet<>();
    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            char val = board[i][j];
            if (val == '.') continue;
            String row = "r" + i + val, col = "c" + j + val, box = "b" + (i / 3) + (j / 3) + val;
            if (!seen.add(row) || !seen.add(col) || !seen.add(box)) return false;
        }
    }
    return true;
}
```
**Approach:** Encode each (row, value), (col, value), (box, value) triple as a unique string key in one shared set — a duplicate insertion means a rule violation.
**Tricks/Pitfalls:** Computing box index as `(i/3, j/3)` cleanly maps any cell to its 3x3 sub-grid without hardcoding boundaries.
**Complexity:** Time O(1) (fixed 81 cells), Space O(1).

### 60. Design HashMap (Implement from Scratch)
**Problem:** Design a HashMap without using any built-in hash table libraries, supporting `put`, `get`, and `remove`.

**Example:**
```
Input: put(1,1); put(2,2); get(1) -> 1; get(3) -> -1; put(2,1); get(2) -> 1; remove(2); get(2) -> -1
Output: [1, -1, 1, -1]
```
```java
class MyHashMap {
    private final LinkedList<int[]>[] buckets;
    private static final int SIZE = 10007; // prime bucket count reduces collisions
    public MyHashMap() {
        buckets = new LinkedList[SIZE];
        for (int i = 0; i < SIZE; i++) buckets[i] = new LinkedList<>();
    }
    private int hash(int key) { return Integer.hashCode(key) % SIZE >= 0 ? Integer.hashCode(key) % SIZE : (Integer.hashCode(key) % SIZE) + SIZE; }
    public void put(int key, int value) {
        int idx = hash(key);
        for (int[] pair : buckets[idx]) if (pair[0] == key) { pair[1] = value; return; }
        buckets[idx].add(new int[]{key, value});
    }
    public int get(int key) {
        for (int[] pair : buckets[hash(key)]) if (pair[0] == key) return pair[1];
        return -1;
    }
    public void remove(int key) { buckets[hash(key)].removeIf(pair -> pair[0] == key); }
}
```
**Approach:** Classic bucket-array + linked-list-chaining implementation — mirrors what `java.util.HashMap` does internally (minus tree-ification for long chains).
**Tricks/Pitfalls:** Handling negative hash codes correctly (`% SIZE` can be negative in Java) is a subtle but important correctness detail.
**Complexity:** Time O(1) average, O(n) worst case (all keys collide), Space O(n).

### 61. Isomorphic Strings
**Problem:** Given two strings `s` and `t`, determine if they are isomorphic (characters in `s` can be replaced to get `t`, with a consistent one-to-one mapping).

**Example:**
```
Input: s = "egg", t = "add"
Output: true
Explanation: e->a, g->d consistently.
```
```java
public boolean isIsomorphic(String s, String t) {
    if (s.length() != t.length()) return false;
    Map<Character, Character> mapST = new HashMap<>(), mapTS = new HashMap<>();
    for (int i = 0; i < s.length(); i++) {
        char a = s.charAt(i), b = t.charAt(i);
        if (mapST.containsKey(a) && mapST.get(a) != b) return false;
        if (mapTS.containsKey(b) && mapTS.get(b) != a) return false;
        mapST.put(a, b); mapTS.put(b, a);
    }
    return true;
}
```
**Approach:** Two-way mapping ensures the relationship is a true bijection, not just a one-directional function.
**Tricks/Pitfalls:** Checking only one direction (`s → t`) misses cases like `"ab" → "aa"` where two different source chars map to the same target char.
**Complexity:** Time O(n), Space O(1) (bounded alphabet).

### 62. Word Pattern
**Problem:** Given a `pattern` and a string `s`, determine if `s` follows the same pattern (a bijection between pattern letters and words in `s`).

**Example:**
```
Input: pattern = "abba", s = "dog cat cat dog"
Output: true
```
```java
public boolean wordPattern(String pattern, String s) {
    String[] words = s.split(" ");
    if (pattern.length() != words.length) return false;
    Map<Character, String> charToWord = new HashMap<>();
    Map<String, Character> wordToChar = new HashMap<>();
    for (int i = 0; i < pattern.length(); i++) {
        char c = pattern.charAt(i);
        String w = words[i];
        if (charToWord.containsKey(c) && !charToWord.get(c).equals(w)) return false;
        if (wordToChar.containsKey(w) && wordToChar.get(w) != c) return false;
        charToWord.put(c, w); wordToChar.put(w, c);
    }
    return true;
}
```
**Approach:** Same bijection-checking pattern as Isomorphic Strings, applied at word granularity instead of character granularity.
**Tricks/Pitfalls:** Recognizing this is the *same underlying pattern* as problem 61 is a strong interview signal — showing you see the abstraction, not just the surface problem.
**Complexity:** Time O(n), Space O(n).

### 63. Contiguous Array (Equal 0s and 1s)
**Problem:** Given a binary array `nums`, find the maximum length of a contiguous subarray with an equal number of 0s and 1s.

**Example:**
```
Input: nums = [0,1,0,0,1,1,0]
Output: 6
```
```java
public int findMaxLength(int[] nums) {
    Map<Integer, Integer> firstIndex = new HashMap<>();
    firstIndex.put(0, -1);
    int count = 0, maxLen = 0;
    for (int i = 0; i < nums.length; i++) {
        count += (nums[i] == 1) ? 1 : -1;
        if (firstIndex.containsKey(count)) maxLen = Math.max(maxLen, i - firstIndex.get(count));
        else firstIndex.put(count, i);
    }
    return maxLen;
}
```
**Approach:** Treat 0 as -1 and 1 as +1; a running sum that repeats between two indices means the subarray between them sums to 0 (equal 0s and 1s).
**Tricks/Pitfalls:** Store only the **first** occurrence of each running sum (maximizes the resulting subarray length) — overwriting with later indices would shrink valid answers.
**Complexity:** Time O(n), Space O(n).

### 64. Insert Delete GetRandom O(1)
**Problem:** Design a data structure that supports `insert`, `remove`, and `getRandom` (each equally likely) all in average O(1) time.

**Example:**
```
Input: insert(1); remove(2) -> false; insert(2); getRandom() -> 1 or 2; remove(1) -> true; insert(2) -> false
Output: [true, false, true, 1_or_2, true, false]
```
```java
class RandomizedSet {
    private final List<Integer> list = new ArrayList<>();
    private final Map<Integer, Integer> indexMap = new HashMap<>(); // value -> index in list
    private final Random rand = new Random();

    public boolean insert(int val) {
        if (indexMap.containsKey(val)) return false;
        indexMap.put(val, list.size());
        list.add(val);
        return true;
    }
    public boolean remove(int val) {
        if (!indexMap.containsKey(val)) return false;
        int idx = indexMap.get(val), lastIdx = list.size() - 1;
        int lastVal = list.get(lastIdx);
        list.set(idx, lastVal); indexMap.put(lastVal, idx);   // swap with last element
        list.remove(lastIdx); indexMap.remove(val);
        return true;
    }
    public int getRandom() { return list.get(rand.nextInt(list.size())); }
}
```
**Approach:** An `ArrayList` gives O(1) `getRandom` via index; a companion `HashMap` gives O(1) lookup of any value's index for removal. Removal swaps the target with the last element to avoid an O(n) shift.
**Tricks/Pitfalls:** The swap-with-last trick for O(1) removal from an array-backed list is a reusable pattern worth memorizing.
**Complexity:** Time O(1) average for all operations, Space O(n).

### 65. Ransom Note
**Problem:** Given strings `ransomNote` and `magazine`, determine if `ransomNote` can be constructed using letters from `magazine` (each letter used at most once).

**Example:**
```
Input: ransomNote = "aa", magazine = "aab"
Output: true
```
```java
public boolean canConstruct(String ransomNote, String magazine) {
    int[] counts = new int[26];
    for (char c : magazine.toCharArray()) counts[c - 'a']++;
    for (char c : ransomNote.toCharArray()) {
        if (--counts[c - 'a'] < 0) return false;
    }
    return true;
}
```
**Approach:** Count available letters from the magazine, then decrement as the ransom note consumes them; a shortage triggers a negative count.
**Tricks/Pitfalls:** Early-exit as soon as a shortfall occurs, rather than counting both fully and comparing afterward.
**Complexity:** Time O(n + m), Space O(1).

### 66. Happy Number
**Problem:** Determine if a number is "happy": repeatedly replace the number by the sum of the squares of its digits; if this process eventually reaches 1, it's happy; if it loops endlessly without reaching 1, it's not.

**Example:**
```
Input: n = 19
Output: true
Explanation: 19 -> 82 -> 68 -> 100 -> 1
```
```java
public boolean isHappy(int n) {
    Set<Integer> seen = new HashSet<>();
    while (n != 1 && !seen.contains(n)) {
        seen.add(n);
        n = sumOfSquares(n);
    }
    return n == 1;
}
private int sumOfSquares(int n) {
    int sum = 0;
    while (n > 0) { int d = n % 10; sum += d * d; n /= 10; }
    return sum;
}
```
**Approach:** A HashSet detects a cycle (non-happy numbers loop forever in a repeating cycle instead of terminating) — equivalent in concept to Floyd's cycle detection but simpler to reason about with a set.
**Tricks/Pitfalls:** Floyd's cycle detection (slow/fast pointer on the sequence) achieves the same result in O(1) space — a good follow-up optimization to mention.
**Complexity:** Time O(log n) per sum-of-squares call, bounded total iterations, Space O(n) (O(1) with Floyd's variant).


## Category 5: Binary Tree & BST

> Note: Trees are shown in LeetCode's level-order array format, e.g., `[3,9,20,null,null,15,7]`.

### 67. Maximum Depth of Binary Tree
**Problem:** Given the root of a binary tree, return its maximum depth (the number of nodes along the longest path from root to a leaf).

**Example:**
```
Input: root = [3,9,20,null,null,15,7]
Output: 3
```
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```
**Approach:** Depth of a node = 1 + max depth of its subtrees; classic bottom-up recursion.
**Tricks/Pitfalls:** Iterative BFS level-counting is an equally valid alternative if recursion depth is a concern for very unbalanced trees.
**Complexity:** Time O(n), Space O(h) recursion stack (h = height).

### 68. Same Tree
**Problem:** Given the roots of two binary trees, check if they are the same (structurally identical with the same node values).

**Example:**
```
Input: p = [1,2,3], q = [1,2,3]
Output: true
```
```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null || p.val != q.val) return false;
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```
**Approach:** Recursively compare node values and both subtrees simultaneously.
**Tricks/Pitfalls:** Check both-null (equal) and either-null (unequal) before dereferencing `.val`.
**Complexity:** Time O(n), Space O(h).

### 69. Invert Binary Tree
**Problem:** Given the root of a binary tree, invert the tree (mirror it left-to-right), and return its root.

**Example:**
```
Input: root = [4,2,7,1,3,6,9]
Output: [4,7,2,9,6,3,1]
```
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);
    root.left = right; root.right = left;
    return root;
}
```
**Approach:** Recursively invert both subtrees, then swap them at the current node.
**Tricks/Pitfalls:** Order doesn't matter (swap before or after recursing), but must actually reassign both `left`/`right` — a classic "famous whiteboard question" (notably referenced in tech-industry interview folklore).
**Complexity:** Time O(n), Space O(h).

### 70. Symmetric Tree
**Problem:** Given the root of a binary tree, check whether it is a mirror of itself (symmetric around its center).

**Example:**
```
Input: root = [1,2,2,3,4,4,3]
Output: true
```
```java
public boolean isSymmetric(TreeNode root) {
    return root == null || isMirror(root.left, root.right);
}
private boolean isMirror(TreeNode a, TreeNode b) {
    if (a == null && b == null) return true;
    if (a == null || b == null || a.val != b.val) return false;
    return isMirror(a.left, b.right) && isMirror(a.right, b.left); // cross comparison
}
```
**Approach:** Compare the left subtree of one side against the right subtree of the other, recursively — the "cross" comparison is the key insight.
**Tricks/Pitfalls:** Comparing `a.left` to `b.left` (instead of `b.right`) checks for structural equality, not mirror symmetry — a common mix-up.
**Complexity:** Time O(n), Space O(h).

### 71. Diameter of Binary Tree
**Problem:** Given the root of a binary tree, return the length (in edges) of the longest path between any two nodes.

**Example:**
```
Input: root = [1,2,3,4,5]
Output: 3
Explanation: The path [4,2,1,3] or [5,2,1,3] has length 3.
```
```java
private int diameter = 0;
public int diameterOfBinaryTree(TreeNode root) {
    height(root);
    return diameter;
}
private int height(TreeNode node) {
    if (node == null) return 0;
    int left = height(node.left), right = height(node.right);
    diameter = Math.max(diameter, left + right); // path through this node
    return 1 + Math.max(left, right);
}
```
**Approach:** While computing height bottom-up, also track the best "left height + right height" seen at any node — that sum is the diameter through that node.
**Tricks/Pitfalls:** The diameter doesn't necessarily pass through the root — must track a global max across all nodes, not just return the root's left+right.
**Complexity:** Time O(n), Space O(h).

### 72. Balanced Binary Tree
**Problem:** Given the root of a binary tree, determine if it is height-balanced (the depth of the two subtrees of every node never differs by more than 1).

**Example:**
```
Input: root = [3,9,20,null,null,15,7]
Output: true
```
```java
public boolean isBalanced(TreeNode root) { return height(root) != -1; }
private int height(TreeNode node) {
    if (node == null) return 0;
    int left = height(node.left);
    if (left == -1) return -1;
    int right = height(node.right);
    if (right == -1) return -1;
    if (Math.abs(left - right) > 1) return -1; // sentinel for "unbalanced found"
    return 1 + Math.max(left, right);
}
```
**Approach:** Use `-1` as a sentinel to signal "already unbalanced," short-circuiting further computation instead of recomputing height and balance separately (which would be O(n²)).
**Tricks/Pitfalls:** A naive approach that calls a separate `height()` for every node is O(n²) — combining the check into the height computation itself gets O(n).
**Complexity:** Time O(n), Space O(h).

### 73. Binary Tree Level Order Traversal
**Problem:** Given the root of a binary tree, return the level order traversal of its nodes' values (left to right, level by level).

**Example:**
```
Input: root = [3,9,20,null,null,15,7]
Output: [[3],[9,20],[15,7]]
```
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```
**Approach:** BFS with a queue; capture `queue.size()` before the inner loop to process exactly one level at a time.
**Tricks/Pitfalls:** Capturing `size` upfront (not re-checking `queue.size()` inside the loop, which changes as children are added) is essential to separate levels correctly.
**Complexity:** Time O(n), Space O(n).

### 74. Binary Tree Zigzag Level Order Traversal
**Problem:** Given the root of a binary tree, return the zigzag level order traversal (left-to-right, then right-to-left, alternating per level).

**Example:**
```
Input: root = [3,9,20,null,null,15,7]
Output: [[3],[20,9],[15,7]]
```
```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;
    while (!queue.isEmpty()) {
        int size = queue.size();
        LinkedList<Integer> level = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            if (leftToRight) level.addLast(node.val); else level.addFirst(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
        leftToRight = !leftToRight;
    }
    return result;
}
```
**Approach:** Same BFS as level order, but use a `LinkedList` for O(1) `addFirst`/`addLast` to build each level in the required direction without a post-processing reverse.
**Tricks/Pitfalls:** Reversing the level list *after* building it also works but is less elegant than directly inserting in the correct direction.
**Complexity:** Time O(n), Space O(n).

### 75. Binary Tree Right Side View
**Problem:** Given the root of a binary tree, return the values of the nodes you can see ordered from top to bottom when looking from the right side.

**Example:**
```
Input: root = [1,2,3,null,5,null,4]
Output: [1,3,4]
```
```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            if (i == size - 1) result.add(node.val); // last node processed in this level
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    return result;
}
```
**Approach:** BFS level order, but only record the last node dequeued per level (the rightmost one).
**Tricks/Pitfalls:** A DFS variant (visit right subtree first, record the first node seen at each depth) also works and uses less auxiliary space in the average case.
**Complexity:** Time O(n), Space O(n).

### 76. Validate Binary Search Tree
**Problem:** Given the root of a binary tree, determine if it is a valid binary search tree (BST).

**Example:**
```
Input: root = [5,1,4,null,null,3,6]
Output: false
Explanation: The root's value is 5, but its right child's left child (3) is less than 5.
```
```java
public boolean isValidBST(TreeNode root) { return validate(root, Long.MIN_VALUE, Long.MAX_VALUE); }
private boolean validate(TreeNode node, long lower, long upper) {
    if (node == null) return true;
    if (node.val <= lower || node.val >= upper) return false;
    return validate(node.left, lower, node.val) && validate(node.right, node.val, upper);
}
```
**Approach:** Pass down a valid (lower, upper) range for each node; every node must satisfy the range imposed by *all* its ancestors, not just its immediate parent.
**Tricks/Pitfalls:** Checking only `node.left.val < node.val < node.right.val` locally is wrong — a right-left grandchild could still violate a value further up the tree; using `long` bounds avoids edge-case overflow issues with `Integer.MIN/MAX_VALUE`.
**Complexity:** Time O(n), Space O(h).

### 77. Kth Smallest Element in a BST
**Problem:** Given the root of a binary search tree and an integer `k`, return the kth smallest value (1-indexed) among all node values.

**Example:**
```
Input: root = [3,1,4,null,2], k = 1
Output: 1
```
```java
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode curr = root;
    while (curr != null || !stack.isEmpty()) {
        while (curr != null) { stack.push(curr); curr = curr.left; }
        curr = stack.pop();
        if (--k == 0) return curr.val;
        curr = curr.right;
    }
    throw new IllegalArgumentException("k out of range");
}
```
**Approach:** Iterative in-order traversal (left, node, right) naturally visits BST nodes in ascending order; stop at the kth visit.
**Tricks/Pitfalls:** Iterative in-order avoids building a full sorted list first (O(n) space regardless) — you get early exit at exactly the kth element without traversing the whole tree.
**Complexity:** Time O(h + k), Space O(h).

### 78. Lowest Common Ancestor of a BST
**Problem:** Given a binary search tree and two nodes `p` and `q`, find their lowest common ancestor (LCA).

**Example:**
```
Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
Output: 6
```
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    TreeNode curr = root;
    while (curr != null) {
        if (p.val < curr.val && q.val < curr.val) curr = curr.left;
        else if (p.val > curr.val && q.val > curr.val) curr = curr.right;
        else return curr; // split point found
    }
    return null;
}
```
**Approach:** Exploit the BST property — if both targets are smaller, go left; both larger, go right; otherwise the current node is where their paths diverge (the LCA).
**Tricks/Pitfalls:** This is O(h), much faster than the general binary-tree LCA algorithm (problem 79) — recognizing you can use BST ordering is the key insight interviewers look for.
**Complexity:** Time O(h), Space O(1) iterative.

### 79. Lowest Common Ancestor of a Binary Tree (General)
**Problem:** Given a general (non-BST) binary tree and two nodes `p` and `q`, find their lowest common ancestor.

**Example:**
```
Input: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
Output: 3
```
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    if (left != null && right != null) return root; // p and q found on different sides
    return left != null ? left : right;
}
```
**Approach:** Recursively search both subtrees; if both return non-null, the current node is the split point (LCA). If only one side returns non-null, propagate that result upward.
**Tricks/Pitfalls:** This single elegant recursive function handles both "p is an ancestor of q" and "p, q in different subtrees" cases without special-casing either.
**Complexity:** Time O(n), Space O(h).

### 80. Binary Tree Maximum Path Sum
**Problem:** Given the root of a binary tree, return the maximum path sum of any non-empty path (a path may start and end at any node).

**Example:**
```
Input: root = [-10,9,20,null,null,15,7]
Output: 42
Explanation: The path 15 -> 20 -> 7 sums to 42.
```
```java
private int maxSum = Integer.MIN_VALUE;
public int maxPathSum(TreeNode root) {
    maxGain(root);
    return maxSum;
}
private int maxGain(TreeNode node) {
    if (node == null) return 0;
    int leftGain = Math.max(maxGain(node.left), 0);   // ignore negative contributions
    int rightGain = Math.max(maxGain(node.right), 0);
    maxSum = Math.max(maxSum, node.val + leftGain + rightGain); // path through this node
    return node.val + Math.max(leftGain, rightGain);  // best single-branch path upward
}
```
**Approach:** At each node, the best "pass-through" path (for the global answer) may use both children, but the value *returned* upward can only use one branch (a path can't branch twice).
**Tricks/Pitfalls:** Clamping negative gains to 0 correctly handles all-negative subtrees (better to not extend into them at all).
**Complexity:** Time O(n), Space O(h).

### 81. Serialize and Deserialize Binary Tree
**Problem:** Design an algorithm to serialize a binary tree to a string and deserialize the string back to the original tree.

**Example:**
```
Input: root = [1,2,3,null,null,4,5]
Serialized: "1,2,N,N,3,4,N,N,5,N,N,"
Output (after deserialize): [1,2,3,null,null,4,5]
```
```java
public String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serializeHelper(root, sb);
    return sb.toString();
}
private void serializeHelper(TreeNode node, StringBuilder sb) {
    if (node == null) { sb.append("N,"); return; }
    sb.append(node.val).append(',');
    serializeHelper(node.left, sb);
    serializeHelper(node.right, sb);
}
public TreeNode deserialize(String data) {
    Deque<String> nodes = new ArrayDeque<>(Arrays.asList(data.split(",")));
    return deserializeHelper(nodes);
}
private TreeNode deserializeHelper(Deque<String> nodes) {
    String val = nodes.poll();
    if (val.equals("N")) return null;
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left = deserializeHelper(nodes);
    node.right = deserializeHelper(nodes);
    return node;
}
```
**Approach:** Pre-order traversal with explicit "null" markers fully captures tree structure, allowing exact reconstruction by consuming tokens in the same order they were produced.
**Tricks/Pitfalls:** Without null markers, pre-order alone is ambiguous — the markers are what make this reversible.
**Complexity:** Time O(n), Space O(n).

### 82. Construct Binary Tree from Preorder and Inorder Traversal
**Problem:** Given two integer arrays `preorder` and `inorder` representing the preorder and inorder traversal of a binary tree, construct and return the tree.

**Example:**
```
Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
Output: [3,9,20,null,null,15,7]
```
```java
private int preIdx = 0;
public TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderIndex = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) inorderIndex.put(inorder[i], i);
    return build(preorder, 0, inorder.length - 1, inorderIndex);
}
private TreeNode build(int[] preorder, int lo, int hi, Map<Integer, Integer> inorderIndex) {
    if (lo > hi) return null;
    int rootVal = preorder[preIdx++];
    TreeNode root = new TreeNode(rootVal);
    int mid = inorderIndex.get(rootVal);
    root.left = build(preorder, lo, mid - 1, inorderIndex);
    root.right = build(preorder, mid + 1, hi, inorderIndex);
    return root;
}
```
**Approach:** Preorder's first element is always the current subtree's root; its position in inorder splits the remaining elements into left/right subtrees.
**Tricks/Pitfalls:** A HashMap for inorder value→index lookups avoids O(n) linear search per call, keeping overall complexity at O(n) instead of O(n²).
**Complexity:** Time O(n), Space O(n).

### 83. Path Sum II
**Problem:** Given the root of a binary tree and a target sum, return all root-to-leaf paths where each path's sum equals the target.

**Example:**
```
Input: root = [5,4,8,11,null,13,4,7,2,null,null,5,1], targetSum = 22
Output: [[5,4,11,2],[5,8,4,5]]
```
```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}
private void dfs(TreeNode node, long remaining, List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;
    path.add(node.val);
    remaining -= node.val;
    if (node.left == null && node.right == null && remaining == 0) result.add(new ArrayList<>(path));
    else { dfs(node.left, remaining, path, result); dfs(node.right, remaining, path, result); }
    path.remove(path.size() - 1); // backtrack
}
```
**Approach:** DFS while carrying the current path and remaining sum; on a valid leaf, snapshot the path (must copy — `new ArrayList<>(path)` — since `path` is mutated afterward).
**Tricks/Pitfalls:** Forgetting to backtrack (`path.remove(...)`) leaves stale values corrupting sibling paths — the single most common backtracking bug.
**Complexity:** Time O(n²) worst case (path copying), Space O(h).

### 84. Convert Sorted Array to Binary Search Tree
**Problem:** Given an integer array sorted in ascending order, convert it to a height-balanced binary search tree.

**Example:**
```
Input: nums = [-10,-3,0,5,9]
Output: [0,-3,9,-10,null,5] (one valid balanced BST)
```
```java
public TreeNode sortedArrayToBST(int[] nums) { return build(nums, 0, nums.length - 1); }
private TreeNode build(int[] nums, int lo, int hi) {
    if (lo > hi) return null;
    int mid = lo + (hi - lo) / 2;
    TreeNode node = new TreeNode(nums[mid]);
    node.left = build(nums, lo, mid - 1);
    node.right = build(nums, mid + 1, hi);
    return node;
}
```
**Approach:** Always pick the middle element as root, guaranteeing the left/right subtree sizes differ by at most 1 at every level — this is what produces balance.
**Tricks/Pitfalls:** When the subarray has even length, either middle candidate produces a valid balanced tree (just a different valid BST) — no need to overthink the tie-break.
**Complexity:** Time O(n), Space O(log n) recursion stack.

### 85. Binary Search Tree Iterator
**Problem:** Design an iterator over a binary search tree that returns the next smallest number in the BST, with `next()` and `hasNext()` in O(1) average time.

**Example:**
```
Input: root = [7,3,15,null,null,9,20]; next() -> 3; next() -> 7; hasNext() -> true; next() -> 9
Output: [3, 7, true, 9]
```
```java
class BSTIterator {
    private final Deque<TreeNode> stack = new ArrayDeque<>();
    public BSTIterator(TreeNode root) { pushLeft(root); }
    public int next() {
        TreeNode node = stack.pop();
        pushLeft(node.right);
        return node.val;
    }
    public boolean hasNext() { return !stack.isEmpty(); }
    private void pushLeft(TreeNode node) { while (node != null) { stack.push(node); node = node.left; } }
}
```
**Approach:** Maintain a stack representing the "spine" of unvisited left-leaning nodes; `next()` pops the smallest remaining and pushes its right subtree's left spine.
**Tricks/Pitfalls:** This achieves O(h) worst-case per call but O(1) *amortized* across all calls, since each node is pushed/popped exactly once overall — an important distinction to state clearly in an interview.
**Complexity:** Time O(1) amortized per `next()`, Space O(h).

### 86. Count Good Nodes in Binary Tree
**Problem:** Given a binary tree, a node is "good" if the path from the root to that node contains no value greater than the node's value. Return the count of good nodes.

**Example:**
```
Input: root = [3,1,4,3,null,1,5]
Output: 4
```
```java
public int goodNodes(TreeNode root) { return dfs(root, Integer.MIN_VALUE); }
private int dfs(TreeNode node, int maxSoFar) {
    if (node == null) return 0;
    int count = (node.val >= maxSoFar) ? 1 : 0;
    int newMax = Math.max(maxSoFar, node.val);
    return count + dfs(node.left, newMax) + dfs(node.right, newMax);
}
```
**Approach:** Pass the running maximum down the recursion; a node is "good" if it's ≥ that running max.
**Tricks/Pitfalls:** Passing the max as a parameter (rather than a shared mutable field) makes the recursion cleanly stateless and easy to reason about, and correctly isolates left/right subtree computations.
**Complexity:** Time O(n), Space O(h).


## Category 6: Graphs

### 87. Number of Islands
**Problem:** Given an `m x n` 2D binary grid representing a map of '1's (land) and '0's (water), return the number of islands (land connected horizontally/vertically).

**Example:**
```
Input: grid = [["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]]
Output: 3
```
```java
public int numIslands(char[][] grid) {
    int count = 0;
    for (int i = 0; i < grid.length; i++)
        for (int j = 0; j < grid[0].length; j++)
            if (grid[i][j] == '1') { count++; dfs(grid, i, j); }
    return count;
}
private void dfs(char[][] grid, int i, int j) {
    if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] != '1') return;
    grid[i][j] = '0'; // mark visited by sinking the island
    dfs(grid, i + 1, j); dfs(grid, i - 1, j); dfs(grid, i, j + 1); dfs(grid, i, j - 1);
}
```
**Approach:** Scan every cell; on finding an unvisited land cell, DFS/flood-fill to sink the entire connected island, incrementing the island count once per island.
**Tricks/Pitfalls:** Mutating the grid in place (`'1' → '0'`) avoids a separate visited array — fine unless the input must stay unmodified, in which case use a `boolean[][] visited`.
**Complexity:** Time O(m·n), Space O(m·n) worst-case recursion stack.

### 88. Flood Fill
**Problem:** Given an image (2D array of pixel values), a starting pixel, and a new color, perform a flood fill starting from that pixel.

**Example:**
```
Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]
```
```java
public int[][] floodFill(int[][] image, int sr, int sc, int color) {
    int startColor = image[sr][sc];
    if (startColor != color) fill(image, sr, sc, startColor, color);
    return image;
}
private void fill(int[][] image, int i, int j, int startColor, int color) {
    if (i < 0 || j < 0 || i >= image.length || j >= image[0].length || image[i][j] != startColor) return;
    image[i][j] = color;
    fill(image, i + 1, j, startColor, color); fill(image, i - 1, j, startColor, color);
    fill(image, i, j + 1, startColor, color); fill(image, i, j - 1, startColor, color);
}
```
**Approach:** Basic DFS flood fill; the `startColor != color` guard at the top prevents infinite recursion when the fill color equals the existing color.
**Tricks/Pitfalls:** Without that guard, filling a region with its own existing color causes infinite recursion (the "already correct color" cells never terminate the DFS).
**Complexity:** Time O(m·n), Space O(m·n).

### 89. Clone Graph
**Problem:** Given a reference to a node in a connected undirected graph, return a deep copy (clone) of the graph.

**Example:**
```
Input: adjList = [[2,4],[1,3],[2,4],[1,3]]  (node 1 connects to 2 and 4, etc.)
Output: A deep copy with identical adjacency structure but entirely new node objects.
```
```java
public Node cloneGraph(Node node) {
    if (node == null) return null;
    Map<Node, Node> visited = new HashMap<>();
    return dfs(node, visited);
}
private Node dfs(Node node, Map<Node, Node> visited) {
    if (visited.containsKey(node)) return visited.get(node);
    Node clone = new Node(node.val);
    visited.put(node, clone);
    for (Node neighbor : node.neighbors) clone.neighbors.add(dfs(neighbor, visited));
    return clone;
}
```
**Approach:** DFS with a map from original node → clone, both to avoid infinite recursion on cycles and to reuse already-cloned nodes.
**Tricks/Pitfalls:** Putting the clone in `visited` *before* recursing into neighbors is essential — otherwise cyclic graphs cause infinite recursion.
**Complexity:** Time O(V + E), Space O(V).

### 90. Course Schedule (Cycle Detection / Topological Sort)
**Problem:** There are `numCourses` courses labeled 0 to numCourses-1. Given prerequisite pairs `[a, b]` (must take b before a), determine if it's possible to finish all courses.

**Example:**
```
Input: numCourses = 2, prerequisites = [[1,0]]
Output: true
Explanation: Take course 0, then course 1.

Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
Output: false (cycle)
```
```java
public boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] p : prerequisites) { graph.get(p[1]).add(p[0]); indegree[p[0]]++; }
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) queue.offer(i);
    int visited = 0;
    while (!queue.isEmpty()) {
        int curr = queue.poll();
        visited++;
        for (int next : graph.get(curr)) if (--indegree[next] == 0) queue.offer(next);
    }
    return visited == numCourses; // if not all visited, a cycle exists
}
```
**Approach:** Kahn's algorithm (BFS topological sort) — repeatedly remove nodes with in-degree 0; if all nodes are eventually removed, there's no cycle.
**Tricks/Pitfalls:** DFS with a 3-state (unvisited/visiting/visited) coloring is the alternative cycle-detection approach — know both, since interviewers sometimes ask for the DFS variant specifically.
**Complexity:** Time O(V + E), Space O(V + E).

### 91. Course Schedule II (Return the Order)
**Problem:** Same setup as Course Schedule, but return a valid ordering of courses to finish all of them, or an empty array if impossible.

**Example:**
```
Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
Output: [0,1,2,3] (or [0,2,1,3])
```
```java
public int[] findOrder(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] p : prerequisites) { graph.get(p[1]).add(p[0]); indegree[p[0]]++; }
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) queue.offer(i);
    int[] order = new int[numCourses];
    int idx = 0;
    while (!queue.isEmpty()) {
        int curr = queue.poll();
        order[idx++] = curr;
        for (int next : graph.get(curr)) if (--indegree[next] == 0) queue.offer(next);
    }
    return idx == numCourses ? order : new int[0];
}
```
**Approach:** Same Kahn's algorithm as problem 90, but record the removal order — this order is itself a valid topological sort.
**Tricks/Pitfalls:** The order in which same-in-degree-0 nodes are processed doesn't matter for correctness — any valid topological order is accepted.
**Complexity:** Time O(V + E), Space O(V + E).

### 92. Pacific Atlantic Water Flow
**Problem:** Given an `m x n` matrix of heights representing a continent, find all cells from which water can flow to both the Pacific (top/left edges) and Atlantic (bottom/right edges) oceans.

**Example:**
```
Input: heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]
Output: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
```
```java
public List<List<Integer>> pacificAtlantic(int[][] heights) {
    int rows = heights.length, cols = heights[0].length;
    boolean[][] pacific = new boolean[rows][cols], atlantic = new boolean[rows][cols];
    for (int i = 0; i < rows; i++) { dfs(heights, i, 0, pacific); dfs(heights, i, cols - 1, atlantic); }
    for (int j = 0; j < cols; j++) { dfs(heights, 0, j, pacific); dfs(heights, rows - 1, j, atlantic); }
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            if (pacific[i][j] && atlantic[i][j]) result.add(List.of(i, j));
    return result;
}
private void dfs(int[][] heights, int i, int j, boolean[][] visited) {
    visited[i][j] = true;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    for (int[] d : dirs) {
        int ni = i + d[0], nj = j + d[1];
        if (ni < 0 || nj < 0 || ni >= heights.length || nj >= heights[0].length || visited[ni][nj]) continue;
        if (heights[ni][nj] < heights[i][j]) continue; // water flows downhill only
        dfs(heights, ni, nj, visited);
    }
}
```
**Approach:** Reverse the problem — instead of checking "can water flow from every cell to the ocean" (expensive), flood-fill **inward from each ocean's border**, since water flowing downhill from A to B means it's equally valid to flow "uphill" from B to A in the reverse search.
**Tricks/Pitfalls:** The reversal (searching from ocean borders inward, using `>=` height comparison) is the key insight that turns an O((mn)²) brute force into O(mn).
**Complexity:** Time O(m·n), Space O(m·n).

### 93. Number of Connected Components (Union-Find)
**Problem:** Given `n` nodes labeled 0 to n-1 and a list of undirected edges, count the number of connected components.

**Example:**
```
Input: n = 5, edges = [[0,1],[1,2],[3,4]]
Output: 2
```
```java
public int countComponents(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    int components = n;
    for (int[] e : edges) if (uf.union(e[0], e[1])) components--;
    return components;
}
// UnionFind class as defined in Part 1, Section 11
```
**Approach:** Start assuming every node is its own component (`n` total); each successful `union()` (i.e., merging two previously-separate sets) reduces the component count by 1.
**Tricks/Pitfalls:** DFS/BFS from each unvisited node also solves this in O(V+E) — Union-Find is preferred when edges arrive incrementally/dynamically (e.g., streaming edge updates).
**Complexity:** Time O(V + E·α(n)) ≈ O(V + E), Space O(V).

### 94. Graph Valid Tree
**Problem:** Given `n` nodes and a list of undirected edges, determine if these edges form a valid tree (fully connected, no cycles).

**Example:**
```
Input: n = 5, edges = [[0,1],[0,2],[0,3],[1,4]]
Output: true

Input: n = 5, edges = [[0,1],[1,2],[2,3],[1,3],[1,4]]
Output: false (cycle between 1,2,3)
```
```java
public boolean validTree(int n, int[][] edges) {
    if (edges.length != n - 1) return false; // a tree has exactly n-1 edges
    UnionFind uf = new UnionFind(n);
    for (int[] e : edges) if (!uf.union(e[0], e[1])) return false; // cycle detected
    return true;
}
```
**Approach:** A valid tree with n nodes must have exactly n-1 edges *and* no cycles — check the edge count first (cheap early exit), then use Union-Find to confirm no cycle.
**Tricks/Pitfalls:** The edge-count precheck is essential — without it, a disconnected graph with n-1 edges but distributed across disjoint cyclic pieces could otherwise slip through incorrectly (though in practice cycle detection would still catch most cases; the count check makes the logic airtight and O(1) cheap).
**Complexity:** Time O(V + E), Space O(V).

### 95. Word Ladder
**Problem:** Given `beginWord`, `endWord`, and a `wordList`, find the length of the shortest transformation sequence changing one letter at a time, where every intermediate word must exist in `wordList`. Return 0 if no such sequence exists.

**Example:**
```
Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
Output: 5
Explanation: "hit" -> "hot" -> "dot" -> "dog" -> "cog"
```
```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(endWord)) return 0;
    Queue<String> queue = new LinkedList<>();
    queue.offer(beginWord);
    Set<String> visited = new HashSet<>();
    visited.add(beginWord);
    int level = 1;
    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            if (word.equals(endWord)) return level;
            char[] chars = word.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char original = chars[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    chars[j] = c;
                    String next = new String(chars);
                    if (dict.contains(next) && visited.add(next)) queue.offer(next);
                }
                chars[j] = original;
            }
        }
        level++;
    }
    return 0;
}
```
**Approach:** BFS treats each word as a graph node; edges connect words that differ by exactly one letter — BFS naturally finds the shortest transformation path since it explores level by level.
**Tricks/Pitfalls:** Generating all 26-letter variants per position (rather than comparing against every word in the dictionary) keeps the branching factor bounded and manageable.
**Complexity:** Time O(N·L·26) where N = word count, L = word length, Space O(N·L).

### 96. Rotting Oranges (Multi-Source BFS)
**Problem:** Given a grid where 0 = empty, 1 = fresh orange, 2 = rotten orange, return the minimum number of minutes until no fresh orange remains, or -1 if impossible.

**Example:**
```
Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
Output: 4
```
```java
public int orangesRotting(int[][] grid) {
    Queue<int[]> queue = new LinkedList<>();
    int fresh = 0;
    for (int i = 0; i < grid.length; i++)
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == 2) queue.offer(new int[]{i, j});
            else if (grid[i][j] == 1) fresh++;
        }
    int minutes = 0;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!queue.isEmpty() && fresh > 0) {
        int size = queue.size();
        for (int k = 0; k < size; k++) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int ni = cell[0] + d[0], nj = cell[1] + d[1];
                if (ni < 0 || nj < 0 || ni >= grid.length || nj >= grid[0].length || grid[ni][nj] != 1) continue;
                grid[ni][nj] = 2; fresh--;
                queue.offer(new int[]{ni, nj});
            }
        }
        minutes++;
    }
    return fresh == 0 ? minutes : -1;
}
```
**Approach:** Seed BFS from *all* initially-rotten oranges simultaneously (multi-source BFS) — each BFS "layer" corresponds to exactly one minute passing.
**Tricks/Pitfalls:** Starting BFS from every rotten orange at once (not one at a time) is what correctly models simultaneous spreading each minute.
**Complexity:** Time O(m·n), Space O(m·n).

### 97. Network Delay Time (Dijkstra)
**Problem:** Given a network of `n` nodes and directed weighted edges `times[i] = [u, v, w]`, and a starting node `k`, find the time it takes for a signal to reach all nodes, or -1 if impossible.

**Example:**
```
Input: times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2
Output: 2
```
```java
public int networkDelayTime(int[][] times, int n, int k) {
    Map<Integer, List<int[]>> graph = new HashMap<>();
    for (int[] t : times) graph.computeIfAbsent(t[0], key -> new ArrayList<>()).add(new int[]{t[1], t[2]});
    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
    pq.offer(new int[]{k, 0});
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        if (curr[1] > dist[curr[0]]) continue;
        for (int[] edge : graph.getOrDefault(curr[0], List.of())) {
            int next = edge[0], newDist = curr[1] + edge[1];
            if (newDist < dist[next]) { dist[next] = newDist; pq.offer(new int[]{next, newDist}); }
        }
    }
    int maxTime = 0;
    for (int i = 1; i <= n; i++) {
        if (dist[i] == Integer.MAX_VALUE) return -1;
        maxTime = Math.max(maxTime, dist[i]);
    }
    return maxTime;
}
```
**Approach:** Standard Dijkstra from source `k`; the answer is the maximum shortest-path distance across all nodes (the last node to receive the signal).
**Tricks/Pitfalls:** Skipping stale heap entries (`curr[1] > dist[curr[0]]`) avoids reprocessing a node with outdated distance info, keeping the algorithm efficient.
**Complexity:** Time O((V + E) log V), Space O(V + E).

### 98. Cheapest Flights Within K Stops (Bellman-Ford Style)
**Problem:** Given `n` cities, flights `[from, to, price]`, and `src`, `dst`, `k`, find the cheapest price from src to dst with at most k stops, or -1 if not possible.

**Example:**
```
Input: n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src = 0, dst = 3, k = 1
Output: 700
Explanation: 0 -> 1 -> 3 costs 100+600=700 (using at most 1 stop).
```
```java
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int i = 0; i <= k; i++) {                 // relax edges k+1 times (k stops = k+1 edges)
        int[] temp = dist.clone();
        for (int[] f : flights) {
            int u = f[0], v = f[1], w = f[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < temp[v]) temp[v] = dist[u] + w;
        }
        dist = temp;
    }
    return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
}
```
**Approach:** A bounded-relaxation-count Bellman-Ford — since Dijkstra doesn't naturally support a "stop count" constraint, limited-iteration edge relaxation (each iteration = one more allowed hop) fits perfectly.
**Tricks/Pitfalls:** Cloning `dist` into `temp` each iteration prevents using an edge relaxed *within the same iteration* (which would effectively allow more hops than permitted).
**Complexity:** Time O(k·E), Space O(n).

### 99. Redundant Connection (Union-Find Cycle Detection)
**Problem:** A tree with `n` nodes originally had n-1 edges, but one extra edge was added creating exactly one cycle. Find an edge that can be removed to restore the tree (if multiple answers, return the one that appears last in the input).

**Example:**
```
Input: edges = [[1,2],[1,3],[2,3]]
Output: [2,3]
```
```java
public int[] findRedundantConnection(int[][] edges) {
    UnionFind uf = new UnionFind(edges.length + 1);
    for (int[] e : edges) if (!uf.union(e[0], e[1])) return e; // first edge that creates a cycle
    return new int[0];
}
```
**Approach:** Process edges in order; the first edge whose `union()` fails (both endpoints already connected) is the redundant one that created the cycle.
**Tricks/Pitfalls:** Because we return the *first* such edge encountered while processing in input order, this correctly matches LeetCode's requirement to return the redundant edge that appears last in the input among all valid answers.
**Complexity:** Time O(n·α(n)) ≈ O(n), Space O(n).

### 100. Surrounded Regions
**Problem:** Given an `m x n` board containing 'X' and 'O', flip all 'O's that are not connected to a border 'O' into 'X'.

**Example:**
```
Input: board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]
Output: [["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]
```
```java
public void solve(char[][] board) {
    int rows = board.length, cols = board[0].length;
    for (int i = 0; i < rows; i++) { markSafe(board, i, 0); markSafe(board, i, cols - 1); }
    for (int j = 0; j < cols; j++) { markSafe(board, 0, j); markSafe(board, rows - 1, j); }
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++) {
            if (board[i][j] == 'O') board[i][j] = 'X';
            else if (board[i][j] == 'S') board[i][j] = 'O';
        }
}
private void markSafe(char[][] board, int i, int j) {
    if (i < 0 || j < 0 || i >= board.length || j >= board[0].length || board[i][j] != 'O') return;
    board[i][j] = 'S'; // temporarily mark as safe
    markSafe(board, i + 1, j); markSafe(board, i - 1, j); markSafe(board, i, j + 1); markSafe(board, i, j - 1);
}
```
**Approach:** Same "search from the border inward" reversal trick as Pacific Atlantic — mark all border-connected 'O's as safe first, then flip everything else.
**Tricks/Pitfalls:** Attempting to flood-fill *from* interior 'O' regions outward to check border-connectivity is the natural but expensive/incorrect-order approach; starting from the border is the key reversal.
**Complexity:** Time O(m·n), Space O(m·n).

### 101. Is Graph Bipartite
**Problem:** Given an undirected graph as an adjacency list, determine if it is bipartite (can be 2-colored such that no adjacent nodes share a color).

**Example:**
```
Input: graph = [[1,2,3],[0,2],[0,1,3],[0,2]]
Output: false
```
```java
public boolean isBipartite(int[][] graph) {
    int n = graph.length;
    int[] colors = new int[n]; // 0 = uncolored, 1 or -1 = colors
    for (int i = 0; i < n; i++) {
        if (colors[i] != 0) continue;
        colors[i] = 1;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(i);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : graph[node]) {
                if (colors[neighbor] == 0) { colors[neighbor] = -colors[node]; queue.offer(neighbor); }
                else if (colors[neighbor] == colors[node]) return false; // conflict
            }
        }
    }
    return true;
}
```
**Approach:** BFS coloring — assign the opposite color to every neighbor; a conflict (same color on both ends of an edge) means the graph isn't bipartite.
**Tricks/Pitfalls:** Must iterate over all components (the outer loop over every node) — a disconnected graph could have one bipartite component and one non-bipartite one.
**Complexity:** Time O(V + E), Space O(V).

### 102. Alien Dictionary (Topological Sort on Characters)
**Problem:** Given a list of words sorted lexicographically by the rules of an alien language, derive a valid character order of that alien alphabet.

**Example:**
```
Input: words = ["wrt","wrf","er","ett","rftt"]
Output: "wertf"
```
```java
public String alienOrder(String[] words) {
    Map<Character, Set<Character>> graph = new HashMap<>();
    Map<Character, Integer> indegree = new HashMap<>();
    for (String w : words) for (char c : w.toCharArray()) { graph.putIfAbsent(c, new HashSet<>()); indegree.putIfAbsent(c, 0); }
    for (int i = 0; i < words.length - 1; i++) {
        String w1 = words[i], w2 = words[i + 1];
        int minLen = Math.min(w1.length(), w2.length());
        boolean foundDiff = false;
        for (int j = 0; j < minLen; j++) {
            char c1 = w1.charAt(j), c2 = w2.charAt(j);
            if (c1 != c2) {
                if (graph.get(c1).add(c2)) indegree.merge(c2, 1, Integer::sum);
                foundDiff = true;
                break;
            }
        }
        if (!foundDiff && w1.length() > w2.length()) return ""; // invalid: "abc" before "ab"
    }
    Queue<Character> queue = new LinkedList<>();
    for (char c : indegree.keySet()) if (indegree.get(c) == 0) queue.offer(c);
    StringBuilder result = new StringBuilder();
    while (!queue.isEmpty()) {
        char c = queue.poll();
        result.append(c);
        for (char next : graph.get(c)) if (--indegree.merge(next, -1, Integer::sum) == 0) queue.offer(next);
    }
    return result.length() == indegree.size() ? result.toString() : ""; // cycle -> impossible
}
```
**Approach:** Build a character-level directed graph from the first differing letter between each pair of adjacent words, then Kahn's algorithm gives a valid alien alphabet order.
**Tricks/Pitfalls:** The "abc before ab" invalid-ordering check (`foundDiff == false` but the first word is longer) is an easy edge case to miss; also, only add an edge for the *first* differing character between each word pair — later differences are irrelevant.
**Complexity:** Time O(C) where C = total characters across all words, Space O(1) (bounded alphabet).


## Category 7: Dynamic Programming

### 103. Climbing Stairs
**Problem:** You're climbing a staircase of `n` steps. Each time you can climb 1 or 2 steps. Return the number of distinct ways to reach the top.

**Example:**
```
Input: n = 3
Output: 3
Explanation: (1+1+1), (1+2), (2+1)
```
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
```
**Approach:** `ways(n) = ways(n-1) + ways(n-2)` — this is Fibonacci in disguise; rolling variables avoid an O(n) array.
**Tricks/Pitfalls:** Recognizing this reduces to Fibonacci is the fastest path to the solution; naive recursion without memoization is O(2ⁿ).
**Complexity:** Time O(n), Space O(1).

### 104. House Robber
**Problem:** Given an array representing money in each house (arranged in a line), find the maximum amount you can rob without robbing two adjacent houses.

**Example:**
```
Input: nums = [1,2,3,1]
Output: 4
Explanation: Rob house 1 (1) and house 3 (3), total = 4.
```
```java
public int rob(int[] nums) {
    int prevNo = 0, prevYes = 0; // max profit ending without/with robbing current
    for (int n : nums) {
        int newYes = prevNo + n;
        prevNo = Math.max(prevNo, prevYes);
        prevYes = newYes;
    }
    return Math.max(prevNo, prevYes);
}
```
**Approach:** At each house, decide: rob it (add to best "not robbed previous" total) or skip it (carry forward the best so far).
**Tricks/Pitfalls:** Tracking two rolling variables (rob vs. not-rob) avoids a full DP array — O(1) space instead of O(n).
**Complexity:** Time O(n), Space O(1).

### 105. House Robber II (Circular)
**Problem:** Same as House Robber, but houses are arranged in a circle (the first and last house are adjacent).

**Example:**
```
Input: nums = [2,3,2]
Output: 3
Explanation: Robbing house 1 and 3 is not allowed (adjacent in circle); best is just house 2 (value 3).
```
```java
public int rob(int[] nums) {
    if (nums.length == 1) return nums[0];
    return Math.max(robLinear(nums, 0, nums.length - 2), robLinear(nums, 1, nums.length - 1));
}
private int robLinear(int[] nums, int start, int end) {
    int prevNo = 0, prevYes = 0;
    for (int i = start; i <= end; i++) {
        int newYes = prevNo + nums[i];
        prevNo = Math.max(prevNo, prevYes);
        prevYes = newYes;
    }
    return Math.max(prevNo, prevYes);
}
```
**Approach:** Since the first and last house can't both be robbed, run the linear House Robber twice — once excluding the last house, once excluding the first — and take the max.
**Tricks/Pitfalls:** Reusing the exact linear solution as a subroutine (rather than rewriting circular-specific logic) is the clean, low-bug way to solve this.
**Complexity:** Time O(n), Space O(1).

### 106. Coin Change
**Problem:** Given coin denominations and a target `amount`, find the fewest number of coins needed to make up that amount (unlimited supply of each coin), or -1 if impossible.

**Example:**
```
Input: coins = [1,2,5], amount = 11
Output: 3
Explanation: 11 = 5 + 5 + 1
```
```java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // sentinel for "unreachable"
    dp[0] = 0;
    for (int i = 1; i <= amount; i++)
        for (int coin : coins)
            if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
}
```
**Approach:** Bottom-up tabulation — `dp[i]` = minimum coins to make amount `i`, built from smaller sub-amounts.
**Tricks/Pitfalls:** Using `amount + 1` as an "infinity" sentinel (instead of `Integer.MAX_VALUE`, which risks overflow on `+1`) is a small but important robustness detail.
**Complexity:** Time O(amount × coins.length), Space O(amount).

### 107. Longest Increasing Subsequence
**Problem:** Given an integer array `nums`, return the length of the longest strictly increasing subsequence.

**Example:**
```
Input: nums = [10,9,2,5,3,7,101,18]
Output: 4
Explanation: [2,3,7,101]
```
```java
public int lengthOfLIS(int[] nums) {
    List<Integer> tails = new ArrayList<>(); // tails.get(i) = smallest tail of an increasing subsequence of length i+1
    for (int num : nums) {
        int idx = Collections.binarySearch(tails, num);
        if (idx < 0) idx = -(idx + 1);
        if (idx == tails.size()) tails.add(num);
        else tails.set(idx, num);
    }
    return tails.size();
}
```
**Approach:** Patience sorting — maintain the smallest possible tail value for every achievable subsequence length; binary search finds where to place/replace each new number in O(log n).
**Tricks/Pitfalls:** The O(n²) DP version (`dp[i] = max(dp[j]+1)` for all `j < i` with `nums[j] < nums[i]`) is simpler to derive live but the O(n log n) patience-sorting version is what senior interviews expect as a follow-up optimization.
**Complexity:** Time O(n log n), Space O(n). (Naive DP: O(n²), O(n).)

### 108. Longest Common Subsequence
**Problem:** Given two strings `text1` and `text2`, return the length of their longest common subsequence (not necessarily contiguous), or 0 if none exists.

**Example:**
```
Input: text1 = "abcde", text2 = "ace"
Output: 3
Explanation: "ace" is the longest common subsequence.
```
```java
public int longestCommonSubsequence(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++)
            dp[i][j] = (text1.charAt(i - 1) == text2.charAt(j - 1))
                ? dp[i - 1][j - 1] + 1
                : Math.max(dp[i - 1][j], dp[i][j - 1]);
    return dp[m][n];
}
```
**Approach:** Classic 2D DP — if characters match, extend the diagonal subsequence; otherwise take the best of dropping one character from either string.
**Tricks/Pitfalls:** The 1-indexed `dp` array (size `(m+1)×(n+1)`) elegantly avoids negative-index checks for the empty-string base case.
**Complexity:** Time O(m·n), Space O(m·n) (reducible to O(min(m,n)) with row compression).

### 109. Word Break
**Problem:** Given a string `s` and a dictionary of strings `wordDict`, return true if `s` can be segmented into a space-separated sequence of one or more dictionary words.

**Example:**
```
Input: s = "leetcode", wordDict = ["leet","code"]
Output: true
```
```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    for (int i = 1; i <= s.length(); i++)
        for (int j = 0; j < i; j++)
            if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
    return dp[s.length()];
}
```
**Approach:** `dp[i]` = true if `s[0..i)` can be segmented; check every split point `j` where the prefix up to `j` is breakable and the remainder `[j, i)` is a dictionary word.
**Tricks/Pitfalls:** Naive recursion without memoization re-solves the same suffixes repeatedly — exponential blowup; the `dp` array eliminates that redundancy.
**Complexity:** Time O(n²) (substring extraction included), Space O(n).

### 110. Partition Equal Subset Sum
**Problem:** Given a non-empty array of positive integers, determine if it can be partitioned into two subsets with equal sum.

**Example:**
```
Input: nums = [1,5,11,5]
Output: true
Explanation: [1,5,5] and [11] both sum to 11.
```
```java
public boolean canPartition(int[] nums) {
    int sum = Arrays.stream(nums).sum();
    if (sum % 2 != 0) return false;
    int target = sum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;
    for (int num : nums)
        for (int j = target; j >= num; j--)      // iterate backward: 0/1 knapsack, one use per item
            dp[j] = dp[j] || dp[j - num];
    return dp[target];
}
```
**Approach:** Reduces to a 0/1 knapsack — can we pick a subset summing to exactly `target = sum/2`?
**Tricks/Pitfalls:** Iterating the inner loop **backward** (`j` from `target` down to `num`) is essential — this is what prevents using the same element twice within one pass (forward iteration would turn this into unbounded knapsack, giving wrong answers).
**Complexity:** Time O(n × target), Space O(target).

### 111. Unique Paths
**Problem:** A robot is at the top-left of an `m x n` grid and can only move right or down. Count the number of unique paths to the bottom-right corner.

**Example:**
```
Input: m = 3, n = 7
Output: 28
```
```java
public int uniquePaths(int m, int n) {
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            dp[j] += dp[j - 1];
    return dp[n - 1];
}
```
**Approach:** `paths[i][j] = paths[i-1][j] + paths[i][j-1]`; a rolling 1D array (row-compressed) suffices since each row only depends on the row above and the running left value.
**Tricks/Pitfalls:** This is combinatorially equivalent to `C(m+n-2, m-1)` — closed-form math also solves it in O(1) if the interviewer wants that follow-up.
**Complexity:** Time O(m·n), Space O(n).

### 112. Minimum Path Sum
**Problem:** Given an `m x n` grid filled with non-negative numbers, find a path from top-left to bottom-right (moving only right or down) that minimizes the sum of numbers along the path.

**Example:**
```
Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
Output: 7
Explanation: Path 1 -> 3 -> 1 -> 1 -> 1 = 7
```
```java
public int minPathSum(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    int[][] dp = new int[rows][cols];
    dp[0][0] = grid[0][0];
    for (int j = 1; j < cols; j++) dp[0][j] = dp[0][j - 1] + grid[0][j];
    for (int i = 1; i < rows; i++) dp[i][0] = dp[i - 1][0] + grid[i][0];
    for (int i = 1; i < rows; i++)
        for (int j = 1; j < cols; j++)
            dp[i][j] = grid[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
    return dp[rows - 1][cols - 1];
}
```
**Approach:** Same movement-restricted grid DP pattern as Unique Paths, but minimizing a sum instead of counting paths.
**Tricks/Pitfalls:** In-place modification of the input grid (using it as the `dp` array directly) saves O(m·n) space if mutating the input is acceptable.
**Complexity:** Time O(m·n), Space O(m·n) (O(n) with row compression, O(1) if modifying input in place).

### 113. Edit Distance
**Problem:** Given two strings `word1` and `word2`, find the minimum number of insert/delete/replace operations to convert `word1` into `word2`.

**Example:**
```
Input: word1 = "horse", word2 = "ros"
Output: 3
Explanation: horse -> rorse (replace h with r) -> rose (remove r) -> ros (remove e)
```
```java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++)
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
            else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
        }
    return dp[m][n];
}
```
**Approach:** Classic Levenshtein distance DP — matching chars carry the diagonal value forward; a mismatch costs 1 plus the best of replace/delete/insert (diagonal/up/left).
**Tricks/Pitfalls:** The base rows/columns (`dp[i][0] = i`, `dp[0][j] = j`) represent converting to/from an empty string via pure insertions/deletions — easy to forget when initializing.
**Complexity:** Time O(m·n), Space O(m·n).

### 114. Decode Ways
**Problem:** A message of digits can be decoded (A=1, B=2, ..., Z=26). Given a digit string `s`, return the number of ways to decode it.

**Example:**
```
Input: s = "226"
Output: 3
Explanation: "2 2 6", "22 6", "2 26" -> BBF, VF, BZ
```
```java
public int numDecodings(String s) {
    if (s.charAt(0) == '0') return 0;
    int n = s.length();
    int prev2 = 1, prev1 = 1; // dp[0]=1 (empty string), dp[1]=1
    for (int i = 2; i <= n; i++) {
        int curr = 0;
        if (s.charAt(i - 1) != '0') curr += prev1;              // single digit valid
        int twoDigit = Integer.parseInt(s.substring(i - 2, i));
        if (twoDigit >= 10 && twoDigit <= 26) curr += prev2;     // two digit valid
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
```
**Approach:** `dp[i]` = ways to decode the first i characters; each position can extend by taking one digit (if nonzero) or two digits (if in range 10-26).
**Tricks/Pitfalls:** A leading `'0'` anywhere that can't be paired with the previous digit as a valid 2-digit code (10 or 20) makes that path invalid — a very common source of off-by-one/logic bugs in this problem.
**Complexity:** Time O(n), Space O(1).

### 115. Best Time to Buy and Sell Stock with Cooldown
**Problem:** Given stock prices, maximize profit with as many transactions as you like, but after selling you must wait one day before buying again (cooldown).

**Example:**
```
Input: prices = [1,2,3,0,2]
Output: 3
Explanation: buy, sell, cooldown, buy, sell = [buy(1), sell(2), cooldown, buy(0), sell(2)] profit = 1+2=3
```
```java
public int maxProfit(int[] prices) {
    int sold = 0, held = Integer.MIN_VALUE, rest = 0;
    for (int price : prices) {
        int prevSold = sold;
        sold = held + price;                       // sell today
        held = Math.max(held, rest - price);        // buy today or keep holding
        rest = Math.max(rest, prevSold);             // rest today (or was already resting)
    }
    return Math.max(sold, rest);
}
```
**Approach:** State-machine DP with three states per day — `sold` (just sold), `held` (holding a stock), `rest` (cooling down or idle) — transition rules encode the cooldown constraint.
**Tricks/Pitfalls:** Must snapshot `prevSold` before updating `sold`, since `rest`'s transition depends on the *previous* day's `sold` state, not the newly computed one.
**Complexity:** Time O(n), Space O(1).

### 116. Longest Palindromic Subsequence
**Problem:** Given a string `s`, find the length of the longest palindromic subsequence (not necessarily contiguous).

**Example:**
```
Input: s = "bbbab"
Output: 4
Explanation: "bbbb" is the longest palindromic subsequence.
```
```java
public int longestPalindromeSubseq(String s) {
    int n = s.length();
    int[][] dp = new int[n][n];
    for (int i = n - 1; i >= 0; i--) {
        dp[i][i] = 1;
        for (int j = i + 1; j < n; j++) {
            dp[i][j] = (s.charAt(i) == s.charAt(j))
                ? dp[i + 1][j - 1] + 2
                : Math.max(dp[i + 1][j], dp[i][j - 1]);
        }
    }
    return dp[0][n - 1];
}
```
**Approach:** Interval DP — `dp[i][j]` = LPS length within substring `[i, j]`; if the endpoints match, they can both be part of the palindrome (+2 plus the inner interval); otherwise drop one end.
**Tricks/Pitfalls:** Iterating `i` from `n-1` down to `0` (and `j` from `i+1` upward) ensures `dp[i+1][j-1]` is already computed when needed — the iteration direction is the crux of getting interval DP right.
**Complexity:** Time O(n²), Space O(n²).

### 117. Target Sum
**Problem:** Given an integer array `nums` and integer `target`, count the number of ways to assign `+` or `-` to each element so the sum equals `target`.

**Example:**
```
Input: nums = [1,1,1,1,1], target = 3
Output: 5
```
```java
public int findTargetSumWays(int[] nums, int target) {
    int sum = Arrays.stream(nums).sum();
    if (Math.abs(target) > sum || (sum + target) % 2 != 0) return 0;
    int s = (sum + target) / 2; // reduces to: count subsets summing to s
    int[] dp = new int[s + 1];
    dp[0] = 1;
    for (int num : nums)
        for (int j = s; j >= num; j--)
            dp[j] += dp[j - num];
    return dp[s];
}
```
**Approach:** Mathematical reduction — if P = sum of positives, N = sum of negatives, P - N = target and P + N = totalSum, solving gives P = (totalSum + target)/2, turning this into "count subsets summing to P" (0/1 knapsack counting variant).
**Tricks/Pitfalls:** The algebraic reduction to a subset-count knapsack is the key insight — without it, this looks like an exponential brute-force sign-assignment problem.
**Complexity:** Time O(n × s), Space O(s).

### 118. Jump Game
**Problem:** Given an array where `nums[i]` is the max jump length from index `i`, determine if you can reach the last index starting from index 0.

**Example:**
```
Input: nums = [2,3,1,1,4]
Output: true

Input: nums = [3,2,1,0,4]
Output: false
```
```java
public boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false; // stuck before reaching here
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    return true;
}
```
**Approach:** Greedily track the farthest index reachable so far; if the current index ever exceeds that reach, it's unreachable.
**Tricks/Pitfalls:** This looks like it needs DP/backtracking, but a single greedy pass suffices — recognizing the greedy simplification is the key interview signal.
**Complexity:** Time O(n), Space O(1).

### 119. Jump Game II (Minimum Jumps)
**Problem:** Given the same setup as Jump Game, return the minimum number of jumps to reach the last index (guaranteed reachable).

**Example:**
```
Input: nums = [2,3,1,1,4]
Output: 2
Explanation: Jump from index 0 to 1, then to the last index.
```
```java
public int jump(int[] nums) {
    int jumps = 0, currEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currEnd) {           // must jump now to progress
            jumps++;
            currEnd = farthest;
        }
    }
    return jumps;
}
```
**Approach:** Greedy BFS-like "layer" expansion — track the farthest reachable point within the current jump's range; when the current index hits the boundary of that range, a jump is forced.
**Tricks/Pitfalls:** The loop bound `nums.length - 1` avoids counting an unnecessary final jump when already at the last index.
**Complexity:** Time O(n), Space O(1).

### 120. 0/1 Knapsack (Classic Formulation)
**Problem:** Given item weights and values and a knapsack capacity, maximize total value without exceeding capacity (each item used at most once).

**Example:**
```
Input: weights = [1,3,4,5], values = [1,4,5,7], capacity = 7
Output: 9
Explanation: Take items with weight 3 and 4 (value 4+5=9).
```
```java
public int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < n; i++)
        for (int w = capacity; w >= weights[i]; w--)    // backward: 0/1, not unbounded
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
    return dp[capacity];
}
```
**Approach:** Space-optimized 1D DP; `dp[w]` = best value achievable with capacity `w`, updated backward per item to enforce single-use.
**Tricks/Pitfalls:** This is the template problem underlying Partition Equal Subset Sum, Target Sum, and many "can we select items to hit X" problems — recognizing the pattern reuse across problems is a strong interview signal.
**Complexity:** Time O(n × capacity), Space O(capacity).

### 121. Maximal Square
**Problem:** Given an `m x n` binary matrix, find the largest square containing only 1s and return its area.

**Example:**
```
Input: matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
Output: 4
```
```java
public int maximalSquare(char[][] matrix) {
    int rows = matrix.length, cols = matrix[0].length, maxSide = 0;
    int[][] dp = new int[rows + 1][cols + 1];
    for (int i = 1; i <= rows; i++)
        for (int j = 1; j <= cols; j++)
            if (matrix[i - 1][j - 1] == '1') {
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                maxSide = Math.max(maxSide, dp[i][j]);
            }
    return maxSide * maxSide;
}
```
**Approach:** `dp[i][j]` = side length of the largest square with bottom-right corner at (i,j); it's limited by the minimum of the three neighboring squares (top, left, diagonal) plus one.
**Tricks/Pitfalls:** Taking the **minimum** of the three neighbors (not the max) is essential — a square's side is only as large as its most constrained neighboring square.
**Complexity:** Time O(m·n), Space O(m·n) (O(n) with row compression).

### 122. Burst Balloons (Interval DP)
**Problem:** Given `n` balloons with numbers on them, bursting balloon `i` gives `nums[left] * nums[i] * nums[right]` coins (left/right are current neighbors). Find the maximum coins from bursting all balloons.

**Example:**
```
Input: nums = [3,1,5,8]
Output: 167
```
```java
public int maxCoins(int[] nums) {
    int n = nums.length;
    int[] balloons = new int[n + 2];
    balloons[0] = balloons[n + 1] = 1;
    for (int i = 0; i < n; i++) balloons[i + 1] = nums[i];
    int[][] dp = new int[n + 2][n + 2];
    for (int len = 1; len <= n; len++) {
        for (int left = 1; left <= n - len + 1; left++) {
            int right = left + len - 1;
            for (int k = left; k <= right; k++) {
                dp[left][right] = Math.max(dp[left][right],
                    dp[left][k - 1] + balloons[left - 1] * balloons[k] * balloons[right + 1] + dp[k + 1][right]);
            }
        }
    }
    return dp[1][n];
}
```
**Approach:** Think in reverse — instead of "which balloon to burst first," decide "which balloon to burst **last**" within each interval `[left, right]`; that balloon's neighbors at burst time are guaranteed to be the interval's original boundaries.
**Tricks/Pitfalls:** The "burst last" reframing is the single hardest insight in this problem — bursting first makes the neighbor values unpredictable, while bursting last within a fixed interval keeps boundary balloons stable and known.
**Complexity:** Time O(n³), Space O(n²).


## Category 8: Greedy

### 123. Gas Station
**Problem:** There are `n` gas stations along a circular route. Given `gas[i]` (gas available at station i) and `cost[i]` (cost to travel to next station), return the starting station index to complete the circuit, or -1 if impossible.

**Example:**
```
Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
Output: 3
```
```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        total += gas[i] - cost[i];
        tank += gas[i] - cost[i];
        if (tank < 0) { start = i + 1; tank = 0; } // can't start at or before i
    }
    return total < 0 ? -1 : start;
}
```
**Approach:** If the tank goes negative at index i, no station from the current `start` through `i` can be a valid start either — reset the candidate start to `i+1`.
**Tricks/Pitfalls:** This greedy works because of a proof: if a solution exists, it's unique, and the first index where you'd "fail" rules out all earlier candidates simultaneously — no need to brute-force every starting point (which would be O(n²)).
**Complexity:** Time O(n), Space O(1).

### 124. Candy
**Problem:** `n` children stand in a line, each with a rating. Distribute the minimum candies such that each child gets at least 1, and children with a higher rating than a neighbor get more candy than that neighbor.

**Example:**
```
Input: ratings = [1,0,2]
Output: 5
Explanation: candies = [2,1,2]
```
```java
public int candy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);
    for (int i = 1; i < n; i++) if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1; // left pass
    for (int i = n - 2; i >= 0; i--) if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1); // right pass
    return Arrays.stream(candies).sum();
}
```
**Approach:** Two greedy passes — left-to-right enforces the "greater than left neighbor" rule, right-to-left enforces "greater than right neighbor," taking the max of both constraints per child.
**Tricks/Pitfalls:** Using `Math.max` (not overwrite) in the second pass is critical — it must never violate the already-satisfied left-pass constraint.
**Complexity:** Time O(n), Space O(n).

### 125. Task Scheduler
**Problem:** Given a list of tasks (characters) and a cooldown `n` between two identical tasks, find the minimum time (including idle slots) to execute all tasks.

**Example:**
```
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: A -> B -> idle -> A -> B -> idle -> A -> B
```
```java
public int leastInterval(char[] tasks, int n) {
    int[] counts = new int[26];
    for (char t : tasks) counts[t - 'A']++;
    Arrays.sort(counts);
    int maxCount = counts[25];
    int idleSlots = (maxCount - 1) * n;
    for (int i = 24; i >= 0 && counts[i] > 0; i--) idleSlots -= Math.min(counts[i], maxCount - 1);
    idleSlots = Math.max(idleSlots, 0);
    return tasks.length + idleSlots;
}
```
**Approach:** The most frequent task dictates a minimum number of "cooldown slots"; fill those slots with other tasks first, and any leftover unfilled slots become forced idle time.
**Tricks/Pitfalls:** Capping each other task's contribution at `maxCount - 1` (it can only fill one slot per cycle of the most frequent task) is the subtle correctness detail.
**Complexity:** Time O(n) (26-bucket sort is O(1)), Space O(1).

### 126. Partition Labels
**Problem:** A string `s` is partitioned into as many parts as possible so that each letter appears in at most one part. Return a list of the sizes of these parts.

**Example:**
```
Input: s = "ababcbacadefegdehijhklij"
Output: [9,7,8]
```
```java
public List<Integer> partitionLabels(String s) {
    int[] lastIndex = new int[26];
    for (int i = 0; i < s.length(); i++) lastIndex[s.charAt(i) - 'a'] = i;
    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
        if (i == end) { result.add(end - start + 1); start = i + 1; }
    }
    return result;
}
```
**Approach:** Precompute each character's last occurrence; greedily extend the current partition's boundary to cover the last occurrence of every character seen so far, closing the partition once the current index catches up to that boundary.
**Tricks/Pitfalls:** This is structurally identical to the "merge intervals" pattern — each character defines an implicit interval [first, last occurrence].
**Complexity:** Time O(n), Space O(1) (fixed 26-size array).

### 127. Minimum Number of Arrows to Burst Balloons
**Problem:** Given balloons represented as intervals `[xstart, xend]`, find the minimum number of arrows (shot straight up) needed to burst all balloons.

**Example:**
```
Input: points = [[10,16],[2,8],[1,6],[7,12]]
Output: 2
```
```java
public int findMinArrowShots(int[][] points) {
    Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1])); // sort by end
    int arrows = 1;
    int end = points[0][1];
    for (int[] p : points) {
        if (p[0] > end) { arrows++; end = p[1]; } // needs a new arrow
    }
    return arrows;
}
```
**Approach:** Same greedy pattern as Non-overlapping Intervals — sort by end coordinate, and an arrow covers every balloon whose start is ≤ the current arrow's position.
**Tricks/Pitfalls:** Recognizing that "burst overlapping balloons with one arrow" is the same greedy shape as classic interval scheduling saves derivation time.
**Complexity:** Time O(n log n), Space O(1).

### 128. Boats to Save People
**Problem:** Given people's weights and a weight `limit` per boat (max 2 people per boat), find the minimum number of boats to carry everyone.

**Example:**
```
Input: people = [3,2,2,1], limit = 3
Output: 3
Explanation: (1,2), (2), (3)
```
```java
public int numRescueBoats(int[] people, int limit) {
    Arrays.sort(people);
    int lo = 0, hi = people.length - 1, boats = 0;
    while (lo <= hi) {
        if (people[lo] + people[hi] <= limit) lo++; // pair lightest with heaviest
        hi--;
        boats++;
    }
    return boats;
}
```
**Approach:** Two pointers on sorted weights — always try to pair the heaviest remaining person with the lightest; if they don't fit together, the heaviest goes alone.
**Tricks/Pitfalls:** The heaviest person always needs a boat regardless (hence `hi--` unconditionally); the greedy question is only whether the lightest can *join* them.
**Complexity:** Time O(n log n), Space O(1).

### 129. Lemonade Change
**Problem:** Each lemonade costs $5. Customers pay with $5, $10, or $20 bills. Determine if you can provide correct change to every customer, starting with no change on hand.

**Example:**
```
Input: bills = [5,5,5,10,20]
Output: true
```
```java
public boolean lemonadeChange(int[] bills) {
    int five = 0, ten = 0;
    for (int bill : bills) {
        if (bill == 5) five++;
        else if (bill == 10) { if (five == 0) return false; five--; ten++; }
        else { // bill == 20
            if (ten > 0 && five > 0) { ten--; five--; }
            else if (five >= 3) five -= 3;
            else return false;
        }
    }
    return true;
}
```
**Approach:** Greedily prefer giving a $10 + $5 as change for a $20 (preserving more $5 bills for future flexibility) over three $5 bills.
**Tricks/Pitfalls:** The preference order (try $10+$5 before three $5s) matters — three $5s works arithmetically but wastes flexibility needed for later $10 payments.
**Complexity:** Time O(n), Space O(1).

### 130. Assign Cookies
**Problem:** Each child has a greed factor `g[i]`, each cookie has a size `s[j]`. A child is content if given a cookie of size ≥ their greed factor. Maximize the number of content children.

**Example:**
```
Input: g = [1,2,3], s = [1,1]
Output: 1
```
```java
public int findContentChildren(int[] g, int[] s) {
    Arrays.sort(g); Arrays.sort(s);
    int child = 0, cookie = 0;
    while (child < g.length && cookie < s.length) {
        if (s[cookie] >= g[child]) child++;
        cookie++;
    }
    return child;
}
```
**Approach:** Sort both arrays; greedily try to satisfy the least-greedy child with the smallest sufficient cookie.
**Tricks/Pitfalls:** Always advance the cookie pointer regardless of a match — a cookie too small for the current child is also too small for any less-greedy remaining child (they've already been satisfied) and irrelevant for greedier ones.
**Complexity:** Time O(n log n + m log m), Space O(1).

### 131. Queue Reconstruction by Height
**Problem:** Given people as `[height, k]` pairs where `k` is the number of people in front with height ≥ this person's height, reconstruct the queue.

**Example:**
```
Input: people = [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]]
Output: [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]
```
```java
public int[][] reconstructQueue(int[][] people) {
    Arrays.sort(people, (a, b) -> a[0] != b[0] ? b[0] - a[0] : a[1] - b[1]); // tallest first, then smallest k
    List<int[]> result = new ArrayList<>();
    for (int[] p : people) result.add(p[1], p);   // insert at index k
    return result.toArray(new int[0][]);
}
```
**Approach:** Process tallest-first; since everyone already placed is at least as tall, inserting the current person at index `k` automatically satisfies their "k taller/equal people in front" constraint, and insertion never disturbs already-correct relative positions.
**Tricks/Pitfalls:** Processing shortest-first would make `k` meaningless (you wouldn't know how many "taller" people will eventually be inserted later) — the tallest-first order is what makes greedy insertion valid.
**Complexity:** Time O(n²) (due to `List.add(index, ...)` shifting), Space O(n).

### 132. Meeting Rooms II
**Problem:** Given an array of meeting time intervals, find the minimum number of conference rooms required.

**Example:**
```
Input: intervals = [[0,30],[5,10],[15,20]]
Output: 2
```
```java
public int minMeetingRooms(int[][] intervals) {
    int[] starts = new int[intervals.length], ends = new int[intervals.length];
    for (int i = 0; i < intervals.length; i++) { starts[i] = intervals[i][0]; ends[i] = intervals[i][1]; }
    Arrays.sort(starts); Arrays.sort(ends);
    int rooms = 0, maxRooms = 0, endPtr = 0;
    for (int start : starts) {
        while (endPtr < ends.length && ends[endPtr] <= start) { rooms--; endPtr++; }
        rooms++;
        maxRooms = Math.max(maxRooms, rooms);
    }
    return maxRooms;
}
```
**Approach:** Chronological sweep — sort starts and ends separately; as each meeting starts, first free up rooms from meetings that have already ended, then occupy one more room.
**Tricks/Pitfalls:** Sorting starts/ends *independently* (not as paired intervals) is the trick that makes a simple two-pointer sweep work in O(n log n) instead of needing a more complex heap-based simulation (though a min-heap of end times is an equally valid alternative approach).
**Complexity:** Time O(n log n), Space O(n).

### 133. Minimum Platforms (Train Scheduling)
**Problem:** Given arrival and departure times of trains at a station, find the minimum number of platforms needed so that no train waits.

**Example:**
```
Input: arrivals = [900, 940, 950, 1100, 1500, 1800], departures = [910, 1200, 1120, 1130, 1900, 2000]
Output: 3
```
```java
public int findMinPlatforms(int[] arrivals, int[] departures) {
    Arrays.sort(arrivals); Arrays.sort(departures);
    int platforms = 0, maxPlatforms = 0, i = 0, j = 0;
    while (i < arrivals.length && j < departures.length) {
        if (arrivals[i] <= departures[j]) { platforms++; i++; maxPlatforms = Math.max(maxPlatforms, platforms); }
        else { platforms--; j++; }
    }
    return maxPlatforms;
}
```
**Approach:** Identical sweep-line pattern to Meeting Rooms II — this is the same underlying problem framed with railway terminology, a very common real-world/interview crossover.
**Tricks/Pitfalls:** Recognize this as a "same pattern, different domain" problem — a strong signal of pattern-matching maturity in an interview.
**Complexity:** Time O(n log n), Space O(1) excluding sort.


## Category 9: Backtracking

### 134. Subsets
**Problem:** Given an integer array `nums` of unique elements, return all possible subsets (the power set).

**Example:**
```
Input: nums = [1,2,3]
Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
```
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> result) {
    result.add(new ArrayList<>(path)); // every path is a valid subset
    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);
        backtrack(nums, i + 1, path, result);
        path.remove(path.size() - 1); // undo choice
    }
}
```
**Approach:** At each recursion level, add the current path as a valid subset, then try extending it with every remaining element.
**Tricks/Pitfalls:** Adding a copy (`new ArrayList<>(path)`) at every node, not just leaves, is what captures all 2ⁿ subsets (including the empty set and non-maximal ones).
**Complexity:** Time O(n · 2ⁿ), Space O(n) recursion depth.

### 135. Subsets II (With Duplicates)
**Problem:** Given an integer array `nums` that may contain duplicates, return all possible unique subsets.

**Example:**
```
Input: nums = [1,2,2]
Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]
```
```java
public List<List<Integer>> subsetsWithDup(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> result) {
    result.add(new ArrayList<>(path));
    for (int i = start; i < nums.length; i++) {
        if (i > start && nums[i] == nums[i - 1]) continue; // skip duplicate at this recursion level
        path.add(nums[i]);
        backtrack(nums, i + 1, path, result);
        path.remove(path.size() - 1);
    }
}
```
**Approach:** Sort first so duplicates are adjacent, then skip a duplicate value if it's not the *first* choice at the current recursion level (`i > start` check).
**Tricks/Pitfalls:** The `i > start` (not `i > 0`) condition is crucial — it allows duplicates *across* different recursion levels (e.g., [1,2,2] needs both 2s eventually) while preventing duplicates *within* the same level's choices.
**Complexity:** Time O(n · 2ⁿ), Space O(n).

### 136. Permutations
**Problem:** Given an array `nums` of distinct integers, return all possible permutations.

**Example:**
```
Input: nums = [1,2,3]
Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}
private void backtrack(int[] nums, List<Integer> path, boolean[] used, List<List<Integer>> result) {
    if (path.size() == nums.length) { result.add(new ArrayList<>(path)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, path, used, result);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```
**Approach:** Unlike Subsets, every position considers *all* unused elements (not just those after a `start` index), since order matters for permutations.
**Tricks/Pitfalls:** A `boolean[] used` array is cleaner than repeatedly checking `path.contains(nums[i])` (which is O(n) per check, making the whole algorithm slower).
**Complexity:** Time O(n · n!), Space O(n).

### 137. Permutations II (With Duplicates)
**Problem:** Given a collection of numbers that might contain duplicates, return all possible unique permutations.

**Example:**
```
Input: nums = [1,1,2]
Output: [[1,1,2],[1,2,1],[2,1,1]]
```
```java
public List<List<Integer>> permuteUnique(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}
private void backtrack(int[] nums, List<Integer> path, boolean[] used, List<List<Integer>> result) {
    if (path.size() == nums.length) { result.add(new ArrayList<>(path)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue; // skip duplicate branch
        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, path, used, result);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```
**Approach:** Sort first; the `!used[i-1]` condition ensures duplicates are only used in a fixed left-to-right order within each recursive branch, eliminating duplicate permutations.
**Tricks/Pitfalls:** This `!used[i-1]` duplicate-skip condition (as opposed to `used[i-1]`) is one of the most frequently mixed-up details in backtracking interviews — worth memorizing the reasoning, not just the code.
**Complexity:** Time O(n · n!), Space O(n).

### 138. Combination Sum
**Problem:** Given an array of distinct positive integers `candidates` and a target, return all unique combinations where numbers sum to target (each candidate may be used unlimited times).

**Example:**
```
Input: candidates = [2,3,6,7], target = 7
Output: [[2,2,3],[7]]
```
```java
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(candidates);
    backtrack(candidates, target, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(int[] candidates, int remaining, int start, List<Integer> path, List<List<Integer>> result) {
    if (remaining == 0) { result.add(new ArrayList<>(path)); return; }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > remaining) break; // sorted -> prune remaining larger candidates
        path.add(candidates[i]);
        backtrack(candidates, remaining - candidates[i], i, path, result); // i, not i+1: reuse allowed
        path.remove(path.size() - 1);
    }
}
```
**Approach:** Backtracking with reuse — pass `i` (not `i+1`) to the recursive call, allowing the same candidate to be chosen again.
**Tricks/Pitfalls:** Sorting first enables the `break` pruning optimization (`candidates[i] > remaining`), which meaningfully cuts the search space versus checking every candidate blindly.
**Complexity:** Time O(2ᵗ) worst case (t = target/min candidate), Space O(target/min candidate) recursion depth.

### 139. Combination Sum II (No Reuse, Has Duplicates)
**Problem:** Given a collection of candidate numbers (may contain duplicates) and a target, return all unique combinations summing to target, each candidate used at most once.

**Example:**
```
Input: candidates = [10,1,2,7,6,1,5], target = 8
Output: [[1,1,6],[1,2,5],[1,7],[2,6]]
```
```java
public List<List<Integer>> combinationSum2(int[] candidates, int target) {
    Arrays.sort(candidates);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(candidates, target, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(int[] candidates, int remaining, int start, List<Integer> path, List<List<Integer>> result) {
    if (remaining == 0) { result.add(new ArrayList<>(path)); return; }
    for (int i = start; i < candidates.length; i++) {
        if (i > start && candidates[i] == candidates[i - 1]) continue; // skip duplicate at this level
        if (candidates[i] > remaining) break;
        path.add(candidates[i]);
        backtrack(candidates, remaining - candidates[i], i + 1, path, result); // i+1: no reuse
        path.remove(path.size() - 1);
    }
}
```
**Approach:** Combines the "no reuse" pattern (`i+1` in recursive call) from Combination Sum with the "skip duplicate at this level" pattern (`i > start` check) from Subsets II.
**Tricks/Pitfalls:** This problem is a great test of whether you can *compose* two patterns you already know rather than needing an entirely new algorithm.
**Complexity:** Time O(2ⁿ), Space O(n).

### 140. Generate Parentheses
**Problem:** Given `n` pairs of parentheses, generate all combinations of well-formed parentheses.

**Example:**
```
Input: n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]
```
```java
public List<String> generateParenthesis(int n) {
    List<String> result = new ArrayList<>();
    backtrack(new StringBuilder(), 0, 0, n, result);
    return result;
}
private void backtrack(StringBuilder sb, int open, int close, int n, List<String> result) {
    if (sb.length() == 2 * n) { result.add(sb.toString()); return; }
    if (open < n) { sb.append('('); backtrack(sb, open + 1, close, n, result); sb.deleteCharAt(sb.length() - 1); }
    if (close < open) { sb.append(')'); backtrack(sb, open, close + 1, n, result); sb.deleteCharAt(sb.length() - 1); }
}
```
**Approach:** Only add `'('` if fewer than n opens used; only add `')'` if fewer closes than opens (ensures validity is maintained at every step, not just at the end).
**Tricks/Pitfalls:** Validating only at the leaf (generate all 2^(2n) strings, then filter) is a correct-but-hugely-wasteful naive approach — pruning invalid states early (`close < open`) is what makes this efficient.
**Complexity:** Time O(4ⁿ/√n) (nth Catalan number bound), Space O(n) recursion depth.

### 141. Palindrome Partitioning
**Problem:** Given a string `s`, partition it such that every substring of the partition is a palindrome. Return all possible partitions.

**Example:**
```
Input: s = "aab"
Output: [["a","a","b"],["aa","b"]]
```
```java
public List<List<String>> partition(String s) {
    List<List<String>> result = new ArrayList<>();
    backtrack(s, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(String s, int start, List<String> path, List<List<String>> result) {
    if (start == s.length()) { result.add(new ArrayList<>(path)); return; }
    for (int end = start + 1; end <= s.length(); end++) {
        if (isPalindrome(s, start, end - 1)) {
            path.add(s.substring(start, end));
            backtrack(s, end, path, result);
            path.remove(path.size() - 1);
        }
    }
}
private boolean isPalindrome(String s, int lo, int hi) {
    while (lo < hi) if (s.charAt(lo++) != s.charAt(hi--)) return false;
    return true;
}
```
**Approach:** At each position, try every possible next-substring length; only recurse further if that substring is itself a palindrome.
**Tricks/Pitfalls:** Precomputing a `boolean[][] isPalin` table via DP first avoids repeatedly re-checking overlapping substrings (turning the check from O(n) each into O(1) each) — a good optimization to mention for large inputs.
**Complexity:** Time O(n · 2ⁿ) worst case, Space O(n) recursion depth.

### 142. N-Queens
**Problem:** Place `n` queens on an `n x n` chessboard so that no two queens attack each other. Return all distinct board configurations.

**Example:**
```
Input: n = 4
Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
```
```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    int[] queens = new int[n]; // queens[row] = column
    backtrack(queens, 0, n, result);
    return result;
}
private void backtrack(int[] queens, int row, int n, List<List<String>> result) {
    if (row == n) { result.add(buildBoard(queens, n)); return; }
    for (int col = 0; col < n; col++) {
        if (isValid(queens, row, col)) {
            queens[row] = col;
            backtrack(queens, row + 1, n, result);
        }
    }
}
private boolean isValid(int[] queens, int row, int col) {
    for (int r = 0; r < row; r++) {
        int c = queens[r];
        if (c == col || Math.abs(c - col) == Math.abs(r - row)) return false; // same column or diagonal
    }
    return true;
}
private List<String> buildBoard(int[] queens, int n) {
    List<String> board = new ArrayList<>();
    for (int col : queens) {
        StringBuilder row = new StringBuilder();
        for (int j = 0; j < n; j++) row.append(j == col ? 'Q' : '.');
        board.add(row.toString());
    }
    return board;
}
```
**Approach:** Place one queen per row, checking column and diagonal conflicts against all previously placed queens before recursing to the next row.
**Tricks/Pitfalls:** Using a 1D `queens[row] = col` array (rather than a full 2D board) makes the `isValid` diagonal check a simple `Math.abs()` comparison and is far more memory-efficient.
**Complexity:** Time O(n!) worst case, Space O(n).

### 143. Sudoku Solver
**Problem:** Solve a 9x9 Sudoku puzzle by filling empty cells ('.') such that every row, column, and 3x3 sub-box contains digits 1-9 exactly once.

**Example:**
```
Input: board = [["5","3",".",".","7",".",".",".","."], ...(partially filled 9x9 board)]
Output: The fully solved 9x9 board (in place).
```
```java
public void solveSudoku(char[][] board) { solve(board); }
private boolean solve(char[][] board) {
    for (int i = 0; i < 9; i++)
        for (int j = 0; j < 9; j++)
            if (board[i][j] == '.') {
                for (char c = '1'; c <= '9'; c++) {
                    if (isValid(board, i, j, c)) {
                        board[i][j] = c;
                        if (solve(board)) return true; // found a full solution downstream
                        board[i][j] = '.';              // backtrack
                    }
                }
                return false; // no valid digit works here -> backtrack further up
            }
    return true; // no empty cells left -> solved
}
private boolean isValid(char[][] board, int row, int col, char c) {
    for (int i = 0; i < 9; i++) {
        if (board[row][i] == c) return false;
        if (board[i][col] == c) return false;
        if (board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == c) return false;
    }
    return true;
}
```
**Approach:** Classic constraint-satisfaction backtracking — try each digit 1-9 in the first empty cell, recurse, and backtrack on failure; returning `boolean` lets the recursion short-circuit as soon as any valid full solution is found.
**Tricks/Pitfalls:** The `if (solve(board)) return true;` pattern (propagating success upward immediately) avoids continuing to search for *additional* solutions once one is found — Sudoku only needs one valid completion.
**Complexity:** Time O(9^(empty cells)) worst case (heavily pruned in practice), Space O(1) beyond the input board.

### 144. Letter Combinations of a Phone Number
**Problem:** Given a string containing digits 2-9, return all possible letter combinations they could represent (phone keypad mapping).

**Example:**
```
Input: digits = "23"
Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
```
```java
public List<String> letterCombinations(String digits) {
    List<String> result = new ArrayList<>();
    if (digits.isEmpty()) return result;
    String[] mapping = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    backtrack(digits, 0, new StringBuilder(), mapping, result);
    return result;
}
private void backtrack(String digits, int idx, StringBuilder path, String[] mapping, List<String> result) {
    if (idx == digits.length()) { result.add(path.toString()); return; }
    String letters = mapping[digits.charAt(idx) - '0'];
    for (char c : letters.toCharArray()) {
        path.append(c);
        backtrack(digits, idx + 1, path, mapping, result);
        path.deleteCharAt(path.length() - 1);
    }
}
```
**Approach:** Standard Cartesian-product backtracking — at each digit position, try every letter it maps to and recurse to the next digit.
**Tricks/Pitfalls:** `StringBuilder` with append/deleteCharAt avoids the O(n) string-concatenation overhead of building/discarding new `String` objects at every recursive step.
**Complexity:** Time O(4ⁿ) worst case (digits 7 and 9 map to 4 letters), Space O(n) recursion depth.

### 145. Restore IP Addresses
**Problem:** Given a string containing only digits, return all possible valid IP address combinations that can be formed by inserting dots.

**Example:**
```
Input: s = "25525511135"
Output: ["255.255.11.135","255.255.111.35"]
```
```java
public List<String> restoreIpAddresses(String s) {
    List<String> result = new ArrayList<>();
    backtrack(s, 0, new ArrayList<>(), result);
    return result;
}
private void backtrack(String s, int start, List<String> segments, List<String> result) {
    if (segments.size() == 4) {
        if (start == s.length()) result.add(String.join(".", segments));
        return;
    }
    for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
        String segment = s.substring(start, start + len);
        if (isValidSegment(segment)) {
            segments.add(segment);
            backtrack(s, start + len, segments, result);
            segments.remove(segments.size() - 1);
        }
    }
}
private boolean isValidSegment(String segment) {
    if (segment.length() > 1 && segment.charAt(0) == '0') return false; // no leading zero (except "0" itself)
    int val = Integer.parseInt(segment);
    return val >= 0 && val <= 255;
}
```
**Approach:** Try every valid segment length (1-3 digits) at each position, validating range (0-255) and leading-zero rules before recursing to build the next octet.
**Tricks/Pitfalls:** The leading-zero check (`"01"` is invalid, but `"0"` alone is valid) is the most commonly missed edge case in this problem.
**Complexity:** Time O(1) practically (bounded to at most 3^4 = 81 candidate splits regardless of input length), Space O(1).


## Category 10: Advanced (Segment Tree, Trie, Union-Find, BIT)

### 146. Implement Trie (Prefix Tree)
**Problem:** Design a Trie supporting `insert(word)`, `search(word)` (exact match), and `startsWith(prefix)`.

**Example:**
```
Input: insert("apple"); search("apple") -> true; search("app") -> false; startsWith("app") -> true
Output: [true, false, true]
```
```java
class Trie {
    private final TrieNode root = new TrieNode();
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'] == null ? (node.children[c - 'a'] = new TrieNode()) : node.children[c - 'a'];
        }
        node.isEnd = true;
    }
    public boolean search(String word) { TrieNode n = find(word); return n != null && n.isEnd; }
    public boolean startsWith(String prefix) { return find(prefix) != null; }
    private TrieNode find(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) return null;
        }
        return node;
    }
}
```
**Approach:** Each node holds a fixed 26-slot array for lowercase letters; `isEnd` marks complete words distinctly from mere prefixes.
**Tricks/Pitfalls:** Distinguishing `search` (must end exactly at a word) from `startsWith` (just needs the path to exist) is the core design nuance interviewers probe.
**Complexity:** Time O(L) per operation (L = word/prefix length), Space O(total characters × 26) worst case.

### 147. Word Search II (Trie + Backtracking)
**Problem:** Given an `m x n` board of characters and a list of words, return all words present in the board (built from adjacent cells, no cell reused within a word).

**Example:**
```
Input: board = [["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]], words = ["oath","pea","eat","rain"]
Output: ["eat","oath"]
```
```java
public List<String> findWords(char[][] board, String[] words) {
    Trie trie = new Trie();
    for (String w : words) trie.insert(w);
    List<String> result = new ArrayList<>();
    for (int i = 0; i < board.length; i++)
        for (int j = 0; j < board[0].length; j++)
            dfs(board, i, j, trie.root, new StringBuilder(), result);
    return result;
}
private void dfs(char[][] board, int i, int j, Trie.TrieNode node, StringBuilder path, List<String> result) {
    if (i < 0 || j < 0 || i >= board.length || j >= board[0].length || board[i][j] == '#') return;
    char c = board[i][j];
    TrieNode next = node.children[c - 'a'];
    if (next == null) return; // prune: no word in the Trie continues with this prefix
    path.append(c);
    if (next.isEnd) { result.add(path.toString()); next.isEnd = false; } // avoid duplicate results
    board[i][j] = '#'; // mark visited
    dfs(board, i + 1, j, next, path, result); dfs(board, i - 1, j, next, path, result);
    dfs(board, i, j + 1, next, path, result); dfs(board, i, j - 1, next, path, result);
    board[i][j] = c; // backtrack
    path.deleteCharAt(path.length() - 1);
}
```
**Approach:** Build a single Trie from all target words, then DFS from every board cell, following the Trie in lockstep — this shares the search across all words simultaneously instead of running Word Search once per word (which would be O(words × cells × 4^L)).
**Tricks/Pitfalls:** Setting `next.isEnd = false` after finding a word prevents adding the same word twice if it's reachable via multiple paths; pruning immediately when `next == null` is what makes the Trie approach dramatically faster than brute-force per-word search.
**Complexity:** Time O(m·n·4^L) bounded by Trie pruning in practice, Space O(sum of word lengths) for the Trie.

### 148. Range Sum Query — Mutable (Segment Tree)
**Problem:** Design a class supporting point updates and range sum queries on an array, both efficiently (not O(n) each).

**Example:**
```
Input: nums = [1,3,5]; sumRange(0,2) -> 9; update(1,2); sumRange(0,2) -> 8
Output: [9, 8]
```
```java
class NumArray {
    int[] tree, n_arr;
    int n;
    public NumArray(int[] nums) {
        n = nums.length;
        n_arr = nums;
        tree = new int[4 * n];
        build(1, 0, n - 1);
    }
    private void build(int node, int lo, int hi) {
        if (lo == hi) { tree[node] = n_arr[lo]; return; }
        int mid = (lo + hi) / 2;
        build(2 * node, lo, mid); build(2 * node + 1, mid + 1, hi);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }
    public void update(int index, int val) { updateHelper(1, 0, n - 1, index, val); }
    private void updateHelper(int node, int lo, int hi, int idx, int val) {
        if (lo == hi) { tree[node] = val; return; }
        int mid = (lo + hi) / 2;
        if (idx <= mid) updateHelper(2 * node, lo, mid, idx, val);
        else updateHelper(2 * node + 1, mid + 1, hi, idx, val);
        tree[node] = tree[2 * node] + tree[2 * node + 1];
    }
    public int sumRange(int left, int right) { return query(1, 0, n - 1, left, right); }
    private int query(int node, int lo, int hi, int l, int r) {
        if (r < lo || hi < l) return 0;
        if (l <= lo && hi <= r) return tree[node];
        int mid = (lo + hi) / 2;
        return query(2 * node, lo, mid, l, r) + query(2 * node + 1, mid + 1, hi, l, r);
    }
}
```
**Approach:** A segment tree gives O(log n) update and O(log n) range query, versus O(n) per query with a plain array (recomputing the sum each time) or O(n) per update with a prefix-sum array.
**Tricks/Pitfalls:** This is the canonical example of "when prefix sums aren't enough" — prefix sums are O(1) query but O(n) update; segment trees balance both at O(log n).
**Complexity:** Time O(log n) per update/query, Space O(n).

### 149. Range Sum Query 2D — Immutable
**Problem:** Given a 2D matrix (immutable), design a class to efficiently answer sum queries for any rectangular sub-region.

**Example:**
```
Input: matrix = [[3,0,1,4,2],[5,6,3,2,1],[1,2,0,1,5],[4,1,0,1,7],[1,0,3,0,5]]; sumRegion(2,1,4,3) -> 8
Output: 8
```
```java
class NumMatrix {
    int[][] prefix;
    public NumMatrix(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        prefix = new int[rows + 1][cols + 1];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                prefix[i + 1][j + 1] = prefix[i][j + 1] + prefix[i + 1][j] - prefix[i][j] + matrix[i][j];
    }
    public int sumRegion(int row1, int col1, int row2, int col2) {
        return prefix[row2 + 1][col2 + 1] - prefix[row1][col2 + 1] - prefix[row2 + 1][col1] + prefix[row1][col1];
    }
}
```
**Approach:** 2D prefix sum via inclusion-exclusion; since the matrix is immutable, this beats a segment tree here — O(1) query after O(m·n) preprocessing.
**Tricks/Pitfalls:** The inclusion-exclusion formula (`+prefix[i][j]` added back after double-subtracting the overlapping corner) is easy to get sign-wrong on first attempt — deriving it via a small hand-drawn example before coding avoids bugs.
**Complexity:** Time O(1) per query, O(m·n) preprocessing, Space O(m·n).

### 150. Kth Largest Element in a Stream
**Problem:** Design a class that, given an initial array and integer `k`, supports adding new numbers and returning the kth largest element after each addition.

**Example:**
```
Input: KthLargest(3, [4,5,8,2]); add(3) -> 4; add(5) -> 5; add(10) -> 5; add(9) -> 8; add(4) -> 8
Output: [4, 5, 5, 8, 8]
```
```java
class KthLargest {
    private final PriorityQueue<Integer> minHeap;
    private final int k;
    public KthLargest(int k, int[] nums) {
        this.k = k;
        minHeap = new PriorityQueue<>();
        for (int n : nums) add(n);
    }
    public int add(int val) {
        minHeap.offer(val);
        if (minHeap.size() > k) minHeap.poll(); // evict smallest, keeping only top-k
        return minHeap.peek();
    }
}
```
**Approach:** Maintain a min-heap capped at size k — its root is always the kth largest element seen so far, since anything smaller than the heap's minimum has already been evicted.
**Tricks/Pitfalls:** Using a **min**-heap (not max) is counterintuitive at first — the min of the top-k elements is exactly the kth largest overall.
**Complexity:** Time O(log k) per insertion, Space O(k).

### 151. Find Median from Data Stream
**Problem:** Design a data structure that supports adding numbers from a stream and finding the median of all numbers added so far.

**Example:**
```
Input: addNum(1); addNum(2); findMedian() -> 1.5; addNum(3); findMedian() -> 2.0
Output: [1.5, 2.0]
```
```java
class MedianFinder {
    private final PriorityQueue<Integer> lowerHalf = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
    private final PriorityQueue<Integer> upperHalf = new PriorityQueue<>();                             // min-heap
    public void addNum(int num) {
        lowerHalf.offer(num);
        upperHalf.offer(lowerHalf.poll()); // move the max of lower half to upper half
        if (upperHalf.size() > lowerHalf.size()) lowerHalf.offer(upperHalf.poll()); // rebalance
    }
    public double findMedian() {
        return lowerHalf.size() > upperHalf.size() ? lowerHalf.peek()
             : (lowerHalf.peek() + upperHalf.peek()) / 2.0;
    }
}
```
**Approach:** Two heaps split the stream into a "lower half" (max-heap) and "upper half" (min-heap), each kept balanced in size; the median is derived from their roots without ever fully sorting the data.
**Tricks/Pitfalls:** The rebalancing step after every insertion (always routing through `lowerHalf` first, then correcting) is what keeps the size invariant (`|lowerHalf| - |upperHalf| <= 1`) intact.
**Complexity:** Time O(log n) per insertion, O(1) per median query, Space O(n).

### 152. Number of Islands II (Dynamic Union-Find)
**Problem:** Given an `m x n` grid initially all water, and a sequence of land-addition positions, return the number of islands after each addition.

**Example:**
```
Input: m = 3, n = 3, positions = [[0,0],[0,1],[1,2],[2,1]]
Output: [1,1,2,3]
```
```java
public List<Integer> numIslands2(int m, int n, int[][] positions) {
    UnionFind uf = new UnionFind(m * n);
    boolean[][] isLand = new boolean[m][n];
    List<Integer> result = new ArrayList<>();
    int count = 0;
    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    for (int[] pos : positions) {
        int r = pos[0], c = pos[1];
        if (isLand[r][c]) { result.add(count); continue; } // duplicate addition
        isLand[r][c] = true;
        count++;
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nc >= 0 && nr < m && nc < n && isLand[nr][nc]) {
                if (uf.union(r * n + c, nr * n + nc)) count--; // merging two islands
            }
        }
        result.add(count);
    }
    return result;
}
```
**Approach:** Union-Find handles the "online" (incremental) nature of this problem far better than re-running full DFS/BFS after every addition — each new land cell merges with its already-land neighbors in near-O(1).
**Tricks/Pitfalls:** Recognizing that a streaming/incremental connectivity problem is a strong signal for Union-Find over DFS/BFS (which would require full re-scans) is the key insight.
**Complexity:** Time O(positions × α(mn)) ≈ O(positions), Space O(m·n).

### 153. Accounts Merge (Union-Find on Emails)
**Problem:** Given a list of accounts, each with a name and a list of emails, merge accounts that share at least one common email, and return the merged accounts (emails sorted).

**Example:**
```
Input: accounts = [["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]
Output: [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]
```
```java
public List<List<String>> accountsMerge(List<List<String>> accounts) {
    Map<String, String> emailToName = new HashMap<>();
    Map<String, Integer> emailToId = new HashMap<>();
    int id = 0;
    for (List<String> account : accounts) {
        String name = account.get(0);
        for (int i = 1; i < account.size(); i++) {
            String email = account.get(i);
            if (!emailToId.containsKey(email)) emailToId.put(email, id++);
            emailToName.put(email, name);
        }
    }
    UnionFind uf = new UnionFind(id);
    for (List<String> account : accounts) {
        int firstId = emailToId.get(account.get(1));
        for (int i = 2; i < account.size(); i++) uf.union(firstId, emailToId.get(account.get(i)));
    }
    Map<Integer, TreeSet<String>> groups = new HashMap<>();
    for (Map.Entry<String, Integer> entry : emailToId.entrySet()) {
        int root = uf.find(entry.getValue());
        groups.computeIfAbsent(root, k -> new TreeSet<>()).add(entry.getKey());
    }
    List<List<String>> result = new ArrayList<>();
    for (Map.Entry<Integer, TreeSet<String>> entry : groups.entrySet()) {
        List<String> merged = new ArrayList<>();
        String anyEmail = entry.getValue().first();
        merged.add(emailToName.get(anyEmail));
        merged.addAll(entry.getValue());
        result.add(merged);
    }
    return result;
}
```
**Approach:** Assign each unique email an integer ID; union all emails within the same account; group emails by their final root, using a `TreeSet` for automatic sorted, de-duplicated output.
**Tricks/Pitfalls:** Mapping emails (not account indices) to Union-Find IDs is the key modeling decision — the actual "connection" happens through shared emails across different account entries, not account order.
**Complexity:** Time O(n log n) (dominated by TreeSet operations) where n = total emails, Space O(n).

### 154. Design Add and Search Words Data Structure (Trie with Wildcard)
**Problem:** Design a data structure supporting `addWord(word)` and `search(word)`, where `search` may contain `.` to match any single character.

**Example:**
```
Input: addWord("bad"); addWord("dad"); addWord("mad"); search("pad") -> false; search(".ad") -> true; search("b..") -> true
Output: [false, true, true]
```
```java
class WordDictionary {
    class TrieNode { TrieNode[] children = new TrieNode[26]; boolean isEnd; }
    private final TrieNode root = new TrieNode();
    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray())
            node = node.children[c - 'a'] == null ? (node.children[c - 'a'] = new TrieNode()) : node.children[c - 'a'];
        node.isEnd = true;
    }
    public boolean search(String word) { return dfs(word, 0, root); }
    private boolean dfs(String word, int idx, TrieNode node) {
        if (node == null) return false;
        if (idx == word.length()) return node.isEnd;
        char c = word.charAt(idx);
        if (c == '.') {
            for (TrieNode child : node.children) if (dfs(word, idx + 1, child)) return true;
            return false;
        }
        return dfs(word, idx + 1, node.children[c - 'a']);
    }
}
```
**Approach:** Standard Trie insertion; search uses DFS that branches into *all* 26 children when a wildcard `.` is encountered, rather than following a single deterministic path.
**Tricks/Pitfalls:** The wildcard branching is what turns this from a simple O(L) Trie lookup into a potentially O(26^(number of dots)) search — worth stating this complexity tradeoff explicitly when asked.
**Complexity:** Time O(L) per exact search, O(26^d · L) worst case with d wildcards, Space O(total characters × 26).

### 155. Count of Smaller Numbers After Self (Binary Indexed Tree / Fenwick Tree)
**Problem:** Given an integer array `nums`, return a counts array where `counts[i]` is the number of smaller elements to the right of `nums[i]`.

**Example:**
```
Input: nums = [5,2,6,1]
Output: [2,1,1,0]
```
```java
public List<Integer> countSmaller(int[] nums) {
    int n = nums.length;
    int[] sorted = nums.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> rank = new HashMap<>();
    int r = 0;
    for (int v : sorted) if (!rank.containsKey(v)) rank.put(v, ++r); // 1-indexed rank
    int[] bit = new int[r + 1];
    Integer[] result = new Integer[n];
    for (int i = n - 1; i >= 0; i--) {
        int rk = rank.get(nums[i]);
        result[i] = query(bit, rk - 1);  // count of smaller ranks already inserted (to the right)
        update(bit, rk, 1);
    }
    return Arrays.asList(result);
}
private void update(int[] bit, int i, int delta) { for (; i < bit.length; i += i & (-i)) bit[i] += delta; }
private int query(int[] bit, int i) { int sum = 0; for (; i > 0; i -= i & (-i)) sum += bit[i]; return sum; }
```
**Approach:** Process the array right-to-left, using a Fenwick Tree (Binary Indexed Tree) keyed by rank to answer "how many smaller values have I seen so far" in O(log n), then insert the current value's rank.
**Tricks/Pitfalls:** Coordinate compression (mapping values to dense 1-indexed ranks) is required first, since BIT indices must be small and contiguous — this pattern (compress + BIT) generalizes to many "count smaller/larger to the side" problems.
**Complexity:** Time O(n log n), Space O(n).

### 156. LFU Cache
**Problem:** Design a Least Frequently Used (LFU) cache supporting `get(key)` and `put(key, value)` in O(1), evicting the least frequently used key (ties broken by least recently used) when full.

**Example:**
```
Input: LFUCache(2); put(1,1); put(2,2); get(1) -> 1; put(3,3) evicts key 2; get(2) -> -1; get(3) -> 3; put(4,4) evicts key 1; get(1) -> -1; get(3) -> 3; get(4) -> 4
Output: [1, -1, 3, -1, 3, 4]
```
```java
class LFUCache {
    class Node { int key, val, freq = 1; }
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final Map<Integer, LinkedHashSet<Node>> freqMap = new HashMap<>();
    public LFUCache(int capacity) { this.capacity = capacity; }
    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        Node node = cache.get(key);
        touch(node);
        return node.val;
    }
    public void put(int key, int value) {
        if (capacity == 0) return;
        if (cache.containsKey(key)) { Node node = cache.get(key); node.val = value; touch(node); return; }
        if (cache.size() == capacity) {
            Node evict = freqMap.get(minFreq).iterator().next(); // least recently used among least frequent
            freqMap.get(minFreq).remove(evict);
            cache.remove(evict.key);
        }
        Node node = new Node(); node.key = key; node.val = value;
        cache.put(key, node);
        freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(node);
        minFreq = 1;
    }
    private void touch(Node node) {
        freqMap.get(node.freq).remove(node);
        if (freqMap.get(node.freq).isEmpty() && node.freq == minFreq) minFreq++;
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }
}
```
**Approach:** A `HashMap<key, Node>` for O(1) lookup, plus a `HashMap<frequency, LinkedHashSet<Node>>` bucketing nodes by access frequency (with insertion order preserved within each bucket for LRU tie-breaking) — together giving O(1) `get`/`put`.
**Tricks/Pitfalls:** LFU is considerably harder than LRU because eviction must consider *both* frequency and recency (as a tie-break) — tracking `minFreq` incrementally (never recomputing it by scanning) is essential for true O(1).
**Complexity:** Time O(1) for both `get` and `put`, Space O(capacity).

### 157. Swim in Rising Water (Union-Find + Binary Search on Answer)
**Problem:** Given an `n x n` grid where `grid[i][j]` is the elevation at that cell, find the minimum time `t` such that you can swim from top-left to bottom-right, only moving to cells with elevation ≤ `t`.

**Example:**
```
Input: grid = [[0,2],[1,3]]
Output: 3
```
```java
public int swimInWater(int[][] grid) {
    int n = grid.length;
    int lo = 0, hi = n * n - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canReach(grid, mid)) hi = mid; else lo = mid + 1;
    }
    return lo;
}
private boolean canReach(int[][] grid, int time) {
    int n = grid.length;
    if (grid[0][0] > time) return false;
    UnionFind uf = new UnionFind(n * n);
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++) {
            if (grid[i][j] > time) continue;
            if (i + 1 < n && grid[i + 1][j] <= time) uf.union(i * n + j, (i + 1) * n + j);
            if (j + 1 < n && grid[i][j + 1] <= time) uf.union(i * n + j, i * n + (j + 1));
        }
    return uf.find(0) == uf.find(n * n - 1);
}
```
**Approach:** Binary search on the answer (the monotonic predicate: "can I reach the end by time T" only becomes true and stays true as T increases), using Union-Find at each candidate time to check connectivity between start and end.
**Tricks/Pitfalls:** Recognizing the monotonic structure ("if reachable at time T, also reachable at any T' > T") is what licenses binary search here — without that insight, this looks like a pure graph problem requiring per-time-step simulation.
**Complexity:** Time O(n² log(n²)) — Union-Find check per binary search step, Space O(n²).

### 158. Min Cost to Connect All Points (Kruskal's MST via Union-Find)
**Problem:** Given `points` on a 2D plane, find the minimum cost to connect all points using Manhattan-distance edges (a Minimum Spanning Tree).

**Example:**
```
Input: points = [[0,0],[2,2],[3,10],[5,2],[7,0]]
Output: 20
```
```java
public int minCostConnectPoints(int[][] points) {
    int n = points.length;
    List<int[]> edges = new ArrayList<>(); // [cost, i, j]
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++) {
            int cost = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
            edges.add(new int[]{cost, i, j});
        }
    edges.sort((a, b) -> a[0] - b[0]); // Kruskal's: process edges by ascending weight
    UnionFind uf = new UnionFind(n);
    int totalCost = 0, edgesUsed = 0;
    for (int[] edge : edges) {
        if (uf.union(edge[1], edge[2])) { // only counts if it actually connects two components
            totalCost += edge[0];
            edgesUsed++;
            if (edgesUsed == n - 1) break; // MST complete
        }
    }
    return totalCost;
}
```
**Approach:** Kruskal's algorithm — generate all possible edges, sort by weight, and greedily add the cheapest edge that doesn't create a cycle (checked via Union-Find) until the MST is complete (n-1 edges).
**Tricks/Pitfalls:** For dense graphs like this (all-pairs edges), Prim's algorithm with a heap is asymptotically better (O(n² ) vs Kruskal's O(n² log n) from sorting all n² edges) — worth mentioning as a follow-up optimization for large n.
**Complexity:** Time O(n² log n) (dominated by sorting all pairs), Space O(n²).

# PART 3: Rapid-Fire DSA Interviewer Q&A

> These are the conceptual questions interviewers commonly ask *around* coding problems — to probe whether you understand the "why," not just whether you can produce working code.

### General Complexity & Data Structure Reasoning

**Q: How do you decide between `ArrayList` and `LinkedList` in a real Java application?**
A: `ArrayList` almost always wins in practice due to cache locality and lower per-element memory overhead (no node pointers), even though `LinkedList` has "better" theoretical insert/delete complexity. Only prefer `LinkedList` (or better, `ArrayDeque`) when you need frequent insertions/deletions at both ends with no random access — and even then, `ArrayDeque` usually outperforms `LinkedList` for stack/queue use cases.

**Q: Why is `ArrayDeque` preferred over `Stack` and `LinkedList` for stack/queue implementations in Java?**
A: `Stack` extends the legacy `Vector` and is unnecessarily synchronized (overhead with no benefit in single-threaded use). `LinkedList` has per-node object overhead and worse cache locality than `ArrayDeque`'s resizable-array-based implementation. `ArrayDeque` is faster in virtually all benchmarks for both stack and queue operations.

**Q: What's the time complexity of `HashMap.get()` in the worst case, and why doesn't this matter in practice?**
A: Worst case is O(n) if all keys hash to the same bucket (or O(log n) since Java 8's tree-ification of long bucket chains ≥ 8 entries). In practice, a good hash function distributes keys evenly, making average-case O(1) the realistic expectation — the O(log n) treeification is a safety net against pathological/adversarial input, not the common case.

**Q: When would you choose a `TreeMap` over a `HashMap` even though it's slower?**
A: When you need sorted iteration order, range queries (`floorKey`, `ceilingKey`, `subMap`), or predictable ordering behavior for output — e.g., generating a sorted report of trade counts by settlement date.

**Q: Explain time complexity vs space complexity trade-offs using a real example.**
A: Memoization in DP trades space (a cache) for time (avoiding recomputation) — e.g., naive Fibonacci is O(2ⁿ) time, O(n) space (call stack); memoized Fibonacci is O(n) time, O(n) space (cache + stack). Another example: a HashSet-based duplicate check trades O(n) space for O(n) time versus an O(1)-space, O(n²)-time nested-loop check.

### Recursion & Backtracking

**Q: How do you convert a recursive solution to an iterative one, and when would you need to?**
A: Use an explicit stack to simulate the call stack (common for DFS, tree traversals). You'd need to when recursion depth risks a `StackOverflowError` (e.g., a very deep/unbalanced tree, or a large linear recursion like naive factorial on huge n) — Java doesn't have tail-call optimization, so deep recursion is a real production risk, not just a theoretical concern.

**Q: What's the difference between backtracking and pure brute-force recursion?**
A: Backtracking prunes invalid branches *early* (before fully exploring them), while brute force explores every possibility and filters at the end. Both may have the same theoretical worst-case complexity, but backtracking's practical performance is usually dramatically better due to pruning.

### Sorting & Searching

**Q: Why does Java use different sorting algorithms for primitives vs. objects?**
A: Primitives have no identity (two `5`s are indistinguishable), so a non-stable, allocation-free Dual-Pivot Quicksort is fine and fast. Objects can carry meaningful identity/equality distinctions where consumers expect stability (e.g., sorting invoices first by date, then by amount — a second sort must preserve the first sort's relative order for equal amounts) — so Java uses a stable algorithm (TimSort) for object arrays and collections.

**Q: Given a nearly-sorted array, which sort would you pick and why?**
A: Insertion sort — its best case is O(n) for nearly-sorted data (each element requires very few shifts), vastly outperforming the O(n log n) general-purpose sorts in this specific scenario. This is also why TimSort (used by Java for objects) detects and exploits already-sorted "runs" internally.

**Q: How would you find the kth largest element without fully sorting the array?**
A: Quickselect (a Quicksort-partition variant) finds it in average O(n) time — partition around a pivot, and recurse only into the side that contains the kth position, discarding the other side entirely (unlike Quicksort, which recurses into both).

### Trees & Graphs

**Q: When would you use BFS over DFS, and vice versa?**
A: BFS for shortest path in unweighted graphs (guarantees the fewest edges) and level-by-level processing. DFS for exhaustive exploration (all paths, cycle detection, topological sort, backtracking-style problems) and when memory is a concern with a mostly-linear/deep graph (DFS's stack is often smaller than BFS's queue for wide graphs).

**Q: Why can't Dijkstra's algorithm handle negative edge weights?**
A: Dijkstra greedily finalizes a node's shortest distance once popped from the priority queue, assuming no future path could improve it — negative edges break this assumption, since a longer-looking path could later become shorter via a negative-weight edge. Bellman-Ford handles this by relaxing all edges repeatedly rather than greedily finalizing.

**Q: What's the practical difference between an adjacency list and adjacency matrix, and when would you choose each?**
A: Adjacency list: O(V+E) space, ideal for sparse graphs (most real-world graphs — social networks, road networks, dependency graphs), O(degree) edge lookup. Adjacency matrix: O(V²) space, but O(1) edge existence check — better for dense graphs or when you frequently need "are u and v connected" queries.

### Dynamic Programming

**Q: How do you recognize that a problem is a DP problem during an interview?**
A: Look for: (1) the problem asks for an optimal value (min/max/count) rather than "find any solution," (2) it can be broken into smaller, similar subproblems ("optimal substructure"), and (3) those subproblems overlap (the same subproblem is needed multiple times) — if subproblems don't overlap, it's likely Divide & Conquer instead, not DP.

**Q: Memoization vs Tabulation — which do you prefer starting with in an interview, and why?**
A: Start with memoization (top-down) — it mirrors the natural recursive problem statement, making it easier to derive correctly under time pressure, and you write the base case/recursive relation exactly as you'd describe the problem verbally. Convert to tabulation (bottom-up) afterward if the interviewer wants to discuss further space optimization (tabulation often allows dimension reduction, e.g., 2D → 1D rolling array) or wants to avoid recursion's stack overhead entirely.

**Q: How do you optimize a 2D DP solution's space to O(n)?**
A: If `dp[i][j]` only depends on the *previous* row (`dp[i-1][...]`), you can drop the first dimension and reuse a single 1D array, updating in place — this works for problems like Unique Paths, 0/1 Knapsack, and Edit Distance (with care around iteration direction for the latter).

### Miscellaneous / Behavioral-Adjacent

**Q: You're stuck on a hard problem in an interview. What's your process?**
A: State the brute-force approach first out loud (shows you can always produce *a* solution), analyze its complexity, then look for the bottleneck operation and ask "can I use a different data structure or precomputation to speed up just that part" — many "hard" problems are a medium problem with one key data-structure substitution (e.g., replacing a linear scan with a HashMap lookup, or a nested loop with two pointers).

**Q: How do you handle a problem where you're not 100% sure your solution is correct?**
A: Trace through 1-2 concrete examples by hand (including an edge case: empty input, single element, all duplicates), and explicitly state the invariant you believe your loop/recursion maintains — articulating the invariant out loud often reveals bugs before you even finish coding, and shows the interviewer your reasoning process regardless of the final outcome.

**Q: What's your approach to testing your own code after writing it, without running it?**
A: Walk through the smallest non-trivial input by hand, check boundary conditions (empty/null input, single element, all-same elements), verify loop bounds don't off-by-one (especially `<` vs `<=`), and check that recursive base cases are reachable and correctly terminate.

---

## Final Notes for Interview Day

- **Pattern recognition beats memorization.** Many of the 150+ problems above are the *same underlying pattern* wearing different clothes — two pointers, sliding window, monotonic stack/queue, backtracking with pruning, interval DP, Union-Find for connectivity, binary search on the answer. Study by pattern, not by problem number.
- **Always state complexity out loud**, even before being asked — it signals you're thinking about efficiency proactively, not reactively.
- **Practice explaining the "why" behind non-obvious tricks** (e.g., why iterate backward in 0/1 knapsack, why sort by end time in interval greedy problems) — this is what separates a senior-level answer from a junior one who has simply memorized the code.
- For an 8-YOE-level interview, expect **follow-up variations** on base problems (e.g., "now what if updates happen concurrently," "what if the array doesn't fit in memory") — having a couple of these follow-ups already thought through per major pattern is a strong differentiator.

*Good luck — you've got the depth to back it up.*