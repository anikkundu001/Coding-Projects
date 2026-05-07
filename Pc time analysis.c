/**
 * Sorting Algorithm Performance Dashboard
 * HTTP Server in C that serves an interactive web interface.
 * Compile: gcc -o sorting_server sorting_server.c -Wall -Wextra
 * Run: ./sorting_server
 * Open browser: http://localhost:8080
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <signal.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 8080
#define BUFFER_SIZE 16384
#define MAX_SIZES 3
#define MAX_ALGOS 5

/* ---------- Sorting Algorithms ---------- */

void bubble_sort(int arr[], int n) {
    for (int i = 0; i < n - 1; i++)
        for (int j = 0; j < n - i - 1; j++)
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
}

void insertion_sort(int arr[], int n) {
    for (int i = 1; i < n; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

void merge(int arr[], int l, int m, int r, int temp[]) {
    int i = l, j = m + 1, k = l;
    while (i <= m && j <= r)
        temp[k++] = (arr[i] <= arr[j]) ? arr[i++] : arr[j++];
    while (i <= m) temp[k++] = arr[i++];
    while (j <= r) temp[k++] = arr[j++];
    for (i = l; i <= r; i++) arr[i] = temp[i];
}

void merge_sort_rec(int arr[], int l, int r, int temp[]) {
    if (l >= r) return;
    int m = l + (r - l) / 2;
    merge_sort_rec(arr, l, m, temp);
    merge_sort_rec(arr, m + 1, r, temp);
    merge(arr, l, m, r, temp);
}

void merge_sort(int arr[], int n) {
    int *temp = (int*)malloc(n * sizeof(int));
    merge_sort_rec(arr, 0, n - 1, temp);
    free(temp);
}

int partition(int arr[], int low, int high) {
    int pivot = arr[low + (high - low) / 2];
    int i = low - 1, j = high + 1;
    while (1) {
        do i++; while (arr[i] < pivot);
        do j--; while (arr[j] > pivot);
        if (i >= j) return j;
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

void quick_sort_rec(int arr[], int low, int high) {
    if (low < high) {
        int pi = partition(arr, low, high);
        quick_sort_rec(arr, low, pi);
        quick_sort_rec(arr, pi + 1, high);
    }
}

void quick_sort(int arr[], int n) {
    quick_sort_rec(arr, 0, n - 1);
}

void heapify(int arr[], int n, int i) {
    int largest = i, left = 2 * i + 1, right = 2 * i + 2;
    if (left < n && arr[left] > arr[largest]) largest = left;
    if (right < n && arr[right] > arr[largest]) largest = right;
    if (largest != i) {
        int temp = arr[i];
        arr[i] = arr[largest];
        arr[largest] = temp;
        heapify(arr, n, largest);
    }
}

void heap_sort(int arr[], int n) {
    for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
    for (int i = n - 1; i > 0; i--) {
        int temp = arr[0];
        arr[0] = arr[i];
        arr[i] = temp;
        heapify(arr, i, 0);
    }
}

/* ---------- Benchmarking ---------- */

typedef struct {
    const char* name;
    void (*sort_func)(int*, int);
} SortAlgorithm;

SortAlgorithm algorithms[] = {
    {"Bubble", bubble_sort},
    {"Insertion", insertion_sort},
    {"Merge", merge_sort},
    {"Quick", quick_sort},
    {"Heap", heap_sort}
};

void generate_random_array(int arr[], int n) {
    for (int i = 0; i < n; i++)
        arr[i] = rand() % 10000;
}

void copy_array(int dest[], int src[], int n) {
    memcpy(dest, src, n * sizeof(int));
}

double measure_time(void (*sort)(int*, int), int arr[], int n) {
    clock_t start = clock();
    sort(arr, n);
    clock_t end = clock();
    return (double)(end - start) / CLOCKS_PER_SEC;
}

char* build_json_benchmark() {
    int sizes[] = {1000, 2000, 4000};
    int num_sizes = sizeof(sizes) / sizeof(sizes[0]);
    int num_algos = sizeof(algorithms) / sizeof(algorithms[0]);

    // Allocate string buffer (plenty of space)
    char* json = (char*)malloc(BUFFER_SIZE);
    if (!json) return NULL;
    char* ptr = json;
    ptr += sprintf(ptr, "{\"sizes\":[");
    for (int i = 0; i < num_sizes; i++)
        ptr += sprintf(ptr, "%d%s", sizes[i], (i < num_sizes - 1) ? "," : "");
    ptr += sprintf(ptr, "],\"algorithms\":[");
    for (int i = 0; i < num_algos; i++)
        ptr += sprintf(ptr, "\"%s\"%s", algorithms[i].name, (i < num_algos - 1) ? "," : "");
    ptr += sprintf(ptr, "],\"times\":[");

    double times[MAX_ALGOS][MAX_SIZES];
    srand(time(NULL));

    for (int s = 0; s < num_sizes; s++) {
        int n = sizes[s];
        int* original = (int*)malloc(n * sizeof(int));
        generate_random_array(original, n);
        int* work = (int*)malloc(n * sizeof(int));

        for (int a = 0; a < num_algos; a++) {
            copy_array(work, original, n);
            times[a][s] = measure_time(algorithms[a].sort_func, work, n);
        }

        free(original);
        free(work);
    }

    // Format JSON times matrix
    for (int a = 0; a < num_algos; a++) {
        ptr += sprintf(ptr, "[");
        for (int s = 0; s < num_sizes; s++) {
            ptr += sprintf(ptr, "%.6f%s", times[a][s], (s < num_sizes - 1) ? "," : "");
        }
        ptr += sprintf(ptr, "]%s", (a < num_algos - 1) ? "," : "");
    }
    ptr += sprintf(ptr, "]}");
    return json;
}

/* ---------- HTTP Server ---------- */

const char* HTML_PAGE = 
    "<!DOCTYPE html>"
    "<html><head><title>Sorting Algorithm Performance</title>"
    "<script src='https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'></script>"
    "<style>"
    "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; "
    "background: #f5f7fa; color: #2c3e50; }"
    ".container { max-width: 1200px; margin: auto; background: white; padding: 30px; "
    "border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }"
    "h1 { text-align: center; color: #1e466e; }"
    ".sub { text-align: center; color: #4a6a8b; margin-bottom: 30px; }"
    "canvas { max-height: 500px; width: 100%; margin-top: 20px; }"
    ".loading { text-align: center; margin: 40px; font-size: 1.2em; color: #2980b9; }"
    ".footer { text-align: center; margin-top: 30px; font-size: 0.85em; color: #7f8c8d; }"
    "</style></head><body>"
    "<div class='container'>"
    "<h1>📊 Sorting Algorithm Performance</h1>"
    "<div class='sub'>Execution time (seconds) – lower is better</div>"
    "<canvas id='perfChart'></canvas>"
    "<div id='loading' class='loading'>⏳ Loading benchmark data from C backend...</div>"
    "<div class='footer'>Data generated by native C sorting algorithms | "
    "Sizes: 1000, 2000, 4000 elements</div>"
    "</div><script>"
    "fetch('/api/sort').then(r=>r.json()).then(data=>{"
    "document.getElementById('loading').style.display='none';"
    "const ctx = document.getElementById('perfChart').getContext('2d');"
    "new Chart(ctx, { type: 'bar', data: { labels: data.algorithms, "
    "datasets: data.sizes.map((size,idx)=>({ label: size+' elements', "
    "data: data.times.map(row=>row[idx]), backgroundColor: `hsla(${idx*120}, 70%, 60%, 0.6)`, "
    "borderColor: `hsl(${idx*120}, 70%, 40%)`, borderWidth: 1 })) }, "
    "options: { responsive: true, plugins: { tooltip: { callbacks: { label: (ctx)=>`${ctx.dataset.label}: ${ctx.raw.toFixed(5)} s` } }, "
    "legend: { position: 'top' } }, scales: { y: { title: { display: true, text: 'Time (seconds)' }, beginAtZero: true } } } });"
    "}).catch(e=>{ document.getElementById('loading').innerHTML='⚠️ Failed to load data. Is the server running?'; });"
    "</script></body></html>";

void send_response(int client_fd, const char* status, const char* content_type, const char* body) {
    char response[BUFFER_SIZE];
    int len = snprintf(response, sizeof(response),
        "HTTP/1.1 %s\r\n"
        "Content-Type: %s\r\n"
        "Content-Length: %zu\r\n"
        "Connection: close\r\n"
        "\r\n"
        "%s",
        status, content_type, strlen(body), body);
    send(client_fd, response, len, 0);
}

void handle_request(int client_fd, const char* request) {
    // Simple path extraction
    char method[16], path[256];
    sscanf(request, "%15s %255s", method, path);

    if (strcmp(path, "/") == 0 || strcmp(path, "/index.html") == 0) {
        send_response(client_fd, "200 OK", "text/html", HTML_PAGE);
    }
    else if (strcmp(path, "/api/sort") == 0) {
        char* json = build_json_benchmark();
        if (json) {
            send_response(client_fd, "200 OK", "application/json", json);
            free(json);
        } else {
            send_response(client_fd, "500 Internal Server Error", "text/plain", "{\"error\":\"benchmark failed\"}");
        }
    }
    else {
        send_response(client_fd, "404 Not Found", "text/plain", "404 - Not Found");
    }
}

int create_server() {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd == -1) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    int opt = 1;
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr*)&address, sizeof(address)) < 0) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 10) < 0) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    printf("✅ Server listening on http://localhost:%d\n", PORT);
    printf("   Open your browser to see the interactive performance dashboard.\n");
    return server_fd;
}

void handle_sigint(int sig) {
    (void)sig;
    printf("\n🛑 Shutting down server...\n");
    exit(0);
}

int main() {
    signal(SIGINT, handle_sigint);
    signal(SIGPIPE, SIG_IGN);

    int server_fd = create_server();

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        int client_fd = accept(server_fd, (struct sockaddr*)&client_addr, &addr_len);
        if (client_fd < 0) {
            perror("accept");
            continue;
        }

        char buffer[BUFFER_SIZE] = {0};
        read(client_fd, buffer, BUFFER_SIZE - 1);
        handle_request(client_fd, buffer);
        close(client_fd);
    }

    close(server_fd);
    return 0;
}