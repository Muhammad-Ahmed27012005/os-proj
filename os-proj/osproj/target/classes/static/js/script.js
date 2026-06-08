// Global variables
let tasks = [];

// Load tasks on page load
document.addEventListener("DOMContentLoaded", function () {
  loadTasks();

  // Add task form handler
  const taskForm = document.getElementById("taskForm");
  if (taskForm) {
    taskForm.addEventListener("submit", function (e) {
      e.preventDefault();
      addTask();
    });
  }
});

// Add new task
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
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(task),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        alert("Task added successfully!");
        document.getElementById("taskForm").reset();
        loadTasks();
      } else {
        alert("Error: " + data.error);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to add task");
    });
}

// Load tasks
function loadTasks() {
  fetch("/scheduling/task/list")
    .then((response) => response.json())
    .then((data) => {
      tasks = data;
      displayTasks();
    })
    .catch((error) => console.error("Error loading tasks:", error));
}

// Display tasks in table
function displayTasks() {
  const tbody = document.getElementById("taskListBody");
  if (!tbody) return;

  tbody.innerHTML = "";

  tasks.forEach((task) => {
    const row = tbody.insertRow();
    row.innerHTML = `
            <td>${task.id}</td>
            <td>${task.taskName}</td>
            <td>${task.burstTime}</td>
            <td>${task.priority}</td>
            <td>${task.arrivalTime}</td>
            <td><span class="badge bg-info">${task.taskType}</span></td>
            <td><span class="badge bg-${task.status === "COMPLETED" ? "success" : "warning"}">${task.status}</span></td>
        `;
  });
}

// Run scheduling algorithm
function runScheduling(algorithm) {
  const timeQuantum = document.getElementById("timeQuantum").value || 2;

  // Show loading spinner
  document.getElementById("resultsSection").style.display = "block";
  document.getElementById("metricsDisplay").innerHTML =
    '<div class="text-center"><div class="spinner"></div><p>Running ' +
    algorithm +
    " scheduling...</p></div>";

  fetch(`/scheduling/schedule/${algorithm}?timeQuantum=${timeQuantum}`, {
    method: "POST",
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displayResults(data.data);
      } else {
        alert("Error: " + data.error);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to run scheduling algorithm");
    });
}

// Display scheduling results
function displayResults(data) {
  const resultsBody = document.getElementById("resultsBody");
  const metricsDisplay = document.getElementById("metricsDisplay");

  // Display metrics
  const metrics = data.metrics;
  metricsDisplay.innerHTML = `
        <div class="row">
            <div class="col-md-6">
                <div class="alert alert-info">
                    <h5>Algorithm: ${data.algorithm}</h5>
                    <p><strong>Average Waiting Time:</strong> ${metrics.avgWaitingTime.toFixed(2)}</p>
                    <p><strong>Average Turnaround Time:</strong> ${metrics.avgTurnaroundTime.toFixed(2)}</p>
                </div>
            </div>
        </div>
    `;

  // Display task details
  resultsBody.innerHTML = "";
  data.scheduledTasks.forEach((task) => {
    const row = resultsBody.insertRow();
    row.innerHTML = `
            <td>${task.taskName}</td>
            <td>${task.arrivalTime}</td>
            <td>${task.burstTime}</td>
            <td>${task.waitingTime}</td>
            <td>${task.turnaroundTime}</td>
            <td><span class="badge bg-success">${task.status}</span></td>
        `;
  });

  // Show results section
  document.getElementById("resultsSection").style.display = "block";
  document.getElementById("comparisonSection").style.display = "none";

  // Scroll to results
  document
    .getElementById("resultsSection")
    .scrollIntoView({ behavior: "smooth" });
}

// Compare all algorithms
function compareAlgorithms() {
  document.getElementById("comparisonSection").style.display = "block";
  document.getElementById("comparisonBody").innerHTML =
    '<tr><td colspan="3" class="text-center"><div class="spinner"></div><p>Comparing algorithms...</p></td></tr>';

  fetch("/scheduling/compare", {
    method: "GET",
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        displayComparison(data.data);
      } else {
        alert("Error: " + data.error);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Failed to compare algorithms");
    });
}

// Display algorithm comparison
function displayComparison(data) {
  const comparisonBody = document.getElementById("comparisonBody");
  comparisonBody.innerHTML = "";

  for (const [algorithm, metrics] of Object.entries(data)) {
    const row = comparisonBody.insertRow();
    row.innerHTML = `
            <td><strong>${algorithm}</strong></td>
            <td>${metrics.avgWaitingTime.toFixed(2)}</td>
            <td>${metrics.avgTurnaroundTime.toFixed(2)}</td>
        `;
  }

  document.getElementById("comparisonSection").style.display = "block";
  document
    .getElementById("comparisonSection")
    .scrollIntoView({ behavior: "smooth" });
}

// Room management functions
function editRoom(id) {
  // Implement room editing logic
  alert("Edit room functionality coming soon!");
}

function deleteRoom(id) {
  if (confirm("Are you sure you want to delete this room?")) {
    window.location.href = `/rooms/delete/${id}`;
  }
}

// Booking management functions
function viewBooking(id) {
  // Implement booking view logic
  alert("View booking functionality coming soon!");
}

function cancelBooking(id) {
  if (confirm("Are you sure you want to cancel this booking?")) {
    window.location.href = `/bookings/cancel/${id}`;
  }
}

// Utility functions
function formatCurrency(amount) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

// Search functionality
function searchRooms() {
  const searchTerm = document.getElementById("searchInput").value.toLowerCase();
  const roomCards = document.querySelectorAll(".room-card");

  roomCards.forEach((card) => {
    const roomType = card.querySelector("h5").textContent.toLowerCase();
    const roomNumber = card
      .querySelector(".text-muted")
      .textContent.toLowerCase();

    if (roomType.includes(searchTerm) || roomNumber.includes(searchTerm)) {
      card.closest(".col-md-4").style.display = "block";
    } else {
      card.closest(".col-md-4").style.display = "none";
    }
  });
}

// Filter rooms by type
function filterRoomsByType(type) {
  window.location.href = `/rooms?type=${type}`;
}

// Auto-dismiss alerts after 5 seconds
document.addEventListener("DOMContentLoaded", function () {
  setTimeout(function () {
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach((alert) => {
      const bsAlert = new bootstrap.Alert(alert);
      bsAlert.close();
    });
  }, 5000);
});

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener("click", function (e) {
    e.preventDefault();
    document.querySelector(this.getAttribute("href")).scrollIntoView({
      behavior: "smooth",
    });
  });
});
