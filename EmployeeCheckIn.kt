
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter

data class EmployeeDetails(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val dept: String,
    val reportingTo: Int?
)

data class AttendanceEntry(
    val empId:Int,
    val checkInTime: LocalTime,
    val checkInDate: LocalDate,
    var checkOutTime: LocalTime?=null,
    var workingHours: Duration?=null
)

val employees: MutableList<EmployeeDetails> = mutableListOf()
val attendanceEntries : MutableList<AttendanceEntry> = mutableListOf()
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
        return employees.any { it.id == empId }
    }
    fun addEmployee(employee: EmployeeDetails) {
        if (!isEmployeeExist(employee.id)) {
            employees.add(employee)
            println("Employee with id ${employee.id} has been added successfully")
        } else {
            println("Employee with id ${employee.id} is already existing")
        }
    }
    fun viewAllEmployees() {
        if (employees.isEmpty()) {
            println("No employees added")
            return
        }
        employees.forEach {
            println("empId=${it.id}  firstName=${it.firstName} lastName=${it.lastName} role=${it.role} dept=${it.dept} reporting to = ${it.reportingTo ?: "N/A"}")
        }
    }
}

    class Attendance {

        fun isCheckedIn(empId: Int): Boolean {
            val todayDate = LocalDate.now()
            return attendanceEntries.any { it.empId == empId && it.checkInDate == todayDate && it.checkOutTime==null}
        }

        fun isCheckedIn(empId: Int, checkInDate: LocalDate): Boolean {
            return attendanceEntries.any { it.empId == empId && it.checkInDate == checkInDate && it.checkOutTime==null}
        }

        fun isValidDate(date: LocalDate): Boolean {
            val today = LocalDate.now()
            if (date.isAfter(today)) {
                return false
            }
            return true
        }

        fun checkInEmployee(empId: Int) :Boolean{
                if (isCheckedIn(empId)) {
                    return false
                } else {
                    val currCheckInDate = LocalDate.now()
                    val currCheckInTime = LocalTime.now()
                    val newEntry = AttendanceEntry(empId, currCheckInTime, currCheckInDate)
                    attendanceEntries.add(newEntry)
                    return true
                }
        }

        fun checkInEmployee(empId: Int, checkInDate: LocalDate, checkInTime: LocalTime):Boolean {

            if (isCheckedIn(empId, checkInDate)) {
                return false
            } else {
                val entry = AttendanceEntry(empId, checkInTime, checkInDate)
                attendanceEntries.add(entry)
                return true
            }
        }

        fun isCheckedOut(empId: Int): Boolean {
            val todayDate = LocalDate.now()
            return attendanceEntries.any { it.empId == empId && it.checkInDate == todayDate && it.checkOutTime!=null}
        }

        fun isCheckedOut(empId:Int,checkOutDate:LocalDate):Boolean {
            return attendanceEntries.any{it.empId==empId && it.checkInDate==checkOutDate && it.checkOutTime!=null}
        }

        fun checkOutEmployee(empId: Int) :Boolean {
            val checkOutDate = LocalDate.now()
            val checkOutTime = LocalTime.now()
            val cIn = attendanceEntries.find { it.empId == empId && it.checkInDate == checkOutDate  && it.checkOutTime == null}
            if (cIn != null) {
                val cInTime = cIn.checkInTime
                val workingHour = Duration.between(cInTime, checkOutTime)
                cIn.checkOutTime=checkOutTime
                cIn.workingHours=workingHour
                return true

            }
            return false
        }



        fun checkOutEmployee(empId: Int, checkOutDate: LocalDate, checkOutTime: LocalTime):Boolean {
                val cIn = attendanceEntries.find { it.empId == empId && it.checkInDate == checkOutDate && it.checkOutTime==null}
                if (cIn != null) {
                    val checkInTime = cIn.checkInTime
                    val workingHour = Duration.between(checkInTime, checkOutTime)
                   cIn.checkOutTime=checkOutTime
                    cIn.workingHours=workingHour
                   return true
                }
                return false

            }


        fun viewAllEntries(){
            if (attendanceEntries.isEmpty()) {
                println("No Attendance entry recorded")
                return
            } else {
                attendanceEntries.forEach {
                    println("empId: ${it.empId}  checkedIn Date : ${it.checkInDate} CheckedIn Time: ${it.checkInTime} CheckedOut Time:${it.checkOutTime} Working Hours:${it.workingHours}")
                }
            }
        }
}
    fun main() {
        val emp = Employee()
        val attendance = Attendance()
        val input = Input()

        emp.addEmployee(EmployeeDetails(101, "Alice", "Smith", "Dev", "IT", 103))
        emp.addEmployee(EmployeeDetails(102, "Bob", "Johnson", "QA", "IT", 101))
        emp.addEmployee(EmployeeDetails(103, "Charlie", "Brown", "HR", "Admin", null))
        emp.addEmployee(EmployeeDetails(104, "Diana", "Prince", "Manager", "IT", 103))
        emp.addEmployee(EmployeeDetails(105, "Eve", "Adams", "Support", "Customer Service", 104))

        var flag: Boolean = true
        while (flag) {
            println("CheckIn Option:\n1.addEmployee\n2.checkIn with EmpId\n3.checkIn with EmpId,CustomDate and time(Date format:dd-MM-yyy  Time format:HH:mm)\n4.checkOut\n5.checkOut with custom Date and time\n6.view all employees\n7.view all entries\n8.exit")
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
                    if(!emp.isEmployeeExist(empId)){
                        println("CheckIn failed:Employee with id ${empId} does not exist")
                    }else {
                        val isCheckedIn = attendance.checkInEmployee(empId)
                        if(isCheckedIn){
                            println("You have been CheckedIn Succesfully ")
                        }
                        else{
                            println("CheckIn failed:You have already been checkIn for the day")
                        }
                    }
                }

                3 -> {
                    println("Enter emp Id")
                    val empId = input.getId()
                    if (!emp.isEmployeeExist(empId)) {
                        println("Check In failed:Employee with id ${empId} does not exist")
                    } else {
                        println("Enter Date (Format dd-MM-yyyy):")
                        val date = input.getDate()

                        println("Enter Time (Format HH:mm):")
                        val time = input.getTime()

                        if (!attendance.isValidDate(date)) {
                            println("CheckIn failed: You cannot checkIn for the future")
                        } else if (attendance.isCheckedIn(empId, date)) {
                            println("check In failed:You have already checked In for the day")
                        } else {
                            val isCheckedIn = attendance.checkInEmployee(empId, date, time)
                            if (isCheckedIn) {
                                println("Checked In successfully")
                            }
                        }

                    }
                }

                4 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    if(!emp.isEmployeeExist(empId)){
                        println("Check Out failed:Employee with id ${empId} does not exist")
                    }
                    else if(!attendance.isCheckedIn(empId)){
                        println("CheckOut failed :  Cannot checkout without checking in for today")
                    }
                    else if(attendance.isCheckedOut(empId)){
                        println("CheckOut Failed: You have already checked out for the day")
                    }
                    else {
                        val isCheckedOut = attendance.checkOutEmployee(empId)
                        if(isCheckedOut){
                            println("You have been checkedOut successfuly.")
                        }
                    }
                }

                5 -> {
                    println("Enter employee Id")
                    val empId = input.getId()
                    if (!emp.isEmployeeExist(empId)) {
                        println("Check Out failed:Employee with id ${empId} does not exist")
                    }
                    else {
                        println("Enter the Date in the format 'dd-MM-yyyy'")
                        val date = input.getDate()
                        println("Enter the Time in the format 'HH:mm")
                        val time = input.getTime()
                        if (!attendance.isValidDate(date)) {
                            println("CheckOut failed: You cannot checkout for the future")
                        }
                        else if(!attendance.isCheckedIn(empId,date)){
                            println("CheckOut failed :  Cannot checkout without checking in ")
                        }
                        else if (attendance.isCheckedOut(empId, date)) {
                            println("CheckOut failed:You have already checkedOut for the day ")
                        } else {
                            val isCheckOut = attendance.checkOutEmployee(empId, date, time)
                            if (isCheckOut) {
                                println("You have been checkedOut successfuly.")
                            }
                        }
                    }
                }

                6 -> {
                    emp.viewAllEmployees()
                }

                7 -> {
                    attendance.viewAllEntries()
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

