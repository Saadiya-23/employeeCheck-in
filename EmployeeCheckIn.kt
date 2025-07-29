
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter

data class EmployeeDetails(
    val emp_id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val dept: String,
    val reportingTo: Int?
)

data class CheckIn(
    val emp_id: Int,
    val checkInDate: LocalDate,
    val checkInTime: LocalTime
)
data class CheckOut(
    val emp_id:Int,
    val checkOutDate: LocalDate,
    val checkOutTime: LocalTime
)
data class AttendanceEntry(
    val emp_id:Int,
    val checkInTime: LocalTime,
    val checkOutTime: LocalTime,
    val checkOutDate: LocalDate,
    val workingHours: Duration
)

var employees: MutableList<EmployeeDetails> = mutableListOf()
var attendanceEntries : MutableList<AttendanceEntry> = mutableListOf()
val checkInList : MutableList<CheckIn> = mutableListOf()
val checkOutList : MutableList<CheckOut> = mutableListOf()
class Input {
    fun getId(): Int {
        val id = readln().toInt()
        return id
    }

    fun getDate(): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        while (true) {
            val dateStr = readln()
            try {
                return LocalDate.parse(dateStr, dateFormatter)
            } catch (e: Exception) {
                println("Invalid format. Please use dd-MM-yyyy for date ")
            }
        }
    }


    fun getTime(): LocalTime {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        while (true) {
            val timeStr = readln()
            try {
                return LocalTime.parse(timeStr, timeFormatter)
            } catch (e: Exception) {
                println("Invalid format : Please use HH:mm format ")
            }
        }
    }
}
    class Employee {
        fun isEmployeeExist(empId: Int): Boolean {
            return employees.any { it.emp_id == empId }
        }

        fun addEmployee(employee: EmployeeDetails) {
            if (!isEmployeeExist(employee.emp_id)) {
                employees.add(employee)
                println("Employee with id ${employee.emp_id} has been added successfully")
            } else {
                println("Employee with id ${employee.emp_id} is already existing")
            }
        }

        fun viewAllEmployees() {
            if (employees.isEmpty()) {
                println("No employees added")
                return
            }
            employees.forEach {
                println("empId=${it.emp_id}  firstName=${it.firstName} lastName=${it.lastName} role=${it.role} dept=${it.dept} reporting to = ${it.reportingTo ?: "N/A"}")
            }
        }
    }

    class Attendance {

        val emp = Employee()
        fun isCheckedIn(empId: Int): Boolean {
            val todayDate = LocalDate.now()
            return checkInList.any { it.emp_id == empId && it.checkInDate == todayDate }
        }

        fun isCheckedIn(empId: Int, checkInDate: LocalDate): Boolean {
            return checkInList.any { it.emp_id == empId && it.checkInDate == checkInDate }
        }

        fun isValidDate(date: LocalDate): Boolean {
            val today = LocalDate.now()
            if (date.isAfter(today)) {
                return false
            }
            return true
        }

        fun checkInEmployee(empId: Int) {
            if (!emp.isEmployeeExist(empId)) {
                println("Employee does not exist")

            } else {
                if (isCheckedIn(empId)) {
                    println("You have already checked In")
                } else {
                    val currCheckInDate = LocalDate.now()
                    val currCheckInTime = LocalTime.now()
                    val newEntry = CheckIn(empId, currCheckInDate, currCheckInTime)
                    checkInList.add(newEntry)
                    println("you have succesfully checkedIn")
                }
            }
        }

        fun checkInEmployee(empId: Int, checkInDate: LocalDate, checkInTime: LocalTime) {
            if (!emp.isEmployeeExist(empId)) {
                println("Employee does not exist")
                return
            }
            if (!isValidDate(checkInDate)) {
                println("Invalid checkIn. You cannot checkIn for the future")
                return
            }
            if (isCheckedIn(empId, checkInDate)) {
                println("you have already checkedIn")
            } else {
                val entry = CheckIn(empId, checkInDate, checkInTime)
                checkInList.add(entry)
                println("you have been checkedIn successfully ")
            }
        }

        fun isCheckOut(empId: Int): Boolean {
            val todayDate = LocalDate.now()
            return checkOutList.any { it.emp_id == empId && it.checkOutDate == todayDate }
        }

        fun checkOutEmployee(empId: Int) {
            val emp = Employee()
            if (!emp.isEmployeeExist(empId)) {
                println("Invalid User")
                return
            } else {
                val cOutDate = LocalDate.now()
                val cOutTime = LocalTime.now()
                val cIn = checkInList.find { it.emp_id == empId && it.checkInDate == cOutDate }
                if (cIn != null) {
                    val cInTime = cIn.checkInTime
                    val workingHour= Duration.between(cInTime, cOutTime)
                    val entry = CheckOut(empId, cOutDate, cOutTime)
                    val newEntry =
                        AttendanceEntry(empId, cInTime, cOutTime, cOutDate,workingHour)
                    checkOutList.add(entry)
                    attendanceEntries.add(newEntry)
                    println("You have been checkedOut successfuly.Working hours:${workingHour}")

                } else {
                    println("Invalid CheckOut.You have not Checked In")
                }

            }
        }


        fun checkOutEmployee(empId: Int, cOutTime: LocalTime, cOutDate: LocalDate) {
            val emp = Employee()
            if (!isValidDate(cOutDate)) {
                println("Invalid checkOut. You cannot checkout for the future")
                return
            }
            if (!emp.isEmployeeExist(empId)) {
                println("Invalid User")
            } else {
                val cIn = checkInList.find { it.emp_id == empId && it.checkInDate == cOutDate }
                if (cIn != null) {
                    val cInTime = cIn.checkInTime
                    val workingHour = Duration.between(cInTime, cOutTime)
                    val entry = CheckOut(empId, cOutDate, cOutTime)
                    val newEntry = AttendanceEntry(
                        empId,
                        cInTime,
                        cOutTime,
                        cOutDate,
                        workingHour
                    )
                    checkOutList.add(entry)
                    attendanceEntries.add(newEntry)
                    println("you have been checked out . Working hours: ${workingHour}")
                } else {
                    println("Invalid checkOut. You hav not checkedIn")
                }
            }
        }

        fun viewAllEntries() {
            if (attendanceEntries.isEmpty()) {
                println("No Attendance entry recorded")
                return
            } else {
                attendanceEntries.forEach {
                    println("empId: ${it.emp_id}  checkedIn Date : ${it.checkOutDate} CheckedIn Time: ${it.checkInTime} CheckedOut Time:${it.checkOutTime} Working Hours:${it.workingHours}")
                }
            }
        }

    }

    fun main() {
        val emp = Employee()
        val attendance = Attendance()
        val input = Input()

        emp.addEmployee(EmployeeDetails(101, "Alice", "Smith", "Dev", "IT", 100))
        emp.addEmployee(EmployeeDetails(102, "Bob", "Johnson", "QA", "IT", 101))
        emp.addEmployee(EmployeeDetails(103, "Charlie", "Brown", "HR", "Admin", null))
        emp.addEmployee(EmployeeDetails(104, "Diana", "Prince", "Manager", "IT", 100))
        emp.addEmployee(EmployeeDetails(105, "Eve", "Adams", "Support", "Customer Service", 104))

        var flag: Boolean = true
        while (flag) {
            println("CheckIn Option: 1.addEmployee\n 2.checkIn with EmpId\n 3.checkIn with EmpId,CustomDate and time(Date format:dd/MM/yyy  Time format:HH:mm)\n 4.view all employees\n 5.view all entries\n 6.checkOut\n 7.checkOut with custom Date and time\n 8.exit")
            println("Enter your option:")
            val op = readln().toInt()
            when (op) {
                1 -> {
                    println("Enter employee ID:")
                    val empId = input.getId()
                    println("Enter employee first name:")
                    val firstName = readln()
                    println("Enter employee last name:")
                    val lastName = readln()
                    println("Enter employee role:")
                    val role = readln()
                    println("Enter employee department:")
                    val dept = readln()
                    println("Enter reporting to ID (or leave blank if none):")
                    val reportingToInput = readln()
                    val reportingTo: Int? = if (reportingToInput.isNotBlank()) reportingToInput.toIntOrNull() else null
                    val newEmployee = EmployeeDetails(empId, firstName, lastName, role, dept, reportingTo)
                    emp.addEmployee(newEmployee)
                    println("Employee details collected for ${firstName} ${lastName}.")
                }

                2 -> {
                    println("Enter empId")
                    val empId = input.getId()
                    attendance.checkInEmployee(empId)
                }

                3 -> {
                    println("Enter emp Id")
                    val empId = input.getId()

                    println("Enter Date (Format dd-MM-yyyy):")
                    val date = input.getDate()

                    println("Enter Time (Format HH:mm):")
                    val time = input.getTime()

                    attendance.checkInEmployee(empId,date,time)

                }

                4 -> {
                    emp.viewAllEmployees()
                }

                5 -> {
                    attendance.viewAllEntries()
                }

                6 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    attendance.checkOutEmployee(empId)
                }

                7 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    println("Enter the Date in the format 'dd-MM-yyyy'")
                    val date= input.getDate()
                    println("Enter the Time in the format 'HH:mm")
                    val time=input.getTime()
                    attendance.checkOutEmployee(empId,time,date)
                }

                8 -> {
                    flag = false
                }

                else -> {
                    println("Invalid option. Please enter a valid option ")
                }
            }
        }
    }

