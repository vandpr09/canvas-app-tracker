// ===== DATA =====
let courses = JSON.parse(localStorage.getItem("courses")) || [];
let tasks = JSON.parse(localStorage.getItem("tasks")) || [];
let courseColors = JSON.parse(localStorage.getItem("courseColors")) || {};

let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();
let selectedDay = null;
let editingTaskId = null;

// ===== ELEMENTS =====
const calendarGrid = document.getElementById("calendarGrid");
const currentMonthYear = document.getElementById("currentMonthYear");
const prevMonthBtn = document.getElementById("prevMonthBtn");
const nextMonthBtn = document.getElementById("nextMonthBtn");
const selectedDate = document.getElementById("selectedDate");
const taskDetailsList = document.getElementById("taskDetailsList");
const darkModeToggle = document.getElementById("darkModeToggle");

const courseNameInput = document.getElementById("courseName");
const addCourseBtn = document.getElementById("addCourseBtn");
const courseList = document.getElementById("courseList");

const taskCourseSelect = document.getElementById("taskCourse");
const taskTitleInput = document.getElementById("taskTitle");
const taskTypeSelect = document.getElementById("taskType");
const taskDateInput = document.getElementById("taskDate");
const taskTimeInput = document.getElementById("taskTime");
const taskPrioritySelect = document.getElementById("taskPriority");
const saveTaskBtn = document.getElementById("saveTaskBtn");

// ===== MONTH NAMES =====
const monthNames = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
];

// ===== SAVE DATA =====
function saveData() {
  localStorage.setItem("courses", JSON.stringify(courses));
  localStorage.setItem("tasks", JSON.stringify(tasks));
  localStorage.setItem("courseColors", JSON.stringify(courseColors));
}

// ===== HELPERS =====
function formatDate(year, month, day) {
  const mm = String(month + 1).padStart(2, "0");
  const dd = String(day).padStart(2, "0");
  return `${year}-${mm}-${dd}`;
}

function isOverdue(task) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const taskDate = new Date(task.dueDate + "T00:00:00");
  return !task.completed && taskDate < today;
}

function getTaskTypeClass(type) {
  if (type === "assignment") return "assignment";
  if (type === "quiz") return "quiz";
  if (type === "exam") return "exam";
  return "syllabus";
}

function getRandomSoftColor() {
  const colors = [
    "#dbeafe", "#dcfce7", "#fce7f3", "#fef3c7",
    "#e9d5ff", "#fde2e4", "#cffafe", "#ede9fe"
  ];
  return colors[Math.floor(Math.random() * colors.length)];
}

// ===== COURSE FUNCTIONS =====
function addCourse() {
  const courseName = courseNameInput.value.trim();

  if (courseName === "") {
    alert("Please enter a course name.");
    return;
  }

  if (courses.includes(courseName)) {
    alert("That course already exists.");
    return;
  }

  courses.push(courseName);
  courseColors[courseName] = getRandomSoftColor();

  courseNameInput.value = "";
  saveData();
  renderCourses();
  updateCourseDropdown();
  generateCalendar(currentMonth, currentYear);
}

function renderCourses() {
  courseList.innerHTML = "";

  if (courses.length === 0) {
    const li = document.createElement("li");
    li.textContent = "No courses added yet.";
    courseList.appendChild(li);
    return;
  }

  courses.forEach((course, index) => {
    const li = document.createElement("li");
    li.classList.add("course-item");

    const leftWrap = document.createElement("div");
    leftWrap.classList.add("course-left");

    const colorDot = document.createElement("span");
    colorDot.classList.add("course-dot");
    colorDot.style.background = courseColors[course] || "#ccc";

    const span = document.createElement("span");
    span.textContent = course;

    leftWrap.appendChild(colorDot);
    leftWrap.appendChild(span);

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.classList.add("small-btn");
    deleteBtn.addEventListener("click", () => deleteCourse(index));

    li.appendChild(leftWrap);
    li.appendChild(deleteBtn);
    courseList.appendChild(li);
  });
}

function updateCourseDropdown() {
  taskCourseSelect.innerHTML = '<option value="">Select Course</option>';

  courses.forEach(course => {
    const option = document.createElement("option");
    option.value = course;
    option.textContent = course;
    taskCourseSelect.appendChild(option);
  });
}

function deleteCourse(index) {
  const removedCourse = courses[index];
  courses.splice(index, 1);
  delete courseColors[removedCourse];

  tasks = tasks.filter(task => task.course !== removedCourse);

  saveData();
  renderCourses();
  updateCourseDropdown();
  generateCalendar(currentMonth, currentYear);
  renderSelectedDayTasks();
}

// ===== TASK FUNCTIONS =====
function addOrUpdateTask() {
  const course = taskCourseSelect.value;
  const title = taskTitleInput.value.trim();
  const type = taskTypeSelect.value;
  const dueDate = taskDateInput.value;
  const dueTime = taskTimeInput.value;
  const priority = taskPrioritySelect.value;

  if (!course || !title || !dueDate) {
    alert("Please fill in course, title, and due date.");
    return;
  }

  if (editingTaskId) {
    const task = tasks.find(t => t.id === editingTaskId);
    if (task) {
      task.course = course;
      task.title = title;
      task.type = type;
      task.dueDate = dueDate;
      task.dueTime = dueTime;
      task.priority = priority;
    }
    editingTaskId = null;
    saveTaskBtn.textContent = "Save Task";
  } else {
    const task = {
      id: Date.now(),
      course,
      title,
      type,
      dueDate,
      dueTime,
      priority,
      completed: false
    };
    tasks.push(task);
  }

  saveData();
  clearTaskForm();
  generateCalendar(currentMonth, currentYear);
  renderSelectedDayTasks();
}

function clearTaskForm() {
  taskCourseSelect.value = "";
  taskTitleInput.value = "";
  taskTypeSelect.value = "assignment";
  taskDateInput.value = "";
  taskTimeInput.value = "";
  taskPrioritySelect.value = "low";
}

function editTask(taskId) {
  const task = tasks.find(t => t.id === taskId);
  if (!task) return;

  taskCourseSelect.value = task.course;
  taskTitleInput.value = task.title;
  taskTypeSelect.value = task.type;
  taskDateInput.value = task.dueDate;
  taskTimeInput.value = task.dueTime || "";
  taskPrioritySelect.value = task.priority || "low";

  editingTaskId = task.id;
  saveTaskBtn.textContent = "Update Task";
}

function toggleTaskComplete(taskId) {
  const task = tasks.find(t => t.id === taskId);
  if (!task) return;

  task.completed = !task.completed;
  saveData();
  generateCalendar(currentMonth, currentYear);
  renderSelectedDayTasks();
}

function deleteTask(taskId) {
  tasks = tasks.filter(t => t.id !== taskId);
  saveData();
  generateCalendar(currentMonth, currentYear);
  renderSelectedDayTasks();
}

// ===== CALENDAR =====
function generateCalendar(month, year) {
  calendarGrid.innerHTML = "";
  currentMonthYear.textContent = `${monthNames[month]} ${year}`;

  const daysOfWeek = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  daysOfWeek.forEach(day => {
    const dayName = document.createElement("div");
    dayName.classList.add("day-name");
    dayName.textContent = day;
    calendarGrid.appendChild(dayName);
  });

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();

  for (let i = 0; i < firstDay; i++) {
    const emptyBox = document.createElement("div");
    emptyBox.classList.add("calendar-day", "empty");
    calendarGrid.appendChild(emptyBox);
  }

  for (let day = 1; day <= daysInMonth; day++) {
    const dayBox = document.createElement("div");
    dayBox.classList.add("calendar-day");

    const fullDate = formatDate(year, month, day);

    const dateNumber = document.createElement("div");
    dateNumber.classList.add("date-number");
    dateNumber.textContent = day;
    dayBox.appendChild(dateNumber);

    const dayTasks = tasks.filter(task => task.dueDate === fullDate);

    dayTasks.forEach(task => {
      const taskTag = document.createElement("div");
      taskTag.classList.add("task-tag");

      taskTag.style.background = courseColors[task.course] || "#dbeafe";

      if (task.completed) {
        taskTag.classList.add("completed-task");
      }

      if (isOverdue(task)) {
        taskTag.classList.add("overdue-task");
      }

      taskTag.textContent = task.title;
      dayBox.appendChild(taskTag);
    });

    dayBox.addEventListener("click", () => {
      selectedDay = fullDate;
      selectedDate.textContent = `${monthNames[month]} ${day}, ${year}`;
      renderSelectedDayTasks();
    });

    calendarGrid.appendChild(dayBox);
  }
}

function renderSelectedDayTasks() {
  taskDetailsList.innerHTML = "";

  if (!selectedDay) {
    taskDetailsList.innerHTML = "<p>No day selected.</p>";
    return;
  }

  const dayTasks = tasks.filter(task => task.dueDate === selectedDay);

  if (dayTasks.length === 0) {
    taskDetailsList.innerHTML = "<p>No tasks for this day yet.</p>";
    return;
  }

  dayTasks.forEach(task => {
    const card = document.createElement("div");
    card.classList.add("task-card");

    if (task.completed) {
      card.classList.add("task-complete");
    }

    if (isOverdue(task)) {
      card.classList.add("task-overdue");
    }

    const overdueText = isOverdue(task) ? " (Overdue)" : "";
    const timeText = task.dueTime ? task.dueTime : "No time set";

    card.innerHTML = `
      <h3>${task.title}${overdueText}</h3>
      <p><strong>Course:</strong> ${task.course}</p>
      <p><strong>Type:</strong> ${task.type}</p>
      <p><strong>Time:</strong> ${timeText}</p>
      <p><strong>Priority:</strong> ${task.priority}</p>
      <p><strong>Status:</strong> ${task.completed ? "Complete" : "Incomplete"}</p>
    `;

    const completeBtn = document.createElement("button");
    completeBtn.textContent = task.completed ? "Mark Incomplete" : "Mark Complete";
    completeBtn.classList.add("small-btn");
    completeBtn.addEventListener("click", () => toggleTaskComplete(task.id));

    const editBtn = document.createElement("button");
    editBtn.textContent = "Edit";
    editBtn.classList.add("small-btn");
    editBtn.addEventListener("click", () => editTask(task.id));

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.classList.add("small-btn", "delete-btn");
    deleteBtn.addEventListener("click", () => deleteTask(task.id));

    card.appendChild(completeBtn);
    card.appendChild(editBtn);
    card.appendChild(deleteBtn);

    taskDetailsList.appendChild(card);
  });
}

// ===== MONTH BUTTONS =====
prevMonthBtn.addEventListener("click", () => {
  currentMonth--;
  if (currentMonth < 0) {
    currentMonth = 11;
    currentYear--;
  }
  generateCalendar(currentMonth, currentYear);
});

nextMonthBtn.addEventListener("click", () => {
  currentMonth++;
  if (currentMonth > 11) {
    currentMonth = 0;
    currentYear++;
  }
  generateCalendar(currentMonth, currentYear);
});

// ===== DARK MODE =====
darkModeToggle.addEventListener("click", () => {
  document.body.classList.toggle("dark-mode");
});

// ===== BUTTON EVENTS =====
addCourseBtn.addEventListener("click", addCourse);
saveTaskBtn.addEventListener("click", addOrUpdateTask);

// ===== START =====
renderCourses();
updateCourseDropdown();
generateCalendar(currentMonth, currentYear);