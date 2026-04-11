// DATA
let courses = [];
let tasks = [];

// DATE SETUP
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

// ELEMENTS
const calendarGrid = document.getElementById("calendarGrid");
const currentMonthYear = document.getElementById("currentMonthYear");
const prevMonthBtn = document.getElementById("prevMonthBtn");
const nextMonthBtn = document.getElementById("nextMonthBtn");
const selectedDate = document.getElementById("selectedDate");
const taskDetailsList = document.getElementById("taskDetailsList");
const darkModeToggle = document.getElementById("darkModeToggle");

// ===== MONTH NAMES =====
const monthNames = [
    "January","February","March","April","May","June",
    "July","August","September","October","November","December"
];

// GENERATE CALENDAR
function generateCalendar(month, year) {
    calendarGrid.innerHTML = "";

    currentMonthYear.textContent = `${monthNames[month]} ${year}`;

    const daysOfWeek = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

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

        const dateNumber = document.createElement("div");
        dateNumber.classList.add("date-number");
        dateNumber.textContent = day;

        dayBox.appendChild(dateNumber);

        dayBox.addEventListener("click", () => {
            selectedDate.textContent = `${monthNames[month]} ${day}, ${year}`;
            taskDetailsList.innerHTML = "<p>No tasks for this day yet.</p>";
        });

        calendarGrid.appendChild(dayBox);
    }
}

// MONTH BUTTONS
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

// DARK MODE
darkModeToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark-mode");
});

// START PROGRAM
generateCalendar(currentMonth, currentYear);