
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class EmployeeDetails(
    val emp_id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
    val dept: String,
    val reportingTo: Int?
)

data class AttendanceEntry(
    val emp_id: Int,
    val checkInDate: String,
    val checkInTime: String
)

val employees: MutableList<EmployeeDetails> = mutableListOf()
val attendanceEntries : MutableList<AttendanceEntry> = mutableListOf()

class Employee{
            public fun isEmployeeExist(empId:Int ):Boolean{
                return employees.any{it.emp_id == empId}
            }
            fun addEmployee(employee : EmployeeDetails){
                if(!isEmployeeExist(employee.emp_id)){
                    employees.add(employee)
                    println("Employee with id ${employee.emp_id} has been added successfully")
                }
                else {
                    println("Employee with id ${employee.emp_id} is already existing")
                }
            }
            fun viewAllEmployees(){
                if(employees.isEmpty()){
                    println("No employees added")
                    return
                }
                employees.forEach{
                    println("empId=${it.emp_id}  firstName=${it.firstName} lastName=${it.lastName} role=${it.role} dept=${it.dept} reporotng to = ${it.reportingTo ?:"N/A" }")
                }
            }
}

class Attendance{
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val emp=Employee()
    fun isCheckedIn(empId:Int):Boolean{
        val todayDate= LocalDate.now().format(dateFormatter)
        return attendanceEntries.any{ it.emp_id==empId && it.checkInDate==todayDate}
    }

    fun isCheckedIn(empId:Int , checkInDate:String):Boolean{
        return attendanceEntries.any{ it.emp_id==empId && it.checkInDate==checkInDate}
    }

    fun checkInEmployee(empId:Int){
        if(!emp.isEmployeeExist(empId)){
            println("Employee does not exist")

        }
        else {
            if(isCheckedIn(empId)){
                println("You have already checked In")
            }
            else {
                val currCheckInDate=LocalDate.now().format(dateFormatter)
                val currCheckInTime=LocalTime.now().format(timeFormatter)
                val newEntry=AttendanceEntry(empId,currCheckInDate,currCheckInTime)
                attendanceEntries.add(newEntry)
                println("you have succesfully checkedIn")
            }
        }
    }

    fun checkInEmployee(empId:Int,checkInDate:String,checkInTime:String){
        if(!emp.isEmployeeExist(empId)){
            println("Employee does not exist")
            return
        }
        try {
            // Parse with the correct formatters (dd-MM-yyyy and HH:mm:ss)
            LocalDate.parse(checkInDate, dateFormatter)
            LocalTime.parse(checkInTime, timeFormatter)
        } catch (e: Exception) {
            println("Check-in failed: Invalid date ($checkInDate) or time ($checkInTime) format. Use dd-MM-yyyy for date and HH:mm for time.")

        }

        if(isCheckedIn(empId,checkInDate)){
            println("you have already checkedIn")

        }
        else{
            val entry=AttendanceEntry(empId,checkInDate,checkInTime)
            attendanceEntries.add(entry)
            println("you have been checkedIn successfully ")
        }
    }

    fun viewAllEntries(){
        if(attendanceEntries.isEmpty()){
            println("No Attendance entry recorded")
            return
        }
        else {
            attendanceEntries.forEach{
                println("empId: ${it.emp_id}  checkedIn Date : ${it.checkInDate} CheckedIn Time: ${it.checkInTime}")
            }
        }
    }

}

fun main() {
    val emp = Employee()
    val attendance = Attendance()


    emp.addEmployee(EmployeeDetails(101, "Alice", "Smith", "Dev", "IT", 100))
    emp.addEmployee(EmployeeDetails(102, "Bob", "Johnson", "QA", "IT", 101))
    emp.addEmployee(EmployeeDetails(103, "Charlie", "Brown", "HR", "Admin", null))
    emp.addEmployee(EmployeeDetails(104, "Diana", "Prince", "Manager", "IT", 100))
    emp.addEmployee(EmployeeDetails(105, "Eve", "Adams", "Support", "Customer Service", 104))

    var flag: Boolean = true
    while (flag) {
        println("CheckIn Option: 1.addEmployee\n 2.checkIn with EmpId\n 3.checkIn with EmpId,CustomDate and time(Date format:dd/MM/yyy  Time format:HH:mm)\n 4.view all employees\n 5.view all entries\n 6.exit")
        println("Enter your option:")
        val op = readln().toInt()
        when (op) {
            1 -> {
                println("Enter employee ID:")
                val empId = readln().toInt()
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
                val empId = readln().toInt()
                attendance.checkInEmployee(empId)
            }

            3 -> {
                println("Enter emp Id")
                val empId = readln().toInt()
                println("Enter Date:(Format dd/MM/yyyy)")
                val checkInDAte = readln()
                println("Enter checkInTime:(Format HH:mm)")
                val checkInTime = readln()
                attendance.checkInEmployee(empId, checkInDAte, checkInTime)
            }

            4 -> {
                emp.viewAllEmployees()
            }

            5 -> {
                attendance.viewAllEntries()
            }

            6 -> {
                flag = false
            }

            else -> {
                println("Invalid option. Please enter a valid option ")
            }
        }
    }
}



