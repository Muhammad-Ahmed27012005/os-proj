// Global variables
let tasks = [];

// Initialize on page load
document.addEventListener("DOMContentLoaded", function () {
  // Load tasks if on scheduling page
  if (document.getElementById("taskListBody")) {
    loadTasks();
  }

  // Add task form handler
  const taskForm = document.getElementById("taskForm");
  if (taskForm) {
    taskForm.addEventListener("submit", function (e) {
      e.preventDefault();
      addTask();
    });
  }

  // Initialize dashboard chart if on dashboard page
  if (document.getElementById("bookingChart")) {
    initDashboardChart();
  }

  // Auto-dismiss alerts
  setTimeout(function () {
    document.querySelectorAll(".alert-dismissible").forEach((alert) => {
      let bsAlert = new bootstrap.Alert(alert);
      bsAlert.close();
    });
  }, 5000);
});

// ========== TASK SCHEDULING FUNCTIONS ==========
function addTask() {
  const task = {
    taskName: document.getElementById("taskName").value,
    burstTime: parseInt(document.getElementById("burstTime").value),
    priority: parseInt(document.getElementById("priority").value),
    arrivalTime: parseInt(document.getElementById("arrivalTime").value),
    taskType: document.getElementById("taskType").value,
  };

  fetch("/scheduling/task/save", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(task),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        showToast("Task added successfully!", "success");
        document.getElementById("taskForm").reset();
        loadTasks();
      } else {
        showToast("Error: " + data.error, "danger");
      }
    })
    .catch((error) => showToast("Failed to add task", "danger"));
}

function loadTasks() {
  fetch("/scheduling/task/list")
    .then((response) => response.json())
    .then((data) => {
      tasks = data;
      displayTasks();
    })
    .catch((error) => console.error("Error loading tasks:", error));
}

function displayTasks() {
  const tbody = document.getElementById("taskListBody");
  if (!tbody) return;
  tbody.innerHTML = "";
  tasks.forEach((task) => {
    let statusClass = task.status === "COMPLETED" ? "success" : "warning";
    tbody.innerHTML += `
            <tr>
                <td>${task.id}</td>
                <td>${task.taskName}</td>
                <td>${task.burstTime}</td>
                <td>${task.priority}</td>
                <td>${task.arrivalTime}</td>
                <td><span class="badge bg-info">${task.taskType}</span></td>
                <td><span class="badge bg-${statusClass}">${task.status}</span></td>
            </tr>
        `;
  });
}

function runScheduling(algorithm) {
  const timeQuantum = document.getElementById("timeQuantum")?.value || 2;
  document.getElementById("resultsSection").style.display = "block";
  document.getElementById("metricsDisplay").innerHTML = `
        <div class="text-center"><div class="spinner"></div><p>Running ${algorithm} scheduling...</p></div>
    `;
  fetch(`/scheduling/schedule/${algorithm}?timeQuantum=${timeQuantum}`, {
    method: "POST",
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displayResults(data.data);
      } else {
        showToast("Error: " + data.error, "danger");
      }
    })
    .catch((error) => showToast("Failed to run scheduling", "danger"));
}

// Replace the existing displayResults function with:
function displayResults(data) {
    const metrics = data.metrics;
    document.getElementById("metricsDisplay").innerHTML = `
        <div class="alert alert-info rounded-4">
            <strong>${data.algorithm}</strong> | Avg Waiting: ${metrics.avgWaitingTime.toFixed(2)} | Avg Turnaround: ${metrics.avgTurnaroundTime.toFixed(2)}
        </div>`;
    
    const tbody = document.getElementById("resultsBody");
    tbody.innerHTML = "";
    data.scheduledTasks.forEach(task => {
        tbody.innerHTML += `<tr>
            <td>${task.taskName}</td><td>${task.arrivalTime}</td><td>${task.burstTime}</td>
            <td>${task.waitingTime}</td><td>${task.turnaroundTime}</td>
        </tr>`;
    });

    // Build visual timeline (Gantt bars)
    let timelineHtml = `<h6>Execution Timeline</h6>`;
    let currentTime = 0;
    data.scheduledTasks.forEach(task => {
        let start = currentTime;
        let end = currentTime + task.burstTime;
        let widthPercent = (task.burstTime / (data.scheduledTasks.reduce((sum, t) => sum + t.burstTime, 0))) * 100;
        timelineHtml += `
            <div class="gantt-row">
                <div class="gantt-label">${task.taskName}</div>
                <div class="gantt-bar">
                    <div class="gantt-progress" style="width: ${widthPercent}%;">${task.burstTime}</div>
                </div>
                <small>${start} → ${end}</small>
            </div>`;
        currentTime = end;
    });
    document.getElementById("timelineContainer").innerHTML = timelineHtml;
    document.getElementById("resultsSection").style.display = "block";
}

function compareAlgorithms() {
  document.getElementById("comparisonSection").style.display = "block";
  document.getElementById("comparisonBody").innerHTML =
    `<tr><td colspan="3" class="text-center"><div class="spinner"></div></td></tr>`;
  fetch("/scheduling/compare")
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        const comparisonBody = document.getElementById("comparisonBody");
        comparisonBody.innerHTML = "";
        for (const [algorithm, metrics] of Object.entries(data.data)) {
          comparisonBody.innerHTML += `
                        <tr>
                            <td><strong>${algorithm}</strong></td>
                            <td>${metrics.avgWaitingTime.toFixed(2)}</td>
                            <td>${metrics.avgTurnaroundTime.toFixed(2)}</td>
                        </tr>
                    `;
        }
      } else {
        showToast("Error comparing algorithms", "danger");
      }
    })
    .catch((error) => showToast("Failed to compare", "danger"));
}

// ========== ROOM & BOOKING HELPERS ==========
function editRoom(id) {
  showToast("Edit functionality coming soon", "info");
}
function deleteRoom(id) {
  if (confirm("Are you sure you want to delete this room?")) {
    window.location.href = `/rooms/delete/${id}`;
  }
}
function cancelBooking(id) {
  if (confirm("Cancel this booking?")) {
    window.location.href = `/bookings/cancel/${id}`;
  }
}

// ========== DASHBOARD CHART ==========
function initDashboardChart() {
  fetch("/bookings/recent-stats")
    .then((res) => res.json())
    .then((data) => {
      const ctx = document.getElementById("bookingChart").getContext("2d");
      new Chart(ctx, {
        type: "line",
        data: {
          labels: data.labels || ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
          datasets: [
            {
              label: "Bookings",
              data: data.values || [12, 19, 15, 17, 14, 23],
              borderColor: "#C5A55A",
              backgroundColor: "rgba(197,165,90,0.1)",
              tension: 0.4,
              fill: true,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { position: "top" },
          },
        },
      });
    })
    .catch((err) => console.error("Chart error:", err));
}

// ========== UTILITY ==========
function showToast(message, type) {
  const toastContainer =
    document.getElementById("toastContainer") || createToastContainer();
  const toastId = "toast-" + Date.now();
  const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
  toastContainer.insertAdjacentHTML("beforeend", toastHtml);
  const toastElement = document.getElementById(toastId);
  const toast = new bootstrap.Toast(toastElement, {
    autohide: true,
    delay: 4000,
  });
  toast.show();
  toastElement.addEventListener("hidden.bs.toast", () => toastElement.remove());
}

function createToastContainer() {
  const container = document.createElement("div");
  container.id = "toastContainer";
  container.style.position = "fixed";
  container.style.bottom = "20px";
  container.style.right = "20px";
  container.style.zIndex = "1100";
  document.body.appendChild(container);
  return container;
}

function formatCurrency(amount) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}
